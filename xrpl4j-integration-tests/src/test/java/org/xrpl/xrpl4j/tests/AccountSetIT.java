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
import com.google.common.primitives.UnsignedInteger;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.flags.AccountRootFlags;
import org.xrpl.xrpl4j.model.flags.AccountSetTransactionFlags;
import org.xrpl.xrpl4j.model.ledger.AccountRootObject;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.AccountSet.AccountSetFlag;

import java.util.Objects;

/**
 * An integration test that submits an AccountSet transaction for each AccountSet flag for an account, validates each
 * one is applied, and then clears each flag and ensure the clearing operation is validated in the ledger.
 *
 * @see "https://xrpl.org/accountset.html"
 */
public class AccountSetIT extends AbstractIT {

  @Test
  public void enableAllAndDisableOne() throws JsonRpcClientErrorException, JsonProcessingException {

    KeyPair keyPair = constructRandomAccount();

    ///////////////////////
    // Get validated account info and validate account state
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    );

    assertThat(accountInfo.status()).isNotEmpty().get().isEqualTo("success");
    assertThat(accountInfo.accountData().flags().lsfGlobalFreeze()).isEqualTo(false);

    assertEntryEqualsAccountInfo(keyPair, accountInfo);

    UnsignedInteger sequence = accountInfo.accountData().sequence();
    //////////////////////
    // Set asfAccountTxnID (no corresponding ledger flag)
    FeeResult feeResult = xrplClient.fee();
    AccountSet accountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .signingPublicKey(keyPair.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      keyPair.privateKey(), accountSet
    );
    SubmitResult<AccountSet> response = xrplClient.submit(signedAccountSet);

    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(signedAccountSet.hash()).isEqualTo(response.transactionResult().hash());
    logSubmitResult(response);

