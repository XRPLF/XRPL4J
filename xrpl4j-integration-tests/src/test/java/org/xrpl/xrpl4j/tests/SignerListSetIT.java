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
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.MultiSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitMultiSignedResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.ledger.AccountRootObject;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.ledger.SignerEntry;
import org.xrpl.xrpl4j.model.ledger.SignerEntryWrapper;
import org.xrpl.xrpl4j.model.ledger.SignerListObject;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Signer;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.metadata.AffectedNode;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaLedgerEntryType;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Integration tests to validate submission of SignerListSet transactions.
 */
public class SignerListSetIT extends AbstractIT {

  @Test
  void addSignersToSignerListAndSendPayment() throws JsonRpcClientErrorException, JsonProcessingException {
    /////////////////////////////
    // Create four accounts, one for the multisign account owner, one for their two friends,
    // and one to send a Payment to.
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair aliceKeyPair = createRandomAccountEd25519();
    KeyPair bobKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    /////////////////////////////
    // Wait for all accounts to show up in a validated ledger
    final AccountInfoResult sourceAccountInfo = scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );
    scanForResult(() -> this.getValidatedAccountInfo(aliceKeyPair.publicKey().deriveAddress()));
    scanForResult(() -> this.getValidatedAccountInfo(bobKeyPair.publicKey().deriveAddress()));
    scanForResult(() -> this.getValidatedAccountInfo(destinationKeyPair.publicKey().deriveAddress()));

    /////////////////////////////
    // And validate that the source account has not set up any signer lists
    assertThat(sourceAccountInfo.accountData().signerLists()).isEmpty();

    /////////////////////////////
    // Then submit a SignerListSet transaction to add alice and bob as signers on the account
    FeeResult feeResult = xrplClient.fee();
    SignerListSet signerListSet = SignerListSet.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(sourceAccountInfo.accountData().sequence())
      .signerQuorum(UnsignedInteger.valueOf(2))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(aliceKeyPair.publicKey().deriveAddress())
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(bobKeyPair.publicKey().deriveAddress())
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<SignerListSet> signedSignerListSet = signatureService.sign(
      sourceKeyPair.privateKey(), signerListSet
    );
    SubmitResult<SignerListSet> signerListSetResult = xrplClient.submit(signedSignerListSet);
    assertThat(signerListSetResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(signerListSetResult);

    /////////////////////////////
    // Then wait until the transaction enters a validated ledger and the source account's signer list
    // exists
    AccountInfoResult sourceAccountInfoAfterSignerListSet = scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().signerLists().size() == 1
    );

    assertSignerListEntryEqualsAccountInfo(signedSignerListSet.hash(), sourceAccountInfoAfterSignerListSet);

    assertThat(
      sourceAccountInfoAfterSignerListSet.accountData().signerLists().get(0)
        .signerEntries().stream()
        .sorted(Comparator.comparing(entry -> entry.signerEntry().account()))
        .collect(Collectors.toList())
    ).isEqualTo(signerListSet.signerEntries().stream()
      .sorted(Comparator.comparing(entry -> entry.signerEntry().account()))
      .collect(Collectors.toList()));

