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
import com.google.common.hash.Hashing;
import com.google.common.primitives.UnsignedInteger;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.CheckObject;
import org.xrpl.xrpl4j.model.ledger.EscrowObject;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.function.Predicate;

/**
 * Integration tests to validate submission of Check transactions.
 */
@SuppressWarnings( {"OptionalGetWithoutIsPresent"})
public class CheckIT extends AbstractIT {

  @Test
  public void createXrpCheckAndCash() throws JsonRpcClientErrorException, JsonProcessingException {

    //////////////////////
    // Generate and fund source and destination accounts
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfoResult = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    //////////////////////
    // Create a Check with an InvoiceID for easy identification
    Hash256 invoiceId = Hash256.of(Hashing.sha256().hashBytes("Check this out.".getBytes()).toString());
    CheckCreate checkCreate = CheckCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfoResult.accountData().sequence())
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .sendMax(XrpCurrencyAmount.ofDrops(12345))
      .invoiceId(invoiceId)
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<CheckCreate> signedCheckCreate = signatureService.sign(
      sourceKeyPair.privateKey(), checkCreate
    );
    SubmitResult<CheckCreate> response = xrplClient.submit(signedCheckCreate);
    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(response);

    //////////////////////
    // Poll the ledger for the source wallet's account objects, and validate that the created Check makes
    // it into the ledger
    CheckObject checkObject = (CheckObject) this
      .scanForResult(
        () -> this.getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
        result -> result.accountObjects().stream().anyMatch(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
      )
      .accountObjects().stream()
      .filter(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
      .findFirst().get();

    assertEntryEqualsObjectFromAccountObjects(checkObject);

    //////////////////////
    // Destination wallet cashes the Check
    feeResult = xrplClient.fee();
    AccountInfoResult destinationAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(destinationKeyPair.publicKey().deriveAddress())
    );
    CheckCash checkCash = CheckCash.builder()
      .account(destinationKeyPair.publicKey().deriveAddress())
      .amount(checkObject.sendMax())
      .sequence(destinationAccountInfo.accountData().sequence())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .checkId(checkObject.index())
      .signingPublicKey(destinationKeyPair.publicKey())
      .build();
    SingleSignedTransaction<CheckCash> signedCheckCash = signatureService.sign(
      destinationKeyPair.privateKey(), checkCash
    );
    SubmitResult<CheckCash> cashResponse = xrplClient.submit(signedCheckCash);
    assertThat(cashResponse.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(cashResponse);

    //////////////////////
    // Validate that the destination account balance increases by the check amount minus fees
    this.scanForResult(
      () -> this.getValidatedAccountInfo(destinationKeyPair.publicKey().deriveAddress()),
      result -> {
        logger.info("AccountInfoResult after CheckCash balance: {}", result.accountData().balance().value());
        return result.accountData().balance().equals(
          destinationAccountInfo.accountData().balance()
            .plus((XrpCurrencyAmount) checkObject.sendMax())
            .minus(checkCash.fee()));
      });

    //////////////////////
    // Validate that the Check object was deleted
    this.scanForResult(
      () -> this.getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
      result -> result.accountObjects().stream().noneMatch(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
    );
  }

  @Test
  public void createCheckAndSourceCancels() throws JsonRpcClientErrorException, JsonProcessingException {

    //////////////////////
    // Generate and fund source and destination accounts
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    //////////////////////
    // Create a Check with an InvoiceID for easy identification
    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfoResult = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    Hash256 invoiceId = Hash256.of(Hashing.sha256().hashBytes("Check this out.".getBytes()).toString());
    CheckCreate checkCreate = CheckCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfoResult.accountData().sequence())
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .sendMax(XrpCurrencyAmount.ofDrops(12345))
      .invoiceId(invoiceId)
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<CheckCreate> signedCheckCreate = signatureService.sign(
      sourceKeyPair.privateKey(), checkCreate
    );
    SubmitResult<CheckCreate> response = xrplClient.submit(signedCheckCreate);
    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(response);

    //////////////////////
    // Poll the ledger for the source wallet's account objects, and validate that the created Check makes
    // it into the ledger
    CheckObject checkObject = (CheckObject) this
      .scanForResult(() -> this.getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
        result -> result.accountObjects().stream().anyMatch(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
      )
      .accountObjects().stream()
      .filter(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
      .findFirst().get();

    assertEntryEqualsObjectFromAccountObjects(checkObject);

    //////////////////////
    // Source account cancels the Check
    feeResult = xrplClient.fee();
    CheckCancel checkCancel = CheckCancel.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .sequence(accountInfoResult.accountData().sequence().plus(UnsignedInteger.ONE))
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .checkId(checkObject.index())
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<CheckCancel> signedCheckCancel = signatureService.sign(
      sourceKeyPair.privateKey(), checkCancel
    );
    SubmitResult<CheckCancel> cancelResult = xrplClient.submit(signedCheckCancel);
    assertThat(cancelResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(cancelResult);

    //////////////////////
    // Validate that the Check does not exist after cancelling
    this.scanForResult(
      () -> this.getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
      result -> result.accountObjects().stream().noneMatch(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
    );
  }

  @Test
  public void createCheckAndDestinationCancels() throws JsonRpcClientErrorException, JsonProcessingException {

    //////////////////////
    // Generate and fund source and destination accounts
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    //////////////////////
    // Create a Check with an InvoiceID for easy identification
    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfoResult = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    Hash256 invoiceId = Hash256.of(Hashing.sha256().hashBytes("Check this out.".getBytes()).toString());
    CheckCreate checkCreate = CheckCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfoResult.accountData().sequence())
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .sendMax(XrpCurrencyAmount.ofDrops(12345))
      .invoiceId(invoiceId)
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    //////////////////////
    // Poll the ledger for the source wallet's account objects, and validate that the created Check makes
    // it into the ledger
    SingleSignedTransaction<CheckCreate> signedCheckCreate = signatureService.sign(
      sourceKeyPair.privateKey(), checkCreate
    );
    SubmitResult<CheckCreate> response = xrplClient.submit(signedCheckCreate);
    assertThat(response.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(response);

    CheckObject checkObject = (CheckObject) this
      .scanForResult(
        () -> this.getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
        result -> result.accountObjects().stream().anyMatch(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
      )
      .accountObjects().stream()
      .filter(findCheck(sourceKeyPair, destinationKeyPair, invoiceId))
      .findFirst().get();

    assertEntryEqualsObjectFromAccountObjects(checkObject);

    //////////////////////
    // Destination account cancels the Check
    feeResult = xrplClient.fee();
    AccountInfoResult destinationAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(destinationKeyPair.publicKey().deriveAddress())
    );
    CheckCancel checkCancel = CheckCancel.builder()
      .account(destinationKeyPair.publicKey().deriveAddress())
      .sequence(destinationAccountInfo.accountData().sequence())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .checkId(checkObject.index())
      .signingPublicKey(destinationKeyPair.publicKey())
      .build();

    SingleSignedTransaction<CheckCancel> signedCheckCancel = signatureService.sign(
      destinationKeyPair.privateKey(), checkCancel
    );
    SubmitResult<CheckCancel> cancelResult = xrplClient.submit(signedCheckCancel);
    assertThat(cancelResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(cancelResult);

    //////////////////////
    // Validate that the Check does not exist after cancelling
    this.scanForResult(
      () -> this.getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
      result -> result.accountObjects().stream().noneMatch(findCheck(sourceKeyPair, destinationKeyPair, invoiceId)));
  }

  private Predicate<LedgerObject> findCheck(KeyPair sourceKeyPair, KeyPair destinationKeyPair, Hash256 invoiceId) {
    return object ->
      CheckObject.class.isAssignableFrom(object.getClass()) &&
        ((CheckObject) object).invoiceId().map(id -> id.equals(invoiceId)).orElse(false) &&
        ((CheckObject) object).account().equals(sourceKeyPair.publicKey().deriveAddress()) &&
        ((CheckObject) object).destination().equals(destinationKeyPair.publicKey().deriveAddress());
  }


  private void assertEntryEqualsObjectFromAccountObjects(CheckObject checkObject) throws JsonRpcClientErrorException {
    LedgerEntryResult<CheckObject> checkEntry = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.check(checkObject.index(), LedgerSpecifier.CURRENT));

    assertThat(checkEntry.node()).isEqualTo(checkObject);

    LedgerEntryResult<CheckObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(checkObject.index(), CheckObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(checkEntry.node());

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(checkObject.index(), LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());
  }
}
