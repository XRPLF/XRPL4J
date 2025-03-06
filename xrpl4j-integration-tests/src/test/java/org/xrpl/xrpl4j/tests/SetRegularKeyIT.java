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
import com.google.common.primitives.UnsignedInteger;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.AccountRootObject;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.SetRegularKey;

/**
 * Integration Tests to validate submission of SetRegularKey transactions.
 */
public class SetRegularKeyIT extends AbstractIT {

  @Test
  void setRegularKeyOnAccount() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////////
    // Create a random account
    KeyPair wallet = createRandomAccountEd25519();

    //////////////////////////
    // Wait for the account to show up on ledger
    AccountInfoResult accountInfo = scanForResult(() -> getValidatedAccountInfo(wallet.publicKey().deriveAddress()));
    assertEntryEqualsAccountInfo(wallet, accountInfo);

    //////////////////////////
    // Generate a new wallet locally
    KeyPair newKeyPair = Seed.ed25519Seed().deriveKeyPair();

    //////////////////////////
    // Submit a SetRegularKey transaction with the new wallet's address so that we
    // can sign future transactions with the new wallet's keypair
    FeeResult feeResult = xrplClient.fee();
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(wallet.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .regularKey(newKeyPair.publicKey().deriveAddress())
      .signingPublicKey(wallet.publicKey())
      .build();

    SingleSignedTransaction<SetRegularKey> signedSetRegularKey = signatureService.sign(
      wallet.privateKey(), setRegularKey
    );
    SubmitResult<SetRegularKey> setResult = xrplClient.submit(signedSetRegularKey);
    assertThat(setResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(setResult);

    //////////////////////////
    // Verify that the SetRegularKey transaction worked by submitting empty
    // AccountSet transactions, signed with the new wallet key pair, until
    // we get a successful response.
    scanForResult(
      () -> {
        AccountSet accountSet = AccountSet.builder()
          .account(wallet.publicKey().deriveAddress())
          .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
          .sequence(accountInfo.accountData().sequence().plus(UnsignedInteger.ONE))
          .signingPublicKey(newKeyPair.publicKey())
          .build();
        SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
          newKeyPair.privateKey(), accountSet
        );
        try {
          return xrplClient.submit(signedAccountSet);
        } catch (JsonRpcClientErrorException | JsonProcessingException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }
    );

    AccountInfoResult accountInfoAfterRegKeySet = scanForResult(
      () -> getValidatedAccountInfo(wallet.publicKey().deriveAddress())
    );
    assertEntryEqualsAccountInfo(wallet, accountInfoAfterRegKeySet);
  }

  @Test
  void removeRegularKeyFromAccount() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////////
    // Create a random account
    KeyPair wallet = createRandomAccountEd25519();

    //////////////////////////
    // Wait for the account to show up on ledger
    AccountInfoResult accountInfo = scanForResult(() -> getValidatedAccountInfo(wallet.publicKey().deriveAddress()));
    assertEntryEqualsAccountInfo(wallet, accountInfo);

    //////////////////////////
    // Generate a new wallet locally
    KeyPair newKeyPair = Seed.ed25519Seed().deriveKeyPair();

    //////////////////////////
    // Submit a SetRegularKey transaction with the new wallet's address so that we
    // can sign future transactions with the new wallet's keypair
    FeeResult feeResult = xrplClient.fee();
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(wallet.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .regularKey(newKeyPair.publicKey().deriveAddress())
      .signingPublicKey(wallet.publicKey())
      .build();

    SingleSignedTransaction<SetRegularKey> signedSetRegularKey = signatureService.sign(
      wallet.privateKey(), setRegularKey
    );
    SubmitResult<SetRegularKey> setResult = xrplClient.submit(signedSetRegularKey);
    assertThat(setResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(setResult);

    //////////////////////////
    // Verify that the SetRegularKey transaction worked by submitting empty
    // AccountSet transactions, signed with the new wallet key pair, until
    // we get a successful response.
    scanForResult(
      () -> {
        AccountSet accountSet = AccountSet.builder()
          .account(wallet.publicKey().deriveAddress())
          .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
          .sequence(accountInfo.accountData().sequence().plus(UnsignedInteger.ONE))
          .signingPublicKey(newKeyPair.publicKey())
          .build();

        SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
          newKeyPair.privateKey(), accountSet
        );
        try {
          return xrplClient.submit(signedAccountSet);
        } catch (JsonRpcClientErrorException | JsonProcessingException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }
    );

    AccountInfoResult accountInfoAfterSet = scanForResult(
      () -> getValidatedAccountInfo(wallet.publicKey().deriveAddress())
    );
    assertEntryEqualsAccountInfo(wallet, accountInfoAfterSet);

    SetRegularKey removeRegularKey = SetRegularKey.builder()
      .account(wallet.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
      .signingPublicKey(wallet.publicKey())
      .build();
    SingleSignedTransaction<SetRegularKey> signedRemoveRegularKey = signatureService.sign(
      wallet.privateKey(), removeRegularKey
    );
    SubmitResult<SetRegularKey> removeResult = xrplClient.submit(signedRemoveRegularKey);
    assertThat(removeResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(removeResult);

    AccountInfoResult accountInfoAfterRemoving = scanForResult(
      () -> getValidatedAccountInfo(wallet.publicKey().deriveAddress()),
      infoResult -> !infoResult.accountData().regularKey().isPresent()
    );

    assertEntryEqualsAccountInfo(wallet, accountInfoAfterRemoving);
  }

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
}
