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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.MultiSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.transactions.SubmitMultiSignedResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.SignerEntry;
import org.xrpl.xrpl4j.model.ledger.SignerEntryWrapper;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Signer;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.TransactionResultCodes;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SubmitMultisignedIT extends AbstractIT {

  /////////////////////////////
  // Create four accounts, one for the multisign account owner, one for their two friends,
  // and one to send a Payment to.
  KeyPair sourceKeyPair = createRandomAccountEd25519();
  KeyPair aliceKeyPair = createRandomAccountEd25519();
  KeyPair bobKeyPair = createRandomAccountEd25519();
  KeyPair destinationKeyPair = createRandomAccountEd25519();

  FeeResult feeResult;
  AccountInfoResult sourceAccountInfoAfterSignerListSet;
  SubmitResult<SignerListSet> signerListSetResult;

  @BeforeEach
  public void setUp() throws JsonRpcClientErrorException, JsonProcessingException {

    /////////////////////////////
    // Wait for all of the accounts to show up in a validated ledger
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
    feeResult = xrplClient.fee();
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

    /////////////////////////////
    // Validate that the transaction was submitted successfully
    SingleSignedTransaction<SignerListSet> signedSignerListSet = signatureService.sign(
      sourceKeyPair.privateKey(), signerListSet
    );
    signerListSetResult = xrplClient.submit(signedSignerListSet);
    assertThat(signerListSetResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
    assertThat(signedSignerListSet.hash()).isEqualTo(signerListSetResult.transactionResult().hash());
    logSubmitResult(signerListSetResult);

    /////////////////////////////
    // Then wait until the transaction enters a validated ledger and the source account's signer list
    // exists
    sourceAccountInfoAfterSignerListSet = scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().signerLists().size() == 1
    );

    assertThat(
      sourceAccountInfoAfterSignerListSet.accountData().signerLists().get(0)
        .signerEntries().stream()
        .sorted(Comparator.comparing(entry -> entry.signerEntry().account()))
        .collect(Collectors.toList())
    ).isEqualTo(signerListSet.signerEntries().stream()
      .sorted(Comparator.comparing(entry -> entry.signerEntry().account()))
      .collect(Collectors.toList()));
  }

  @Test
  public void submitMultisignedAndVerifyHash() throws JsonRpcClientErrorException, JsonProcessingException {

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
    // Alice and Bob sign the transaction with their private keys
    List<Signer> signers = Lists.newArrayList(aliceKeyPair, bobKeyPair).stream()
      .map(keyPair -> signatureService.multiSignToSigner(keyPair.privateKey(), unsignedPayment))
      .collect(Collectors.toList());

    /////////////////////////////
    // Then we add the signatures to the Payment object and submit it
    MultiSignedTransaction<Payment> signedTransaction = MultiSignedTransaction.<Payment>builder()
      .unsignedTransaction(unsignedPayment)
      .signerSet(signers)
      .build();

    String libraryCalculatedHash = signedTransaction.hash().value();

    SubmitMultiSignedResult<Payment> submitMultiSignedResult = xrplClient.submitMultisigned(signedTransaction);
    assertThat(submitMultiSignedResult.transaction().hash().value()).isEqualTo(libraryCalculatedHash);
    assertThat(submitMultiSignedResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
    logSubmitResult(submitMultiSignedResult);
  }

  @Test
  public void submitMultisignedWithSignersInDescOrderAndVerifyHash() throws JsonRpcClientErrorException {

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
    // Alice and Bob sign the transaction with their private keys
    List<Signer> signers = Lists.newArrayList(aliceKeyPair, bobKeyPair).stream()
      .map(keyPair -> signatureService.multiSignToSigner(keyPair.privateKey(), unsignedPayment))
      .collect(Collectors.toList());

    /////////////////////////////
    // Then we add the signatures to the Payment object and submit it
    MultiSignedTransaction<Payment> signedTransaction = MultiSignedTransaction.<Payment>builder()
      .unsignedTransaction(unsignedPayment)
      .signerSet(signers)
      .build();

    String libraryCalculatedHash = signedTransaction.hash().value();

    SubmitMultiSignedResult<Payment> submitMultiSignedResult = xrplClient.submitMultisigned(signedTransaction);
    assertThat(submitMultiSignedResult.transaction().hash().value()).isEqualTo(libraryCalculatedHash);
    assertThat(submitMultiSignedResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
    logSubmitResult(submitMultiSignedResult);

  }
}
