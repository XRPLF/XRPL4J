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
import com.google.common.primitives.UnsignedLong;
import com.ripple.cryptoconditions.PreimageSha256Fulfillment;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.github.dockerjava.core.dockerfile.DockerfileStatement.Add;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountObjectsRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountObjectsRequestParams.AccountObjectType;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.EscrowLedgerEntryParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;
import org.xrpl.xrpl4j.model.ledger.EscrowObject;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.EscrowCancel;
import org.xrpl.xrpl4j.model.transactions.EscrowCreate;
import org.xrpl.xrpl4j.model.transactions.EscrowFinish;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.time.Duration;
import java.time.Instant;

/**
 * Integration test to validate creation, cancellation, and execution of escrow transactions.
 */
public class EscrowIT extends AbstractIT {

  @Test
  public void createAndFinishTimeBasedEscrow() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////
    // Create random sender and receiver accounts
    KeyPair senderKeyPair = createRandomAccountEd25519();
    KeyPair receiverKeyPair = createRandomAccountEd25519();

    //////////////////////
    // Sender account creates an Escrow with the receiver account
    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress())
    );
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .sequence(senderAccountInfo.accountData().sequence())
      .fee(feeResult.drops().openLedgerFee())
      .amount(XrpCurrencyAmount.ofDrops(123456))
      .destination(receiverKeyPair.publicKey().deriveAddress())
      .cancelAfter(instantToXrpTimestamp(getMinExpirationTime().plus(Duration.ofSeconds(100))))
      .finishAfter(instantToXrpTimestamp(getMinExpirationTime().plus(Duration.ofSeconds(5))))
      .signingPublicKey(senderKeyPair.publicKey())
      .build();

    //////////////////////
    // Submit the EscrowCreate transaction and validate that it was successful
    SingleSignedTransaction<EscrowCreate> signedEscrowCreate = signatureService.sign(
      senderKeyPair.privateKey(), escrowCreate
    );
    SubmitResult<EscrowCreate> createResult = xrplClient.submit(signedEscrowCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////
    // Then wait until the transaction gets committed to a validated ledger
    TransactionResult<EscrowCreate> result = this.scanForResult(
      () -> this.getValidatedTransaction(createResult.transactionResult().hash(), EscrowCreate.class)
    );

    assertEntryEqualsObjectFromAccountObjects(
      senderKeyPair.publicKey().deriveAddress(),
      escrowCreate.sequence()
    );

    //////////////////////
    // Wait until the close time on the current validated ledger is after the finishAfter time on the Escrow
    this.scanForResult(
      this::getValidatedLedger,
      ledgerResult ->
        FluentCompareTo.is(ledgerResult.ledger().closeTime().orElse(UnsignedLong.ZERO))
          .greaterThan(
            createResult.transactionResult().transaction().finishAfter()
              .map(finishAfter -> finishAfter.plus(UnsignedLong.valueOf(5)))
              .orElse(UnsignedLong.MAX_VALUE)
          )
    );

    //////////////////////
    // Receiver submits an EscrowFinish transaction to release the Escrow funds
    AccountInfoResult receiverAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(receiverKeyPair.publicKey().deriveAddress())
    );
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(receiverKeyPair.publicKey().deriveAddress())
      .fee(feeResult.drops().openLedgerFee())
      .sequence(receiverAccountInfo.accountData().sequence())
      .owner(senderKeyPair.publicKey().deriveAddress())
      .offerSequence(result.transaction().sequence())
      .signingPublicKey(receiverKeyPair.publicKey())
      .build();

    SingleSignedTransaction<EscrowFinish> signedEscrowFinish = signatureService.sign(
      receiverKeyPair.privateKey(), escrowFinish
    );
    SubmitResult<EscrowFinish> finishResult = xrplClient.submit(signedEscrowFinish);
    assertThat(finishResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(finishResult);

    //////////////////////
    // Wait for the EscrowFinish to get applied to a validated ledger
    this.scanForResult(
      () -> this.getValidatedTransaction(finishResult.transactionResult().hash(), EscrowFinish.class)
    );

    /////////////////////
    // Ensure that the funds were released to the receiver.
    this.scanForResult(
      () -> this.getValidatedAccountInfo(receiverKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().balance().equals(
        receiverAccountInfo.accountData().balance()
          .plus(escrowCreate.amount())
          .minus(feeResult.drops().openLedgerFee())
      )
    );

  }

  @Test
  public void createAndCancelTimeBasedEscrow() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////
    // Create random sender and receiver accounts
    KeyPair senderKeyPair = createRandomAccountEd25519();
    KeyPair receiverKeyPair = createRandomAccountEd25519();

    //////////////////////
    // Sender account creates an Escrow with the receiver account
    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress())
    );

    scanForResult(() -> getValidatedAccountInfo(receiverKeyPair.publicKey().deriveAddress()));
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .sequence(senderAccountInfo.accountData().sequence())
      .fee(feeResult.drops().openLedgerFee())
      .amount(XrpCurrencyAmount.ofDrops(123456))
      .destination(receiverKeyPair.publicKey().deriveAddress())
      .cancelAfter(instantToXrpTimestamp(getMinExpirationTime().plus(Duration.ofSeconds(10))))
      .finishAfter(instantToXrpTimestamp(getMinExpirationTime().plus(Duration.ofSeconds(5))))
      .signingPublicKey(senderKeyPair.publicKey())
      .build();

    //////////////////////
    // Submit the EscrowCreate transaction and validate that it was successful
    SingleSignedTransaction<EscrowCreate> signedEscrowCreate = signatureService.sign(
      senderKeyPair.privateKey(), escrowCreate
    );
    SubmitResult<EscrowCreate> createResult = xrplClient.submit(signedEscrowCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////
    // Then wait until the transaction gets committed to a validated ledger
    final TransactionResult<EscrowCreate> result = this.scanForResult(
      () -> this.getValidatedTransaction(createResult.transactionResult().hash(), EscrowCreate.class)
    );

    this.scanForResult(
      () -> this.getValidatedAccountObjects(senderKeyPair.publicKey().deriveAddress()),
      objectsResult -> objectsResult.accountObjects().stream()
        .anyMatch(object ->
          EscrowObject.class.isAssignableFrom(object.getClass()) &&
            ((EscrowObject) object).destination().equals(receiverKeyPair.publicKey().deriveAddress())
        )
    );

    assertEntryEqualsObjectFromAccountObjects(
      senderKeyPair.publicKey().deriveAddress(),
      escrowCreate.sequence()
    );

    //////////////////////
    // Wait until the close time on the current validated ledger is after the cancelAfter time on the Escrow
    this.scanForResult(
      this::getValidatedLedger,
      ledgerResult ->
        FluentCompareTo.is(ledgerResult.ledger().closeTime().orElse(UnsignedLong.ZERO))
          .greaterThan(
            createResult.transactionResult().transaction().cancelAfter()
              .map(cancelAfter -> cancelAfter.plus(UnsignedLong.valueOf(5)))
              .orElse(UnsignedLong.MAX_VALUE)
          )
    );

    //////////////////////
    // Sender account cancels the Escrow
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .fee(feeResult.drops().openLedgerFee())
      .sequence(senderAccountInfo.accountData().sequence().plus(UnsignedInteger.ONE))
      .owner(senderKeyPair.publicKey().deriveAddress())
      .offerSequence(result.transaction().sequence())
      .signingPublicKey(senderKeyPair.publicKey())
      .build();

    SingleSignedTransaction<EscrowCancel> signedEscrowCancel = signatureService.sign(
      senderKeyPair.privateKey(), escrowCancel
    );
    SubmitResult<EscrowCancel> cancelResult = xrplClient.submit(signedEscrowCancel);
    assertThat(cancelResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(cancelResult);

    //////////////////////
    // Wait until the transaction enters a validated ledger
    this.scanForResult(() -> this.getValidatedTransaction(cancelResult.transactionResult().hash(), EscrowCancel.class));

    //////////////////////
    // Ensure that the funds were released to the sender.
    this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().balance().equals(
        senderAccountInfo.accountData().balance()
          .minus(feeResult.drops().openLedgerFee().times(XrpCurrencyAmount.of(UnsignedLong.valueOf(2))))
      )
    );
  }

  @Test
  public void createAndFinishCryptoConditionBasedEscrow() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////
    // Create random sender and receiver accounts
    KeyPair senderKeyPair = createRandomAccountEd25519();
    KeyPair receiverKeyPair = createRandomAccountEd25519();

    //////////////////////
    // Create Secret Escrow CryptoCondition/Fulfillment Pair.
    final byte[] secret = "shh".getBytes();
    final PreimageSha256Fulfillment executeEscrowFulfillment = PreimageSha256Fulfillment.from(secret);

    //////////////////////
    // Sender account creates an Escrow with the receiver account
    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress())
    );
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .sequence(senderAccountInfo.accountData().sequence())
      .fee(feeResult.drops().openLedgerFee())
      .amount(XrpCurrencyAmount.ofDrops(123456))
      .destination(receiverKeyPair.publicKey().deriveAddress())
      .signingPublicKey(senderKeyPair.publicKey())
      // With the fix1571 amendment enabled, you must supply FinishAfter, Condition, or both.
      .finishAfter(instantToXrpTimestamp(getMinExpirationTime().plus(Duration.ofSeconds(5))))
      .condition(executeEscrowFulfillment.getDerivedCondition()) // <-- Only the fulfillment holder can execute this.
      .build();

    //////////////////////
    // Submit the EscrowCreate transaction and validate that it was successful
    SingleSignedTransaction<EscrowCreate> signedEscrowCreate = signatureService.sign(
      senderKeyPair.privateKey(), escrowCreate
    );
    SubmitResult<EscrowCreate> createResult = xrplClient.submit(signedEscrowCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////
    // Then wait until the transaction gets committed to a validated ledger
    TransactionResult<EscrowCreate> result = this.scanForResult(
      () -> this.getValidatedTransaction(createResult.transactionResult().hash(), EscrowCreate.class)
    );

    assertEntryEqualsObjectFromAccountObjects(
      senderKeyPair.publicKey().deriveAddress(),
      escrowCreate.sequence()
    );

    //////////////////////
    // Wait until the close time on the current validated ledger is after the finishAfter time on the Escrow
    this.scanForResult(
      this::getValidatedLedger,
      ledgerResult ->
        FluentCompareTo.is(ledgerResult.ledger().closeTime().orElse(UnsignedLong.ZERO))
          .greaterThan(
            createResult.transactionResult().transaction().finishAfter()
              .map(cancelAfter -> cancelAfter.plus(UnsignedLong.valueOf(5)))
              .orElse(UnsignedLong.MAX_VALUE)
          )
    );

    //////////////////////
    // Execute the escrow using the secret fulfillment known only to the appropriate party.
    AccountInfoResult receiverAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(receiverKeyPair.publicKey().deriveAddress())
    );

    final XrpCurrencyAmount feeForFulfillment = EscrowFinish
      .computeFee(feeResult.drops().openLedgerFee(), executeEscrowFulfillment);
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(receiverKeyPair.publicKey().deriveAddress())
      // V-- Be sure to add more fee to process the Fulfillment
      .fee(EscrowFinish.computeFee(feeResult.drops().openLedgerFee(), executeEscrowFulfillment))
      .sequence(receiverAccountInfo.accountData().sequence())
      .owner(senderKeyPair.publicKey().deriveAddress())
      .offerSequence(result.transaction().sequence())
      .signingPublicKey(receiverKeyPair.publicKey())
      .condition(executeEscrowFulfillment.getDerivedCondition()) // <-- condition and fulfillment are required.
      .fulfillment(executeEscrowFulfillment) // <-- condition and fulfillment are required to finish an escrow
      .build();

    SingleSignedTransaction<EscrowFinish> signedEscrowFinish = signatureService.sign(
      receiverKeyPair.privateKey(), escrowFinish
    );
    SubmitResult<EscrowFinish> finishResult = xrplClient.submit(signedEscrowFinish);
    assertThat(finishResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(finishResult);

    //////////////////////
    // Wait until the transaction enters a validated ledger
    this.scanForResult(() -> this.getValidatedTransaction(finishResult.transactionResult().hash(), EscrowFinish.class));

    //////////////////////
    // Ensure that the funds were released to the receiver.
    this.scanForResult(
      () -> this.getValidatedAccountInfo(receiverKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().balance().equals(
        receiverAccountInfo.accountData().balance()
          .plus(escrowCreate.amount())
          .minus(feeForFulfillment)
      )
    );

  }

  @Test
  public void createAndCancelCryptoConditionBasedEscrow() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////
    // Create random sender and receiver accounts
    KeyPair senderKeyPair = createRandomAccountEd25519();
    KeyPair receiverKeyPair = createRandomAccountEd25519();

    //////////////////////
    // Create Secret Escrow CryptoCondition/Fulfillment Pair.
    final byte[] secret = "shh".getBytes();
    final PreimageSha256Fulfillment escrowFulfillment = PreimageSha256Fulfillment.from(secret);

    //////////////////////
    // Sender account creates an Escrow with the receiver account
    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress())
    );
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .sequence(senderAccountInfo.accountData().sequence())
      .fee(feeResult.drops().openLedgerFee())
      .amount(XrpCurrencyAmount.ofDrops(123456))
      .destination(receiverKeyPair.publicKey().deriveAddress())
      .cancelAfter(instantToXrpTimestamp(getMinExpirationTime().plus(Duration.ofSeconds(10))))
      .condition(escrowFulfillment.getDerivedCondition()) // <-- Only the fulfillment holder can execute this.
      .signingPublicKey(senderKeyPair.publicKey())
      .build();

    //////////////////////
    // Submit the EscrowCreate transaction and validate that it was successful
    SingleSignedTransaction<EscrowCreate> signedEscrowCreate = signatureService.sign(
      senderKeyPair.privateKey(), escrowCreate
    );
    SubmitResult<EscrowCreate> createResult = xrplClient.submit(signedEscrowCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////
    // Then wait until the transaction gets committed to a validated ledger
    TransactionResult<EscrowCreate> result = this.scanForResult(
      () -> this.getValidatedTransaction(createResult.transactionResult().hash(), EscrowCreate.class)
    );

    assertEntryEqualsObjectFromAccountObjects(
      senderKeyPair.publicKey().deriveAddress(),
      escrowCreate.sequence()
    );

    //////////////////////
    // Wait until the close time on the current validated ledger is after the cancelAfter time on the Escrow
    this.scanForResult(
      this::getValidatedLedger,
      ledgerResult ->
        FluentCompareTo.is(ledgerResult.ledger().closeTime().orElse(UnsignedLong.ZERO))
          .greaterThan(
            createResult.transactionResult().transaction().cancelAfter()
              .map(cancelAfter -> cancelAfter.plus(UnsignedLong.valueOf(5)))
              .orElse(UnsignedLong.MAX_VALUE)
          )
    );

    //////////////////////
    // Sender account cancels the Escrow
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .fee(feeResult.drops().openLedgerFee())
      .sequence(senderAccountInfo.accountData().sequence().plus(UnsignedInteger.ONE))
      .owner(senderKeyPair.publicKey().deriveAddress())
      .offerSequence(result.transaction().sequence())
      .signingPublicKey(senderKeyPair.publicKey())
      .build();

    SingleSignedTransaction<EscrowCancel> signedEscrowCancel = signatureService.sign(
      senderKeyPair.privateKey(), escrowCancel
    );
    SubmitResult<EscrowCancel> cancelResult = xrplClient.submit(signedEscrowCancel);
    assertThat(cancelResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(cancelResult);

    //////////////////////
    // Wait until the transaction enters a validated ledger
    this.scanForResult(() -> this.getValidatedTransaction(cancelResult.transactionResult().hash(), EscrowCancel.class));

    //////////////////////
    // Ensure that the funds were released to the sender.
    this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().balance().equals(
        senderAccountInfo.accountData().balance()
          .minus(feeResult.drops().openLedgerFee().times(XrpCurrencyAmount.of(UnsignedLong.valueOf(2))))
      )
    );

  }

  private void assertEntryEqualsObjectFromAccountObjects(
    Address escrowOwner,
    UnsignedInteger createSequence
  ) throws JsonRpcClientErrorException {
    EscrowObject escrowObject = (EscrowObject) this.scanForResult(
      () -> {
        try {
          return xrplClient.accountObjects(AccountObjectsRequestParams.builder()
            .type(AccountObjectType.ESCROW)
            .account(escrowOwner)
            .ledgerSpecifier(LedgerSpecifier.VALIDATED)
            .build()
          ).accountObjects();
        } catch (JsonRpcClientErrorException e) {
          throw new RuntimeException(e);
        }
      },
      result -> result.size() == 1
    ).get(0);

    LedgerEntryResult<EscrowObject> escrowEntry = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.escrow(
        EscrowLedgerEntryParams.builder()
          .owner(escrowOwner)
          .seq(createSequence)
          .build(),
        LedgerSpecifier.VALIDATED
      )
    );

    assertThat(escrowEntry.node()).isEqualTo(escrowObject);

    LedgerEntryResult<EscrowObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(escrowObject.index(), EscrowObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(escrowEntry.node());

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(escrowObject.index(), LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());
  }
}
