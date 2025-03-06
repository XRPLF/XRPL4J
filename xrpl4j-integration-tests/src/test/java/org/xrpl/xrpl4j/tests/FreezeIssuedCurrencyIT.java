package org.xrpl.xrpl4j.tests;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: integration-tests
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.xrpl.xrpl4j.model.transactions.TransactionResultCodes.TEC_PATH_DRY;
import static org.xrpl.xrpl4j.model.transactions.TransactionResultCodes.TES_SUCCESS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.TrustLine;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.flags.TrustSetFlags;
import org.xrpl.xrpl4j.model.flags.TrustSetFlags.Builder;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.AccountSet.AccountSetFlag;
import org.xrpl.xrpl4j.model.transactions.ImmutableAccountSet;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

/**
 * An Integration Test to validate that an amount of issued currency held by a bad actor can be "frozen."
 */
public class FreezeIssuedCurrencyIT extends AbstractIT {

  private static final String TEN_THOUSAND = "10000";
  private static final String FIVE_THOUSAND = "5000";
  private static final String ISSUED_CURRENCY_CODE = Strings.padEnd(
    BaseEncoding.base16().encode("usd".getBytes()), 40, '0'
  );

  private KeyPair issuerKeyPair;
  private KeyPair badActorKeyPair;
  private KeyPair goodActorKeyPair;

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  @BeforeEach
  public void setUp() throws JsonRpcClientErrorException, JsonProcessingException {

    // Create and fund random accounts for this harness.
    issuerKeyPair = this.createRandomAccountEd25519();
    // This is necessary for non-issuers to be able to send money to other non-issuers.
    this.enableDefaultRipple(issuerKeyPair);

    badActorKeyPair = this.createRandomAccountEd25519();
    goodActorKeyPair = this.createRandomAccountEd25519();
  }