    /////////////////////////////
    // Construct an unsigned Payment transaction to be multisigned
    Payment unsignedPayment = Payment.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(
        FeeUtils.computeMultisigNetworkFees(
          feeResult,
          sourceAccountInfoAfterSignerListSet.accountData().signerLists().get(0)
        ).recommendedFee()
      )
      .sequence(sourceAccountInfoAfterSignerListSet.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .build();

    /////////////////////////////
    // Alice and Bob sign the transaction with their private keys using the "multiSign" method.
    Set<Signer> signers = Lists.newArrayList(aliceKeyPair, bobKeyPair).stream()
      .map(wallet -> signatureService.multiSignToSigner(wallet.privateKey(), unsignedPayment))
      .collect(Collectors.toSet());

    /////////////////////////////
    // Then we add the signatures to the Payment object and submit it
    MultiSignedTransaction<Payment> multiSigPayment = MultiSignedTransaction.<Payment>builder()
      .unsignedTransaction(unsignedPayment)
      .signerSet(signers)
      .build();

    SubmitMultiSignedResult<Payment> paymentResult = xrplClient.submitMultisigned(multiSigPayment);
    assertThat(paymentResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(paymentResult);
  }

  @Test
  void addSignersToSignerListThenDeleteSignerList() throws JsonRpcClientErrorException, JsonProcessingException {
    /////////////////////////////
    // Create three accounts, one for the multisign account owner, one for their two friends
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair aliceKeyPair = createRandomAccountEd25519();
    KeyPair bobKeyPair = createRandomAccountEd25519();

    /////////////////////////////
    // Wait for all accounts to show up in a validated ledger
    AccountInfoResult sourceAccountInfo = scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );
    scanForResult(() -> this.getValidatedAccountInfo(aliceKeyPair.publicKey().deriveAddress()));
    scanForResult(() -> this.getValidatedAccountInfo(bobKeyPair.publicKey().deriveAddress()));

    /////////////////////////////
    // And validate that the source account has not set up any signer lists
    assertThat(sourceAccountInfo.accountData().signerLists()).isEmpty();

    /////////////////////////////
    // Then submit a SignerListSet transaction to add alice and bob as signers on the account
    FeeResult feeResult = xrplClient.fee();
    SignerListSet signerListSet = SignerListSet.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(sourceAccountInfo.accountData().sequence())
      .signerQuorum(UnsignedInteger.valueOf(2))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(aliceKeyPair.publicKey().deriveAddress())
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(bobKeyPair.publicKey().deriveAddress())
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<SignerListSet> signedSignerListSet = signatureService.sign(
      sourceKeyPair.privateKey(), signerListSet
    );
    SubmitResult<SignerListSet> signerListSetResult = xrplClient.submit(signedSignerListSet);
    assertThat(signerListSetResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(signerListSetResult);

    /////////////////////////////
    // Then wait until the transaction enters a validated ledger and the source account's signer list
    // exists
    AccountInfoResult sourceAccountInfoAfterSignerListSet = scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().signerLists().size() == 1
    );

    assertSignerListEntryEqualsAccountInfo(signedSignerListSet.hash(), sourceAccountInfoAfterSignerListSet);

    assertThat(
      sourceAccountInfoAfterSignerListSet.accountData().signerLists().get(0)
        .signerEntries().stream()
        .sorted(Comparator.comparing(entry -> entry.signerEntry().account()))
        .collect(Collectors.toList())
    ).isEqualTo(signerListSet.signerEntries().stream()
      .sorted(Comparator.comparing(entry -> entry.signerEntry().account()))
      .collect(Collectors.toList()));

    /////////////////////////////
    // Construct a SignerListSet transaction with 0 quorum and an empty list of signer entries to
    // delete the signer list
    SignerListSet deleteSignerList = SignerListSet.builder()
      .from(signerListSet)
      .signerQuorum(UnsignedInteger.ZERO)
      .signerEntries(Collections.emptyList())
      .sequence(sourceAccountInfoAfterSignerListSet.accountData().sequence())
      .build();

    SingleSignedTransaction<SignerListSet> signedDeleteSignerList = signatureService.sign(
      sourceKeyPair.privateKey(), deleteSignerList
    );
    SubmitResult<SignerListSet> signerListDeleteResult = xrplClient.submit(signedDeleteSignerList);
    assertThat(signerListDeleteResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(signerListDeleteResult);

    /////////////////////////////
    // Then wait until the transaction enters a validated ledger and the signer list has been deleted
    scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().signerLists().size() == 0
    );

  }

  private void assertSignerListEntryEqualsAccountInfo(Hash256 signerListSetTx, AccountInfoResult accountInfo)
    throws JsonRpcClientErrorException {

    TransactionResult<SignerListSet> signerListSet = this.getValidatedTransaction(signerListSetTx,
      SignerListSet.class);
    Hash256 signerListId = signerListSet.metadata().get()
      .affectedNodes()
      .stream()
      .filter(affectedNode -> affectedNode.ledgerEntryType().equals(MetaLedgerEntryType.SIGNER_LIST))
      .findFirst()
      .map(AffectedNode::ledgerIndex)
      .get();

    LedgerEntryResult<SignerListObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(signerListId, SignerListObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(accountInfo.accountData().signerLists().get(0));

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(signerListId, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());
  }
}
