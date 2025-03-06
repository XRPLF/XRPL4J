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
import org.xrpl.xrpl4j.codec.addresses.KeyType;
import org.xrpl.xrpl4j.crypto.keys.PrivateKeyReference;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.MultiSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.transactions.SubmitMultiSignedResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.SignerEntry;
import org.xrpl.xrpl4j.model.ledger.SignerEntryWrapper;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Signer;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Integration tests for submitting payment transactions to the XRPL using a {@link SignatureService} that uses
 * instances of {@link PrivateKeyReference} for all signing operations.
 */
public class TransactUsingDerivedKeySignatureServiceIT extends AbstractIT {

  @Test
  public void sendPaymentFromEd25519Account() throws JsonRpcClientErrorException, JsonProcessingException {
    // We must use a random key identifier here rather than a hardcoded identifier because the keypair associated
    // with the hard coded identifier is deterministic. When we run ITs on a real network in CI like testnet or devnet,
    // we sometimes see `tefPAST_SEQ` errors when submitting the transaction because another CI job has submitted
    // a transaction for the account between when this test gets the source's account info and when it submits the
    // transaction. Using a random account every time ensures this test's behavior is isolated from other tests.
    final PrivateKeyReference sourceKeyMetadata = constructPrivateKeyReference(
      UUID.randomUUID().toString(),
      KeyType.ED25519
    );
    final PublicKey sourceWalletPublicKey = derivedKeySignatureService.derivePublicKey(sourceKeyMetadata);
    final Address sourceWalletAddress = sourceWalletPublicKey.deriveAddress();
    this.fundAccount(sourceWalletAddress);

    final PrivateKeyReference destinationKeyMetadata = constructPrivateKeyReference(
      "destinationWallet", KeyType.ED25519
    );
    final PublicKey destinationWalletPublicKey = derivedKeySignatureService.derivePublicKey(
      destinationKeyMetadata);
    final Address destinationWalletAddress = destinationWalletPublicKey.deriveAddress();
    this.fundAccount(destinationWalletAddress);

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this.scanForResult(() -> this.getValidatedAccountInfo(sourceWalletAddress));
    Payment payment = Payment.builder()
      .account(sourceWalletAddress)
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(destinationWalletAddress)
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .signingPublicKey(sourceWalletPublicKey)
      .build();

    SingleSignedTransaction<Payment> signedTransaction = derivedKeySignatureService.sign(sourceKeyMetadata,
      payment);
    SubmitResult<Payment> result = xrplClient.submit(signedTransaction);
    assertThat(result.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(result);

    this.scanForResult(() -> this.getValidatedTransaction(result.transactionResult().hash(), Payment.class));
  }

  @Test
  public void sendPaymentFromSecp256k1Account() throws JsonRpcClientErrorException, JsonProcessingException {
    // We must use a random key identifier here rather than a hardcoded identifier because the keypair associated
    // with the hard coded identifier is deterministic. When we run ITs on a real network in CI like testnet or devnet,
    // we sometimes see `tefPAST_SEQ` errors when submitting the transaction because another CI job has submitted
    // a transaction for the account between when this test gets the source's account info and when it submits the
    // transaction. Using a random account every time ensures this test's behavior is isolated from other tests.
    final PrivateKeyReference sourceKeyMetadata = constructPrivateKeyReference(
      UUID.randomUUID().toString(),
      KeyType.SECP256K1
    );
    final PublicKey sourceWalletPublicKey = derivedKeySignatureService.derivePublicKey(sourceKeyMetadata);
    final Address sourceWalletAddress = sourceWalletPublicKey.deriveAddress();
    this.fundAccount(sourceWalletAddress);

    final PrivateKeyReference destinationKeyMetadata
      = constructPrivateKeyReference("destinationWallet", KeyType.SECP256K1);
    final PublicKey destinationWalletPublicKey = derivedKeySignatureService.derivePublicKey(destinationKeyMetadata);
    final Address destinationWalletAddress = destinationWalletPublicKey.deriveAddress();
    this.fundAccount(destinationWalletAddress);

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this
      .scanForResult(() -> this.getValidatedAccountInfo(sourceWalletAddress));
    Payment payment = Payment.builder()
      .account(sourceWalletAddress)
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(destinationWalletAddress)
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .signingPublicKey(sourceWalletPublicKey)
      .build();

    SingleSignedTransaction<Payment> transactionWithSignature
      = derivedKeySignatureService.sign(sourceKeyMetadata, payment);
    SubmitResult<Payment> result = xrplClient.submit(transactionWithSignature);
    assertThat(result.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(result);

    this.scanForResult(() -> this.getValidatedTransaction(result.transactionResult().hash(), Payment.class));
  }

  @Test
  void multiSigSendPaymentFromEd25519Account() throws JsonRpcClientErrorException, JsonProcessingException {
    // Create four accounts: one for the source account; two for the signers; one for the destination
    PrivateKeyReference sourcePrivateKey = createRandomPrivateKeyReferenceEd25519();
    fundAccount(toAddress(sourcePrivateKey));

    PrivateKeyReference alicePrivateKey = createRandomPrivateKeyReferenceEd25519();
    fundAccount(toAddress(alicePrivateKey));

    PrivateKeyReference bobPrivateKey = createRandomPrivateKeyReferenceEd25519();
    fundAccount(toAddress(bobPrivateKey));

    PrivateKeyReference destinationPrivateKey = createRandomPrivateKeyReferenceEd25519();
    fundAccount(toAddress(destinationPrivateKey));

    this.multiSigSendPaymentHelper(sourcePrivateKey, alicePrivateKey, bobPrivateKey, destinationPrivateKey);
  }

  @Test
  void multiSigSendPaymentFromSecp256k1Account() throws JsonRpcClientErrorException, JsonProcessingException {
    // Create four accounts: one for the source account; two for the signers; one for the destination
    PrivateKeyReference sourcePrivateKey = createRandomPrivateKeyReferenceSecp256k1();
    fundAccount(toAddress(sourcePrivateKey));

    PrivateKeyReference alicePrivateKey = createRandomPrivateKeyReferenceSecp256k1();
    fundAccount(toAddress(alicePrivateKey));

    PrivateKeyReference bobPrivateKey = createRandomPrivateKeyReferenceSecp256k1();
    fundAccount(toAddress(bobPrivateKey));

    PrivateKeyReference destinationPrivateKey = createRandomPrivateKeyReferenceSecp256k1();
    fundAccount(toAddress(destinationPrivateKey));

    this.multiSigSendPaymentHelper(sourcePrivateKey, alicePrivateKey, bobPrivateKey, destinationPrivateKey);
  }

  /**
   * Helper to send a multisign payment using a designated {@link SignatureService}.
   */
  private void multiSigSendPaymentHelper(
    final PrivateKeyReference sourcePrivateKeyReference,
    final PrivateKeyReference alicePrivateKeyReference,
    final PrivateKeyReference bobKeyPrivateKeyReference,
    final PrivateKeyReference destinationPrivateKeyReference
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    Objects.requireNonNull(sourcePrivateKeyReference);
    Objects.requireNonNull(alicePrivateKeyReference);
    Objects.requireNonNull(bobKeyPrivateKeyReference);
    Objects.requireNonNull(destinationPrivateKeyReference);

    /////////////////////////////
    // Wait for all accounts to show up in a validated ledger
    final AccountInfoResult sourceAccountInfo = scanForResult(
      () -> this.getValidatedAccountInfo(
        toPublicKey(sourcePrivateKeyReference).deriveAddress())
    );
    scanForResult(() -> this.getValidatedAccountInfo(
      toPublicKey(alicePrivateKeyReference).deriveAddress())
    );
    scanForResult(() -> this.getValidatedAccountInfo(
      toPublicKey(bobKeyPrivateKeyReference).deriveAddress())
    );
    scanForResult(() -> this.getValidatedAccountInfo(
      toPublicKey(destinationPrivateKeyReference).deriveAddress())
    );

    /////////////////////////////
    // And validate that the source account has not set up any signer lists
    assertThat(sourceAccountInfo.accountData().signerLists()).isEmpty();

    /////////////////////////////
    // Then submit a SignerListSet transaction to add alice and bob as signers on the account
    FeeResult feeResult = xrplClient.fee();
    SignerListSet signerListSet = SignerListSet.builder()
      .account(toAddress(sourcePrivateKeyReference))
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(sourceAccountInfo.accountData().sequence())
      .signerQuorum(UnsignedInteger.valueOf(2))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(toAddress(alicePrivateKeyReference))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(toAddress(bobKeyPrivateKeyReference))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .signingPublicKey(toPublicKey(sourcePrivateKeyReference))
      .build();

    SingleSignedTransaction<SignerListSet> signedSignerListSet = derivedKeySignatureService.sign(
      sourcePrivateKeyReference, signerListSet
    );
    SubmitResult<SignerListSet> signerListSetResult = xrplClient.submit(signedSignerListSet);
    assertThat(signerListSetResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(signerListSetResult);

    /////////////////////////////
    // Then wait until the transaction enters a validated ledger and the source account's signer list
    // exists
    AccountInfoResult sourceAccountInfoAfterSignerListSet = scanForResult(
      () -> this.getValidatedAccountInfo(toAddress(sourcePrivateKeyReference)),
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

    /////////////////////////////
    // Construct an unsigned Payment transaction to be multisigned
    Payment unsignedPayment = Payment.builder()
      .account(toAddress(sourcePrivateKeyReference))
      .fee(
        FeeUtils.computeMultisigNetworkFees(
          feeResult,
          sourceAccountInfoAfterSignerListSet.accountData().signerLists().get(0)
        ).recommendedFee()
      )
      .sequence(sourceAccountInfoAfterSignerListSet.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .destination(toAddress(destinationPrivateKeyReference))
      .build();

    /////////////////////////////
    // Alice and Bob sign the transaction with their private keys using the "multiSign" method.
    Set<Signer> signers = Lists.newArrayList(alicePrivateKeyReference, bobKeyPrivateKeyReference)
      .stream()
      .map(privateKeyReference -> derivedKeySignatureService.multiSignToSigner(privateKeyReference, unsignedPayment))
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
}