  /**
   * This test creates a Trustline between an issuer and a badActor, issues funds to the badActor, then freezes the
   * funds and validates that the badActor is unable to use those funds.
   *
   * @see "https://xrpl.org/freezes.html#individual-freeze"
   */
  @Test
  public void issueAndFreezeFundsIndividual() throws JsonRpcClientErrorException, JsonProcessingException {
    FeeResult feeResult = xrplClient.fee();

    // Create a Trust Line between issuer and the bad actor.
    TrustLine badActorTrustLine = createTrustLine(
      badActorKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(ISSUED_CURRENCY_CODE)
        .value(TEN_THOUSAND)
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );
    assertThat(badActorTrustLine.freeze()).isFalse();
    assertThat(badActorTrustLine.freezePeer()).isFalse();
    assertThat(badActorTrustLine.noRipple()).isFalse();
    assertThat(badActorTrustLine.noRipplePeer()).isTrue();

    ///////////////////////////
    // Create a Trust Line between issuer and the good actor.
    TrustLine goodActorTrustLine = this.createTrustLine(
      goodActorKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(ISSUED_CURRENCY_CODE)
        .value(TEN_THOUSAND)
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );
    assertThat(goodActorTrustLine.freeze()).isFalse();
    assertThat(goodActorTrustLine.freezePeer()).isFalse();
    assertThat(goodActorTrustLine.noRipple()).isFalse();
    assertThat(goodActorTrustLine.noRipplePeer()).isTrue();

    /////////////
    // Send Funds
    /////////////

    logger.info("Send ${} from issuer ({}) to the badActor ({}) and expect {}",
      TEN_THOUSAND,
      issuerKeyPair.publicKey().deriveAddress(),
      goodActorKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );

    sendIssuedCurrency(
      issuerKeyPair, // <-- From
      badActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(TEN_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    ///////////////////////////
    // Validate that the TrustLine balance was updated as a result of the Payment.
    // The trust line returned is from the perspective of the issuer, so the balance should be negative.
    this.scanForResult(() -> getValidatedAccountLines(issuerKeyPair.publicKey().deriveAddress(),
        badActorKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("-" + TEN_THOUSAND))
    );

    logger.info("Send ${} from badActor ({}) to the goodActor ({}) and expect {}",
      FIVE_THOUSAND,
      badActorKeyPair.publicKey().deriveAddress(),
      goodActorKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );

    sendIssuedCurrency(
      badActorKeyPair, // <-- From
      goodActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Validate that the TrustLine balance was updated as a result of the Payment.
    // The trust line returned is from the perspective of the issuer, so the balance should be negative.
    this.scanForResult(() -> getValidatedAccountLines(issuerKeyPair.publicKey().deriveAddress(),
        goodActorKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("-" + FIVE_THOUSAND))
    );

    // Individual-Freeze the trustline between the issuer and bad actor.
    badActorTrustLine = this.adjustTrustlineFreeze(
      issuerKeyPair,
      badActorKeyPair,
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      FREEZE
    );
    assertThat(badActorTrustLine.freeze()).isTrue();
    assertThat(badActorTrustLine.freezePeer()).isFalse();
    assertThat(badActorTrustLine.noRipple()).isFalse();
    assertThat(badActorTrustLine.noRipplePeer()).isTrue();

    /////////////
    // Assertions
    /////////////

    // 1) Payments can still occur directly between the two parties of the frozen trust line.
    // 2) The counterparty can only send the frozen currencies directly to the issuer (no where else)
    // 3) The counterparty can still receive payments from others on the frozen trust line.

    logger.info("Send ${} from badActor ({}) to the goodActor ({}) and expect {}",
      FIVE_THOUSAND,
      badActorKeyPair.publicKey().deriveAddress(),
      goodActorKeyPair.publicKey().deriveAddress(),
      TEC_PATH_DRY
    );

    sendIssuedCurrency(
      badActorKeyPair, // <-- From
      goodActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TEC_PATH_DRY
    );

    // Sending from the badActor to the issuer should still work
    logger.info("Send ${} from badActor ({}) to the issuer ({}) and expect {}",
      FIVE_THOUSAND,
      badActorKeyPair.publicKey().deriveAddress(),
      issuerKeyPair.publicKey().deriveAddress(),
      TEC_PATH_DRY
    );

    sendIssuedCurrency(
      badActorKeyPair, // <-- From
      issuerKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TEC_PATH_DRY
    );

    // Sending from the goodActor to the badActor should still work
    logger.info("Send ${} from goodActor ({}) to the badActor ({}) and expect {}",
      FIVE_THOUSAND,
      goodActorKeyPair.publicKey().deriveAddress(),
      badActorKeyPair.publicKey().deriveAddress(),
      TEC_PATH_DRY
    );
    sendIssuedCurrency(
      goodActorKeyPair, // <-- From
      badActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TEC_PATH_DRY
    );

    // Unfreeze the bad actor.
    badActorTrustLine = this.adjustTrustlineFreeze(
      issuerKeyPair,
      badActorKeyPair,
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      UN_FREEZE
    );
    assertThat(badActorTrustLine.freeze()).isTrue();
    assertThat(badActorTrustLine.freezePeer()).isFalse();
    assertThat(badActorTrustLine.noRipple()).isFalse();
    assertThat(badActorTrustLine.noRipplePeer()).isTrue();
  }

  /**
   * This test creates a Trustline between an issuer and a badActor, issues funds to two counterparties, then globally
   * freezes the trustlines for the issuer. The test validates that neither the good nor the bad actor is able to send
   * funds, except back to the issuer.
   *
   * @see "https://xrpl.org/freezes.html#global-freeze"
   */
  @Test
  public void issueAndFreezeFundsGlobal() throws JsonRpcClientErrorException, JsonProcessingException {
    FeeResult feeResult = xrplClient.fee();

    // Create a Trust Line between issuer and the bad actor.
    TrustLine badActorTrustLine = this.createTrustLine(
      badActorKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(ISSUED_CURRENCY_CODE)
        .value(TEN_THOUSAND)
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );
    assertThat(badActorTrustLine.freeze()).isFalse();
    assertThat(badActorTrustLine.freezePeer()).isFalse();
    assertThat(badActorTrustLine.noRipple()).isFalse();
    assertThat(badActorTrustLine.noRipplePeer()).isTrue();

    ///////////////////////////
    // Create a Trust Line between issuer and the good actor.
    TrustLine goodActorTrustLine = this.createTrustLine(
      goodActorKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(ISSUED_CURRENCY_CODE)
        .value(TEN_THOUSAND)
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );
    assertThat(goodActorTrustLine.freeze()).isFalse();
    assertThat(goodActorTrustLine.freezePeer()).isFalse();
    assertThat(goodActorTrustLine.noRipple()).isFalse();
    assertThat(goodActorTrustLine.noRipplePeer()).isTrue();

    /////////////
    // Send Funds
    /////////////

    logger.info("Send ${} from issuer ({}) to the badActor ({}) and expect {}",
      TEN_THOUSAND,
      issuerKeyPair.publicKey().deriveAddress(),
      badActorKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );
    sendIssuedCurrency(
      issuerKeyPair, // <-- From
      badActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(TEN_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    ///////////////////////////
    // Validate that the TrustLine balance was updated as a result of the Payment.
    // The trust line returned is from the perspective of the issuer, so the balance should be negative.
    this.scanForResult(() -> getValidatedAccountLines(issuerKeyPair.publicKey().deriveAddress(),
        badActorKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("-" + TEN_THOUSAND))
    );

    logger.info("Send ${} from badActor ({}) to the goodActor ({}) and expect {}",
      FIVE_THOUSAND,
      badActorKeyPair.publicKey().deriveAddress(),
      goodActorKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );
    sendIssuedCurrency(
      badActorKeyPair, // <-- From
      goodActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    ///////////////////////////
    // Validate that the TrustLine balance was updated as a result of the Payment.
    // The trust line returned is from the perspective of the issuer, so the balance should be negative.
    this.scanForResult(() -> getValidatedAccountLines(
        issuerKeyPair.publicKey().deriveAddress(), goodActorKeyPair.publicKey().deriveAddress()
      ),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("-" + FIVE_THOUSAND))
    );

    logger.info("Globally freeze trustline for issuer ({})", issuerKeyPair.publicKey().deriveAddress());

    // Global-Freeze the trustline for the issuer.
    AccountInfoResult issuerAccountInfo = this.adjustGlobalTrustlineFreeze(
      issuerKeyPair,
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      FREEZE
    );
    assertThat(issuerAccountInfo.accountData().flags().lsfGlobalFreeze()).isTrue();

    /////////////
    // Assertions
    /////////////

    // 1) The counterparty can only send the frozen currencies directly to the issuer (no where else)
    // 2) The counterparty can still receive payments from others on the frozen trust line.

    logger.info("Send ${} from badActor ({}) to the goodActor ({}) and expect {}",
      "500",
      badActorKeyPair.publicKey().deriveAddress(),
      goodActorKeyPair.publicKey().deriveAddress(),
      TEC_PATH_DRY
    );
    sendIssuedCurrency(
      badActorKeyPair, // <-- From
      goodActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value("500")
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TEC_PATH_DRY
    );

    logger.info("Send ${} from goodActor ({}) to the badActor ({}) and expect {}",
      "500",
      goodActorKeyPair.publicKey().deriveAddress(),
      badActorKeyPair.publicKey().deriveAddress(),
      TEC_PATH_DRY
    );
    sendIssuedCurrency(
      goodActorKeyPair, // <-- From
      badActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value("500")
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TEC_PATH_DRY
    );

    // Note: The following should work per https://xrpl.org/enact-global-freeze.html#intermission-while-frozen).
    logger.info("Send ${} from issuer ({}) to the goodActor ({}) and expect {}",
      "100",
      issuerKeyPair.publicKey().deriveAddress(),
      goodActorKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );
    sendIssuedCurrency(
      issuerKeyPair, // <-- From
      goodActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value("100")
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    // Note: The following should work per https://xrpl.org/enact-global-freeze.html#intermission-while-frozen).
    logger.info("Send ${} from issuer ({}) to the badActor ({}) and expect {}",
      "100",
      issuerKeyPair.publicKey().deriveAddress(),
      badActorKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );
    sendIssuedCurrency(
      issuerKeyPair, // <-- From
      badActorKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value("100")
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    // Note: The following should work per https://xrpl.org/enact-global-freeze.html#intermission-while-frozen).
    logger.info("Send ${} from issuer ({}) to the badActor ({}) and expect {}",
      FIVE_THOUSAND,
      badActorKeyPair.publicKey().deriveAddress(),
      issuerKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );
    sendIssuedCurrency(
      badActorKeyPair, // <-- From
      issuerKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    // Note: The following should work per https://xrpl.org/enact-global-freeze.html#intermission-while-frozen).
    logger.info("Send ${} from issuer ({}) to the goodActor ({}) and expect {}",
      "FIVE_THOUSAND",
      goodActorKeyPair.publicKey().deriveAddress(),
      issuerKeyPair.publicKey().deriveAddress(),
      TES_SUCCESS
    );
    sendIssuedCurrency(
      goodActorKeyPair, // <-- From
      issuerKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .value(FIVE_THOUSAND)
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TES_SUCCESS
    );

    // Unfreeze the bad actor.
    issuerAccountInfo = this.adjustGlobalTrustlineFreeze(
      issuerKeyPair,
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      UN_FREEZE
    );
    assertThat(issuerAccountInfo.accountData().flags().lsfGlobalFreeze()).isFalse();
  }

  /**
   * Set the `asfRequireAuth` AccountSet flag so that only approved counterparties can hold currency from the issuer.
   *
   * @throws JsonRpcClientErrorException If anything goes wrong while communicating with rippled.
   * @throws JsonProcessingException     If there are any problems parsing JSON.
   * @see "https://xrpl.org/become-an-xrp-ledger-gateway.html#default-ripple"
   */
  protected void enableDefaultRipple(final KeyPair wallet)
    throws JsonRpcClientErrorException, JsonProcessingException {
    FeeResult feeResult = xrplClient.fee();

    AccountInfoResult issuerKeyPairAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(wallet.publicKey().deriveAddress())
    );

    AccountSet accountSet = AccountSet.builder()
      .sequence(issuerKeyPairAccountInfo.accountData().sequence())
      .account(wallet.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .setFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .signingPublicKey(wallet.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedTrustSet = signatureService.sign(wallet.privateKey(), accountSet);
    SubmitResult<AccountSet> trustSetSubmitResult = xrplClient.submit(signedTrustSet);
    assertThat(trustSetSubmitResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(trustSetSubmitResult);

    scanForResult(
      () -> getValidatedAccountInfo(wallet.publicKey().deriveAddress()),
      accountInfoResult -> accountInfoResult.accountData().flags().lsfDefaultRipple()
    );
  }

  private static final boolean FREEZE = true;
  private static final boolean UN_FREEZE = false;

  /**
   * Freeze an individual trustline that exists between the specified issuer and the specified counterparty for the
   * {@link #ISSUED_CURRENCY_CODE} (which is the only currency code this test uses).
   *
   * @param issuerKeyPair       The {@link KeyPair} of the trustline issuer.
   * @param counterpartyKeyPair The {@link KeyPair} of the trustline counterparty.
   * @param fee                 The fee to spend to get the "freeze" transaction into the ledger.
   * @param freeze              A boolean to toggle the trustline operation (i.e., {@code false} to unfreeze and
   *                            {@code true} to freeze).
   *
   * @return The {@link TrustLine} that was frozen or unfrozen.
   *
   * @throws JsonRpcClientErrorException If anything goes wrong while communicating with rippled.
   * @throws JsonProcessingException     If there are any problems parsing JSON.
   */
  private TrustLine adjustTrustlineFreeze(
    KeyPair issuerKeyPair,
    KeyPair counterpartyKeyPair,
    XrpCurrencyAmount fee,
    boolean freeze
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    AccountInfoResult issuerAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );

    Builder flagsBuilder = TrustSetFlags.builder();
    if (freeze) {
      flagsBuilder.tfSetFreeze();
    }

    TrustSet trustSet = TrustSet.builder()
      .account(issuerKeyPair.publicKey().deriveAddress())
      .fee(fee)
      .sequence(issuerAccountInfo.accountData().sequence())
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency(FreezeIssuedCurrencyIT.ISSUED_CURRENCY_CODE)
        .issuer(counterpartyKeyPair.publicKey().deriveAddress())
        .value("0")
        .build())
      .flags(flagsBuilder.build())
      .signingPublicKey(issuerKeyPair.publicKey())
      .build();

    SingleSignedTransaction<TrustSet> signedTrustSet = signatureService.sign(issuerKeyPair.privateKey(), trustSet);
    SubmitResult<TrustSet> trustSetSubmitResult = xrplClient.submit(signedTrustSet);
    assertThat(trustSetSubmitResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(trustSetSubmitResult);

    return scanForResult(
      () -> getValidatedAccountLines(issuerKeyPair.publicKey().deriveAddress(),
        counterpartyKeyPair.publicKey().deriveAddress()),
      accountLineResult -> accountLineResult.lines().stream()
        .filter(trustLine -> trustLine.account().equals(counterpartyKeyPair.publicKey().deriveAddress()))
        .anyMatch(TrustLine::freeze)
    )
      .lines().get(0);

  }

  /**
   * Globally freeze all trustlines that exists between the specified issuer and any counterparty.
   *
   * @param issuerKeyPair The {@link KeyPair} of the trustline issuer.
   * @param fee           The fee to spend to get the "freeze" transaction into the ledger.
   * @param freeze        A boolean to toggle the trustline operation (i.e., {@code false} to unfreeze and {@code true}
   *                      to freeze).
   *
   * @return The {@link AccountInfoResult} of the issuer account after the operation has been validated by the ledger.
   *
   * @throws JsonRpcClientErrorException If anything goes wrong while communicating with rippled.
   * @throws JsonProcessingException     If there are any problems parsing JSON.
   */
  private AccountInfoResult adjustGlobalTrustlineFreeze(
    KeyPair issuerKeyPair,
    XrpCurrencyAmount fee,
    boolean freeze
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    AccountInfoResult issuerAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );

    ImmutableAccountSet.Builder builder = AccountSet.builder()
      .account(issuerKeyPair.publicKey().deriveAddress())
      .fee(fee)
      .sequence(issuerAccountInfo.accountData().sequence())
      .signingPublicKey(issuerKeyPair.publicKey());
    if (freeze) {
      builder.setFlag(AccountSetFlag.GLOBAL_FREEZE);
    } else {
      builder.clearFlag(AccountSetFlag.GLOBAL_FREEZE);
    }
    AccountSet accountSet = builder.build();

    SingleSignedTransaction<AccountSet> signedTrustSet = signatureService.sign(issuerKeyPair.privateKey(), accountSet);
    SubmitResult<AccountSet> transactionResult = xrplClient.submit(signedTrustSet);
    assertThat(transactionResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(transactionResult);

    return scanForResult(
      () -> getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress()),
      accountInfoResult -> accountInfoResult.accountData().flags().lsfGlobalFreeze() == freeze
    );

  }
}
