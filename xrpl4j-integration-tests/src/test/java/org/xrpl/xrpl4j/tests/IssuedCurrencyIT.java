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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountCurrenciesRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountCurrenciesResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountObjectsRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.TrustLine;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.ledger.RippleStateLedgerEntryParams;
import org.xrpl.xrpl4j.model.client.ledger.RippleStateLedgerEntryParams.RippleStateAccounts;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.flags.TrustSetFlags;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.ledger.RippleStateObject;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.PathStep;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.tests.environment.DevnetEnvironment;
import org.xrpl.xrpl4j.tests.environment.TestnetEnvironment;

import java.math.BigDecimal;
import java.util.List;

/**
 * An Integration Test to validate submission of issued currency transactions.
 */
public class IssuedCurrencyIT extends AbstractIT {

  // Test cases: MAX, MAX - 1, MAX + 1 (fail), MIN, MIN + 1, MIN - 1 (fail), 0, 1, -1
  @Test
  void createTrustlineWithMaxLimit() throws JsonRpcClientErrorException, JsonProcessingException {
    ///////////////////////////
    // Create random accounts for the issuer and the counterparty
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair counterpartyKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();

    ///////////////////////////
    // Create a Trust Line between issuer and counterparty denominated in a custom currency
    // by submitting a TrustSet transaction
    String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');

    TrustLine trustLine = createTrustLine(
      counterpartyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .value(IssuedCurrencyAmount.MAX_VALUE)
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    assertThatEntryEqualsObjectFromAccountObjects(
      trustLine,
      issuerKeyPair.publicKey().deriveAddress(),
      xrpl4jCoin
    );

    assertThat(trustLine.limitPeer()).isEqualTo("9999999999999999e80");
  }

  @Test
  void createTrustlineWithMaxLimitMinusOneExponent() throws JsonRpcClientErrorException, JsonProcessingException {
    ///////////////////////////
    // Create random accounts for the issuer and the counterparty
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair counterpartyKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();

    ///////////////////////////
    // Create a Trust Line between issuer and counterparty denominated in a custom currency
    // by submitting a TrustSet transaction
    String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');

    TrustLine trustLine = createTrustLine(
      counterpartyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .value(new BigDecimal(IssuedCurrencyAmount.MAX_VALUE).scaleByPowerOfTen(-1).toEngineeringString())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    assertThatEntryEqualsObjectFromAccountObjects(
      trustLine,
      issuerKeyPair.publicKey().deriveAddress(),
      xrpl4jCoin
    );

    assertThat(trustLine.limitPeer()).isEqualTo("9999999999999999e79");
  }

  @Test
  void createTrustlineWithSmallestPositiveLimit() throws JsonRpcClientErrorException, JsonProcessingException {
    ///////////////////////////
    // Create random accounts for the issuer and the counterparty
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair counterpartyKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();

    ///////////////////////////
    // Create a Trust Line between issuer and counterparty denominated in a custom currency
    // by submitting a TrustSet transaction
    String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');

    TrustLine trustLine = createTrustLine(
      counterpartyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .value(IssuedCurrencyAmount.MIN_POSITIVE_VALUE)
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    assertThatEntryEqualsObjectFromAccountObjects(
      trustLine,
      issuerKeyPair.publicKey().deriveAddress(),
      xrpl4jCoin
    );

    assertThat(trustLine.limitPeer()).isEqualTo("1000000000000000e-96");
  }

  @Test
  void createTrustlineWithSmalletPositiveLimitPlusOne() throws JsonRpcClientErrorException, JsonProcessingException {
    ///////////////////////////
    // Create random accounts for the issuer and the counterparty
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair counterpartyKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();

    ///////////////////////////
    // Create a Trust Line between issuer and counterparty denominated in a custom currency
    // by submitting a TrustSet transaction
    String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');

    BigDecimal limitValue = new BigDecimal(IssuedCurrencyAmount.MIN_POSITIVE_VALUE)
      .add(new BigDecimal(IssuedCurrencyAmount.MIN_POSITIVE_VALUE).scaleByPowerOfTen(-1));
    TrustLine trustLine = createTrustLine(
      counterpartyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .value(limitValue.toString())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    assertThatEntryEqualsObjectFromAccountObjects(
      trustLine,
      issuerKeyPair.publicKey().deriveAddress(),
      xrpl4jCoin
    );

    assertThat(trustLine.limitPeer()).isEqualTo("1100000000000000e-96");
  }

  @Test
  public void issueIssuedCurrencyBalance() throws JsonRpcClientErrorException, JsonProcessingException {
    ///////////////////////////
    // Create random accounts for the issuer and the counterparty
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair counterpartyKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();

    ///////////////////////////
    // Create a Trust Line between issuer and counterparty denominated in a custom currency
    // by submitting a TrustSet transaction
    String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');

    TrustLine trustLine = createTrustLine(
      counterpartyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    assertThatEntryEqualsObjectFromAccountObjects(
      trustLine,
      issuerKeyPair.publicKey().deriveAddress(),
      xrpl4jCoin
    );

    ///////////////////////////
    // Send some xrpl4jCoin to the counterparty account.
    sendIssuedCurrency(
      issuerKeyPair, counterpartyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .value(trustLine.limitPeer())
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Validate that the TrustLine balance was updated as a result of the Payment.
    // The trust line returned is from the perspective of the issuer, so the balance should be negative.
    TrustLine trustLineAfterPayment = this.scanForResult(
        () -> getValidatedAccountLines(
          issuerKeyPair.publicKey().deriveAddress(),
          counterpartyKeyPair.publicKey().deriveAddress()
        ),
        linesResult -> linesResult.lines().stream().anyMatch(line -> line.balance().equals("-" + trustLine.limitPeer()))
      ).lines().stream()
      .filter(line -> line.balance().equals("-" + trustLine.limitPeer()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(
        String.format("Expected a RippleStateObject for account %s, but none existed.", trustLine.account().value())
      ));

    assertThatEntryEqualsObjectFromAccountObjects(
      trustLineAfterPayment,
      issuerKeyPair.publicKey().deriveAddress(),
      xrpl4jCoin
    );

    ///////////////////////////
    // We can also retrieve the currencies that the counterparty can send/recieve using the accountCurrencies
    // method.
    AccountCurrenciesResult counterpartyCurrencies = xrplClient.accountCurrencies(
      AccountCurrenciesRequestParams.builder()
        .account(counterpartyKeyPair.publicKey().deriveAddress())
        .ledgerSpecifier(LedgerSpecifier.CURRENT)
        .build()
    );

    assertThat(counterpartyCurrencies.sendCurrencies()).asList().containsOnly(xrpl4jCoin);
    assertThat(counterpartyCurrencies.ledgerCurrentIndex()).isNotEmpty();
  }

  @Test
  public void sendSimpleRipplingIssuedCurrencyPayment() throws JsonRpcClientErrorException, JsonProcessingException {
    ///////////////////////////
    // Create a gateway (issuer) account and two normal accounts
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair aliceKeyPair = createRandomAccountEd25519();
    KeyPair bobKeyPair = createRandomAccountEd25519();

    ///////////////////////////
    // Set the DefaultRipple account flag on the issuer wallet, so that
    // its TrustLines allow rippling
    FeeResult feeResult = xrplClient.fee();
    setDefaultRipple(issuerKeyPair, feeResult);

    ///////////////////////////
    // Create a TrustLine between alice and the issuer
    createTrustLine(
      aliceKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Create a TrustLine between bob and the issuer
    TrustLine bobTrustLine = createTrustLine(
      bobKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Issuer issues 50 USD to alice
    sendIssuedCurrency(
      issuerKeyPair, aliceKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("50")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Issuer issues 50 USD to bob
    sendIssuedCurrency(
      issuerKeyPair, bobKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("50")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Try to find a path for this Payment.
    IssuedCurrencyAmount pathDestinationAmount = IssuedCurrencyAmount.builder()
      .issuer(issuerKeyPair.publicKey().deriveAddress())
      .currency(bobTrustLine.currency())
      .value("10")
      .build();

    ///////////////////////////
    // Validate that there exists a path such that this Payment can succeed.
    // Note that because this path consists of all implied paths, rippled will not return any path steps
    scanForResult(
      () -> getValidatedRipplePath(aliceKeyPair, bobKeyPair, pathDestinationAmount),
      path -> !path.alternatives().isEmpty() // rippled will return a PathAlternative without any path steps
    );

    ///////////////////////////
    // Send 10 USD from Alice to Bob by rippling through the issuer
    AccountInfoResult aliceAccountInfo = getValidatedAccountInfo(aliceKeyPair.publicKey().deriveAddress());
    Payment aliceToBobPayment = Payment.builder()
      .account(aliceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(aliceAccountInfo.accountData().sequence())
      .destination(bobKeyPair.publicKey().deriveAddress())
      .amount(IssuedCurrencyAmount.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10")
        .build())
      .signingPublicKey(aliceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<Payment> signedAliceToBobPayment = signatureService.sign(
      aliceKeyPair.privateKey(), aliceToBobPayment
    );
    SubmitResult<Payment> paymentResult = xrplClient.submit(signedAliceToBobPayment);
    assertThat(paymentResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(paymentResult);

    ///////////////////////////
    // Validate that bob and alice's trust line balances have been updated appropriately
    scanForResult(
      () -> getValidatedAccountLines(aliceKeyPair.publicKey().deriveAddress(),
        issuerKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("40"))
    );

    scanForResult(
      () -> getValidatedAccountLines(bobKeyPair.publicKey().deriveAddress(), issuerKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("60"))
    );
  }

  /**
   * Send and verify a multi-hop payment that transits the following path.
   *
   * <pre>
   * ┌────────────┐         ┌────────────┐        ┌────────────┐         ┌────────────┐        ┌────────────┐
   * │  Charlie   │◁───TL──▷│  IssuerA   │◁──TL──▷│   Emily    │◁──TL───▷│  IssuerB   │◁──TL──▷│   Daniel   │
   * └────────────┘         └────────────┘        └────────────┘         └────────────┘        └────────────┘
   * </pre>
   */
  @Test
  public void sendMultiHopSameCurrencyPayment() throws JsonRpcClientErrorException, JsonProcessingException {
    // NOTE: Only run this on non-testnet and non-devnet environments.
    if (TestnetEnvironment.class.isAssignableFrom(xrplEnvironment.getClass()) ||
      DevnetEnvironment.class.isAssignableFrom(xrplEnvironment.getClass())) {
      return;
    }

    ///////////////////////////
    // Create two issuer wallets and three non-issuer wallets
    final KeyPair issuerAKeyPair = createRandomAccountEd25519();
    final KeyPair issuerBKeyPair = createRandomAccountEd25519();
    final KeyPair charlieKeyPair = createRandomAccountEd25519();
    final KeyPair emilyKeyPair = createRandomAccountEd25519();
    final KeyPair danielKeyPair = createRandomAccountEd25519();

    ///////////////////////////
    // Set the lsfDefaultRipple AccountRoot flag so that all trustlines in this topography allow rippling
    FeeResult feeResult = xrplClient.fee();
    setDefaultRipple(issuerAKeyPair, feeResult);
    setDefaultRipple(issuerBKeyPair, feeResult);
    setDefaultRipple(charlieKeyPair, feeResult);
    setDefaultRipple(emilyKeyPair, feeResult);
    setDefaultRipple(danielKeyPair, feeResult);

    ///////////////////////////
    // Create a Trustline between charlie and issuerA
    final TrustLine charlieTrustLineWithIssuerA = createTrustLine(
      charlieKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerAKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TrustSetFlags.empty()
    );

    ///////////////////////////
    // Create a Trustline between emily and issuerA
    createTrustLine(
      emilyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerAKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TrustSetFlags.empty()
    );

    ///////////////////////////
    // Create a Trustline between emily and issuerB
    createTrustLine(
      emilyKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerBKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TrustSetFlags.empty()
    );

    ///////////////////////////
    // Create a Trustline between daniel and issuerB
    final TrustLine danielTrustLineWithIssuerB = createTrustLine(
      danielKeyPair,
      IssuedCurrencyAmount.builder()
        .issuer(issuerBKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10000")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee(),
      TrustSetFlags.empty()
    );

    ///////////////////////////
    // Issue 10 USD from issuerA to charlie.
    // IssuerA now owes Charlie 10 USD.
    sendIssuedCurrency(
      issuerAKeyPair, // <-- From
      charlieKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .issuer(issuerAKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("10")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Issue 1 USD from issuerA to emily.
    // IssuerA now owes Emily 1 USD
    sendIssuedCurrency(
      issuerAKeyPair, // <-- From
      emilyKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .issuer(issuerAKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("1")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Issue 100 USD from issuerB to emily.
    // IssuerB now owes Emily 100 USD
    sendIssuedCurrency(
      issuerBKeyPair, // <-- From
      emilyKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .issuer(issuerBKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("100")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Issue 2 USD from issuerB to daniel.
    // IssuerB now owes Daniel 2 USD
    sendIssuedCurrency(
      issuerBKeyPair, // <-- From
      danielKeyPair, // <-- To
      IssuedCurrencyAmount.builder()
        .issuer(issuerBKeyPair.publicKey().deriveAddress())
        .currency("USD")
        .value("2")
        .build(),
      FeeUtils.computeNetworkFees(feeResult).recommendedFee()
    );

    ///////////////////////////
    // Look for a payment path from charlie to daniel.
    List<List<PathStep>> pathSteps = scanForResult(
      () -> getValidatedRipplePath(charlieKeyPair, danielKeyPair,
        IssuedCurrencyAmount.builder()
          .issuer(issuerBKeyPair.publicKey().deriveAddress())
          .currency(charlieTrustLineWithIssuerA.currency())
          .value("10")
          .build()),
      path -> !path.alternatives().isEmpty()
    )
      .alternatives().stream()
      .filter(alt ->
        !alt.pathsComputed().isEmpty() &&
          alt.sourceAmount().equals(
            IssuedCurrencyAmount.builder()
              .issuer(charlieKeyPair.publicKey().deriveAddress())
              .currency(charlieTrustLineWithIssuerA.currency())
              .value("10")
              .build()
          )
      )
      .findFirst().orElseThrow(() -> new RuntimeException("No path found."))
      .pathsComputed();

    ///////////////////////////
    // Send a Payment from charlie to Daniel using the previously found paths.
    AccountInfoResult charlieAccountInfo = getValidatedAccountInfo(charlieKeyPair.publicKey().deriveAddress());
    Payment charlieToDanielPayment = Payment.builder()
      .account(charlieKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(charlieAccountInfo.accountData().sequence())
      .destination(danielKeyPair.publicKey().deriveAddress())
      .amount(IssuedCurrencyAmount.builder()
        .issuer(issuerBKeyPair.publicKey().deriveAddress())
        .currency(danielTrustLineWithIssuerB.currency())
        .value("10")
        .build())
      .paths(pathSteps)
      .signingPublicKey(charlieKeyPair.publicKey())
      .build();

    SingleSignedTransaction<Payment> signedCharlieToDanielPayment = signatureService.sign(
      charlieKeyPair.privateKey(), charlieToDanielPayment
    );
    SubmitResult<Payment> paymentResult = xrplClient.submit(signedCharlieToDanielPayment);
    assertThat(paymentResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(paymentResult);

    ///////////////////////////
    // Validate that everyone's trust line balances have been updated appropriately
    scanForResult(
      () -> getValidatedAccountLines(charlieKeyPair.publicKey().deriveAddress(),
        issuerAKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("0"))
    );

    scanForResult(
      () -> getValidatedAccountLines(emilyKeyPair.publicKey().deriveAddress(),
        issuerAKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("11"))
    );

    scanForResult(
      () -> getValidatedAccountLines(emilyKeyPair.publicKey().deriveAddress(),
        issuerBKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("90"))
    );

    scanForResult(
      () -> getValidatedAccountLines(danielKeyPair.publicKey().deriveAddress(),
        issuerBKeyPair.publicKey().deriveAddress()),
      linesResult -> linesResult.lines().stream()
        .anyMatch(line -> line.balance().equals("12"))
    );

  }

  /**
   * Set the {@code lsfDefaultRipple} flag on an issuer account.
   *
   * @param issuerKeyPair The {@link KeyPair} containing the address of the issuer account.
   * @param feeResult     The current {@link FeeResult}.
   *
   * @throws JsonRpcClientErrorException If anything goes wrong while communicating with rippled.
   */
  public void setDefaultRipple(KeyPair issuerKeyPair, FeeResult feeResult)
    throws JsonRpcClientErrorException, JsonProcessingException {
    AccountInfoResult issuerAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );

    AccountSet setDefaultRipple = AccountSet.builder()
      .account(issuerKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(issuerAccountInfo.accountData().sequence())
      .signingPublicKey(issuerKeyPair.publicKey())
      .setFlag(AccountSet.AccountSetFlag.DEFAULT_RIPPLE)
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      issuerKeyPair.privateKey(), setDefaultRipple
    );
    SubmitResult<AccountSet> setResult = xrplClient.submit(signedAccountSet);
    assertThat(setResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(setResult);

    scanForResult(
      () -> getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress()),
      info -> info.accountData().flags().lsfDefaultRipple()
    );
  }


  private void assertThatEntryEqualsObjectFromAccountObjects(
    TrustLine trustLine,
    Address peerAddress,
    String currency
  ) throws JsonRpcClientErrorException {
    RippleStateObject rippleStateObject = (RippleStateObject) xrplClient.accountObjects(
        AccountObjectsRequestParams.of(trustLine.account())
      ).accountObjects().stream()
      .filter(object -> RippleStateObject.class.isAssignableFrom(object.getClass()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(
        String.format("Expected a RippleStateObject for account %s, but none existed.", trustLine.account().value())
      ));

    LedgerEntryResult<RippleStateObject> entry = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.rippleState(
        RippleStateLedgerEntryParams.builder()
          .accounts(RippleStateAccounts.of(trustLine.account(), peerAddress))
          .currency(currency)
          .build(),
        LedgerSpecifier.VALIDATED
      )
    );

    assertThat(entry.node()).isEqualTo(rippleStateObject);

    LedgerEntryResult<RippleStateObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(rippleStateObject.index(), RippleStateObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entry.node());

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(rippleStateObject.index(), LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());
  }
}