    ///////////////////////
    // Set flags one-by-one
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.DEFAULT_RIPPLE, AccountRootFlags.DEFAULT_RIPPLE);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.DEPOSIT_AUTH, AccountRootFlags.DEPOSIT_AUTH);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.DISALLOW_XRP, AccountRootFlags.DISALLOW_XRP);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.REQUIRE_AUTH, AccountRootFlags.REQUIRE_AUTH);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.REQUIRE_DEST, AccountRootFlags.REQUIRE_DEST_TAG);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.GLOBAL_FREEZE, AccountRootFlags.GLOBAL_FREEZE);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_NFT_OFFER, AccountRootFlags.DISALLOW_INCOMING_NFT_OFFER
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_CHECK, AccountRootFlags.DISALLOW_INCOMING_CHECK
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_PAY_CHAN, AccountRootFlags.DISALLOW_INCOMING_PAY_CHAN
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_TRUSTLINE, AccountRootFlags.DISALLOW_INCOMING_TRUSTLINE
    );
    sequence = sequence.plus(UnsignedInteger.ONE);

    AccountRootFlags flags1 = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    ).accountData().flags();

    assertClearFlag(keyPair, sequence, AccountSetFlag.GLOBAL_FREEZE, AccountRootFlags.GLOBAL_FREEZE);

    AccountRootFlags flags2 = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    ).accountData().flags();

    assertThat(flags1.getValue() - flags2.getValue())
      .isEqualTo(AccountRootFlags.GLOBAL_FREEZE.getValue());
  }

  @Test
  public void disableAndEnableAllFlags() throws JsonRpcClientErrorException, JsonProcessingException {

    KeyPair keyPair = constructRandomAccount();

    ///////////////////////
    // Get validated account info and validate account state
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    );
    assertThat(accountInfo.status()).isNotEmpty().get().isEqualTo("success");
    assertThat(accountInfo.accountData().flags().lsfGlobalFreeze()).isEqualTo(false);

    UnsignedInteger sequence = accountInfo.accountData().sequence();
    //////////////////////
    // Set asfAccountTxnID (no corresponding ledger flag)
    FeeResult feeResult = xrplClient.fee();
    AccountSet accountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .signingPublicKey(keyPair.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      keyPair.privateKey(), accountSet
    );
    SubmitResult<AccountSet> response = xrplClient.submit(signedAccountSet);

    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(response.transactionResult().hash()).isEqualTo(signedAccountSet.hash());
    logSubmitResult(response);

    ///////////////////////
    // Set flags one-by-one
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.DEFAULT_RIPPLE, AccountRootFlags.DEFAULT_RIPPLE);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.DEPOSIT_AUTH, AccountRootFlags.DEPOSIT_AUTH);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.DISALLOW_XRP, AccountRootFlags.DISALLOW_XRP);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.REQUIRE_AUTH, AccountRootFlags.REQUIRE_AUTH);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.REQUIRE_DEST, AccountRootFlags.REQUIRE_DEST_TAG);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(keyPair, sequence, AccountSetFlag.GLOBAL_FREEZE, AccountRootFlags.GLOBAL_FREEZE);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_NFT_OFFER, AccountRootFlags.DISALLOW_INCOMING_NFT_OFFER
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_CHECK, AccountRootFlags.DISALLOW_INCOMING_CHECK
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_PAY_CHAN, AccountRootFlags.DISALLOW_INCOMING_PAY_CHAN
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertSetFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_TRUSTLINE, AccountRootFlags.DISALLOW_INCOMING_TRUSTLINE
    );
    sequence = sequence.plus(UnsignedInteger.ONE);

    assertClearFlag(keyPair, sequence, AccountSetFlag.GLOBAL_FREEZE, AccountRootFlags.GLOBAL_FREEZE);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(keyPair, sequence, AccountSetFlag.REQUIRE_DEST, AccountRootFlags.REQUIRE_DEST_TAG);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(keyPair, sequence, AccountSetFlag.REQUIRE_AUTH, AccountRootFlags.REQUIRE_AUTH);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(keyPair, sequence, AccountSetFlag.DISALLOW_XRP, AccountRootFlags.DISALLOW_XRP);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(keyPair, sequence, AccountSetFlag.DEPOSIT_AUTH, AccountRootFlags.DEPOSIT_AUTH);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(keyPair, sequence, AccountSetFlag.DEFAULT_RIPPLE, AccountRootFlags.DEFAULT_RIPPLE);
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_NFT_OFFER, AccountRootFlags.DISALLOW_INCOMING_NFT_OFFER
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_CHECK, AccountRootFlags.DISALLOW_INCOMING_CHECK
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_PAY_CHAN, AccountRootFlags.DISALLOW_INCOMING_PAY_CHAN
    );
    sequence = sequence.plus(UnsignedInteger.ONE);
    assertClearFlag(
        keyPair, sequence, AccountSetFlag.DISALLOW_INCOMING_TRUSTLINE, AccountRootFlags.DISALLOW_INCOMING_TRUSTLINE
    );
  }

  @Test
  void enableAndDisableFlagsUsingTransactionFlags() throws JsonRpcClientErrorException, JsonProcessingException {
    BcSignatureService bcSignatureService = new BcSignatureService();
    KeyPair keyPair = constructRandomAccount();

    ///////////////////////
    // Get validated account info and validate account state
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress()));
    assertThat(accountInfo.status()).isNotEmpty().get().isEqualTo("success");
    assertThat(accountInfo.accountData().flags().lsfGlobalFreeze()).isEqualTo(false);

    UnsignedInteger sequence = accountInfo.accountData().sequence();

    FeeResult feeResult = xrplClient.fee();
    AccountSet enableAccountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(feeResult.drops().openLedgerFee())
      .sequence(sequence)
      .signingPublicKey(keyPair.publicKey())
      .flags(
        AccountSetTransactionFlags.builder()
          .tfRequireDestTag()
          .tfRequireAuth()
          .tfDisallowXrp()
          .build()
      )
      .build();

    SingleSignedTransaction<AccountSet> signedTransaction
      = bcSignatureService.sign(keyPair.privateKey(), enableAccountSet);
    SubmitResult<AccountSet> enableResponse = xrplClient.submit(signedTransaction);
    assertThat(enableResponse.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(signedTransaction.hash()).isEqualTo(enableResponse.transactionResult().hash());
    logSubmitResult(enableResponse, "SetFlag");

    /////////////////////////
    // Validate Account State
    this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress()),
      accountInfoResult -> {
        logger.info("AccountInfoResponse Flags: {}", accountInfoResult.accountData().flags());
        return accountInfoResult.accountData().flags().isSet(AccountRootFlags.REQUIRE_DEST_TAG) &&
          accountInfoResult.accountData().flags().isSet(AccountRootFlags.REQUIRE_AUTH) &&
          accountInfoResult.accountData().flags().isSet(AccountRootFlags.DISALLOW_XRP);
      });

    AccountSet disableAccountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(feeResult.drops().openLedgerFee())
      .sequence(sequence.plus(UnsignedInteger.ONE))
      .signingPublicKey(keyPair.publicKey())
      .flags(
        AccountSetTransactionFlags.builder()
          .tfOptionalDestTag()
          .tfOptionalAuth()
          .tfAllowXrp()
          .build()
      )
      .build();

    signedTransaction = bcSignatureService.sign(keyPair.privateKey(), disableAccountSet);
    SubmitResult<AccountSet> disableResponse = xrplClient.submit(signedTransaction);
    assertThat(disableResponse.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(signedTransaction.hash()).isEqualTo(disableResponse.transactionResult().hash());
    logSubmitResult(disableResponse, "SetFlag");

    /////////////////////////
    // Validate Account State
    this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress()),
      accountInfoResult -> {
        logger.info("AccountInfoResponse Flags: {}", accountInfoResult.accountData().flags());
        return !accountInfoResult.accountData().flags().isSet(AccountRootFlags.REQUIRE_DEST_TAG) &&
          !accountInfoResult.accountData().flags().isSet(AccountRootFlags.REQUIRE_AUTH) &&
          !accountInfoResult.accountData().flags().isSet(AccountRootFlags.DISALLOW_XRP);
      });
  }

  @Test
  void disableMasterFailsWithNoSignerList() throws JsonRpcClientErrorException, JsonProcessingException {
    BcSignatureService bcSignatureService = new BcSignatureService();
    KeyPair keyPair = constructRandomAccount();

    ///////////////////////
    // Get validated account info and validate account state
    AccountInfoResult accountInfo = this.scanForResult(() -> this.getValidatedAccountInfo(
      keyPair.publicKey().deriveAddress()
    ));
    assertThat(accountInfo.status()).isNotEmpty().get().isEqualTo("success");

    UnsignedInteger sequence = accountInfo.accountData().sequence();

    FeeResult feeResult = xrplClient.fee();
    AccountSet enableAccountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(feeResult.drops().openLedgerFee())
      .sequence(sequence)
      .signingPublicKey(keyPair.publicKey())
      .setFlag(AccountSetFlag.DISABLE_MASTER)
      .build();

    SingleSignedTransaction<AccountSet> signedTransaction
      = bcSignatureService.sign(keyPair.privateKey(), enableAccountSet);
    SubmitResult<AccountSet> enableResponse = xrplClient.submit(signedTransaction);
    assertThat(enableResponse.engineResult()).isEqualTo("tecNO_ALTERNATIVE_KEY");
    assertThat(signedTransaction.hash()).isEqualTo(enableResponse.transactionResult().hash());
    logger.info("AccountSet SetFlag transaction failed successfully:");
  }

  @Test
  void submitAndRetrieveAccountSetWithZeroClearFlagAndSetFlag()
    throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair keyPair = constructRandomAccount();

    ///////////////////////
    // Get validated account info and validate account state
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    );

    FeeResult feeResult = xrplClient.fee();
    AccountSet accountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .setFlag(AccountSetFlag.NONE)
      .clearFlag(AccountSetFlag.NONE)
      .signingPublicKey(keyPair.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      keyPair.privateKey(), accountSet
    );
    SubmitResult<AccountSet> response = xrplClient.submit(signedAccountSet);

    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(signedAccountSet.hash()).isEqualTo(response.transactionResult().hash());
    logSubmitResult(response);

    TransactionResult<AccountSet> accountSetTransactionResult = this.scanForResult(() ->
      this.getValidatedTransaction(signedAccountSet.hash(), AccountSet.class)
    );

    assertThat(accountSetTransactionResult.transaction().setFlag()).isNotEmpty().get().isEqualTo(AccountSetFlag.NONE);
    assertThat(accountSetTransactionResult.transaction().clearFlag()).isNotEmpty().get().isEqualTo(AccountSetFlag.NONE);
  }

  @Test
  void setAndUnsetDomainAndMessageKey() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair keyPair = constructRandomAccount();

    ///////////////////////
    // Get validated account info and validate account state
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    );

    FeeResult feeResult = xrplClient.fee();
    AccountSet setDomain = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .signingPublicKey(keyPair.publicKey())
      .domain("ABCD")
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .emailHash("F9879D71855B5FF21E4963273A886BFC")
      .walletLocator("F9879D71855B5FF21E4963273A886BFCF9879D71855B5FF21E4963273A886BFC")
      .build();

    SingleSignedTransaction<AccountSet> signedSetDomain = signatureService.sign(
      keyPair.privateKey(), setDomain
    );
    SubmitResult<AccountSet> response = xrplClient.submit(signedSetDomain);

    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(signedSetDomain.hash()).isEqualTo(response.transactionResult().hash());
    logSubmitResult(response);

    this.scanForResult(() ->
      this.getValidatedTransaction(signedSetDomain.hash(), AccountSet.class)
    );
    accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    );
    assertThat(accountInfo.accountData().domain()).isNotEmpty().isEqualTo(setDomain.domain());
    assertThat(accountInfo.accountData().messageKey()).isNotEmpty().isEqualTo(setDomain.messageKey());
    assertThat(accountInfo.accountData().emailHash()).isNotEmpty().isEqualTo(setDomain.emailHash());
    assertThat(accountInfo.accountData().walletLocator()).isNotEmpty().isEqualTo(setDomain.walletLocator());

    AccountSet clearDomain = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .signingPublicKey(keyPair.publicKey())
      .domain("")
      .messageKey("")
      .emailHash(Strings.repeat("0", 32))
      .walletLocator(Strings.repeat("0", 64))
      .build();

    SingleSignedTransaction<AccountSet> signedClearDomain = signatureService.sign(
      keyPair.privateKey(), clearDomain
    );
    SubmitResult<AccountSet> clearDomainSubmitResult = xrplClient.submit(signedClearDomain);

    assertThat(clearDomainSubmitResult.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(signedClearDomain.hash()).isEqualTo(clearDomainSubmitResult.transactionResult().hash());
    logSubmitResult(clearDomainSubmitResult);

    this.scanForResult(() ->
      this.getValidatedTransaction(signedClearDomain.hash(), AccountSet.class)
    );
    accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress())
    );
    assertThat(accountInfo.accountData().domain()).isEmpty();
    assertThat(accountInfo.accountData().messageKey()).isEmpty();
    assertThat(accountInfo.accountData().emailHash()).isEmpty();
    assertThat(accountInfo.accountData().walletLocator()).isEmpty();
  }

  //////////////////////
  // Test Helpers
  //////////////////////

  private void assertEntryEqualsAccountInfo(
    KeyPair keyPair,
    AccountInfoResult accountInfo
  ) throws JsonRpcClientErrorException {
    LedgerEntryResult<AccountRootObject> accountRoot = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.accountRoot(keyPair.publicKey().deriveAddress(), LedgerSpecifier.VALIDATED)
    );

    assertThat(accountInfo.accountData()).isEqualTo(accountRoot.node());

    LedgerEntryResult<AccountRootObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(accountRoot.index(), AccountRootObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(accountRoot.node());

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(accountRoot.index(), LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());
  }

  private void assertSetFlag(
    final KeyPair keyPair,
    final UnsignedInteger sequence,
    final AccountSetFlag accountSetFlag,
    final AccountRootFlags accountRootFlag
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    Objects.requireNonNull(keyPair);
    Objects.requireNonNull(accountSetFlag);

    FeeResult feeResult = xrplClient.fee();
    AccountSet accountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(sequence)
      .setFlag(accountSetFlag)
      .signingPublicKey(keyPair.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      keyPair.privateKey(), accountSet
    );
    SubmitResult<AccountSet> response = xrplClient.submit(signedAccountSet);

    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(response.transactionResult().hash()).isEqualTo(response.transactionResult().hash());
    logSubmitResult(response, String.format("SetFlag asf=%s arf=%s", accountSetFlag, accountRootFlag));

    /////////////////////////
    // Validate Account State
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress()),
      accountInfoResult -> {
        logger.info("AccountInfoResponse Flags: {}", accountInfoResult.accountData().flags());
        return accountInfoResult.accountData().flags().isSet(accountRootFlag);
      });

    assertEntryEqualsAccountInfo(keyPair, accountInfo);
  }

  private void assertClearFlag(
    final KeyPair keyPair,
    final UnsignedInteger sequence,
    final AccountSetFlag accountSetFlag,
    final AccountRootFlags accountRootFlag
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    Objects.requireNonNull(keyPair);
    Objects.requireNonNull(accountSetFlag);

    FeeResult feeResult = xrplClient.fee();
    AccountSet accountSet = AccountSet.builder()
      .account(keyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(sequence)
      .clearFlag(accountSetFlag)
      .signingPublicKey(keyPair.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      keyPair.privateKey(), accountSet
    );
    SubmitResult<AccountSet> response = xrplClient.submit(signedAccountSet);

    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    assertThat(response.transactionResult().hash()).isEqualTo(response.transactionResult().hash());
    logSubmitResult(response, String.format("ClearFlag asf=%s arf=%s", accountSetFlag, accountRootFlag));

    /////////////////////////
    // Validate Account State
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(keyPair.publicKey().deriveAddress()),
      accountInfoResult -> {
        logger.info("AccountInfoResponse Flags: {}", accountInfoResult.accountData().flags());
        return !accountInfoResult.accountData().flags().isSet(accountRootFlag);
      });

    assertEntryEqualsAccountInfo(keyPair, accountInfo);
  }
}
