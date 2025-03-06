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
import org.xrpl.xrpl4j.crypto.keys.PrivateKey;
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
import java.util.stream.Collectors;

/**
 * Integration tests for submitting payment transactions to the XRPL using a {@link SignatureService} that uses *
 * instances of {@link PrivateKey}  for all signing operations.
 */
public class TransactUsingSignatureServiceIT extends AbstractIT {

  @Test
  public void sendPaymentFromEd25519Wallet() throws JsonRpcClientErrorException, JsonProcessingException {
    final PrivateKey sourcePrivateKey = constructPrivateKey("sourceWallet", KeyType.ED25519);
    final PublicKey sourcePublicKey = signatureService.derivePublicKey(sourcePrivateKey);
    final Address sourceWalletAddress = sourcePublicKey.deriveAddress();
    this.fundAccount(sourceWalletAddress);

    final PrivateKey destinationPrivateKey = constructPrivateKey("destinationWallet", KeyType.ED25519);
    final PublicKey destinationWalletPublicKey = signatureService.derivePublicKey(destinationPrivateKey);
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
      .signingPublicKey(sourcePublicKey)
      .build();

    SingleSignedTransaction<Payment> signedTransaction = signatureService.sign(sourcePrivateKey, payment);
    SubmitResult<Payment> result = xrplClient.submit(signedTransaction);
    assertThat(result.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(result);

    this.scanForResult(() -> this.getValidatedTransaction(result.transactionResult().hash(), Payment.class));
  }

  @Test
  public void sendPaymentFromSecp256k1Wallet() throws JsonRpcClientErrorException, JsonProcessingException {
    final PrivateKey sourcePrivateKey = constructPrivateKey("sourceWallet", KeyType.SECP256K1);
    final PublicKey sourceWalletPublicKey = signatureService.derivePublicKey(sourcePrivateKey);
    final Address sourceWalletAddress = sourceWalletPublicKey.deriveAddress();
    this.fundAccount(sourceWalletAddress);

    final PrivateKey destinationPrivateKey = constructPrivateKey("destinationWallet", KeyType.SECP256K1);
    final PublicKey destinationWalletPublicKey = signatureService.derivePublicKey(destinationPrivateKey);
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

    SingleSignedTransaction<Payment> transactionWithSignature = signatureService.sign(sourcePrivateKey, payment);
    SubmitResult<Payment> result = xrplClient.submit(transactionWithSignature);
    assertThat(result.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(result);

    this.scanForResult(() -> this.getValidatedTransaction(result.transactionResult().hash(), Payment.class));
  }

  @Test
  void multiSigPaymentFromEd25519Wallet() throws JsonRpcClientErrorException, JsonProcessingException {
    // Create four accounts: one for the source account; two for the signers; one for the destination
    PrivateKey sourcePrivateKey = constructPrivateKey("source", KeyType.ED25519);
    fundAccount(toAddress(sourcePrivateKey));

    PrivateKey alicePrivateKey = constructPrivateKey("alice", KeyType.ED25519);
    fundAccount(toAddress(alicePrivateKey));

    PrivateKey bobPrivateKey = constructPrivateKey("bob", KeyType.ED25519);
    fundAccount(toAddress(bobPrivateKey));

    PrivateKey destinationPrivateKey = constructPrivateKey("destination", KeyType.ED25519);
    fundAccount(toAddress(destinationPrivateKey));

    this.multiSigSendPaymentHelper(sourcePrivateKey, alicePrivateKey, bobPrivateKey, destinationPrivateKey);
  }

  @Test
  void multiSigPaymentFromSecp256k1Wallet() throws JsonRpcClientErrorException, JsonProcessingException {
    // Create four accounts: one for the source account; two for the signers; one for the destination
    PrivateKey sourcePrivateKey = constructPrivateKey("source", KeyType.SECP256K1);
    fundAccount(toAddress(sourcePrivateKey));

    PrivateKey alicePrivateKey = constructPrivateKey("alice", KeyType.SECP256K1);
    fundAccount(toAddress(alicePrivateKey));

    PrivateKey bobPrivateKey = constructPrivateKey("bob", KeyType.SECP256K1);
    fundAccount(toAddress(bobPrivateKey));

    PrivateKey destinationPrivateKey = constructPrivateKey("destination", KeyType.SECP256K1);
    fundAccount(toAddress(destinationPrivateKey));

    this.multiSigSendPaymentHelper(sourcePrivateKey, alicePrivateKey, bobPrivateKey, destinationPrivateKey);
  }

  /**
   * Helper to send a multisign payment using a designated {@link SignatureService}.
   */
  private void multiSigSendPaymentHelper(
    final PrivateKey sourcePrivateKey,
    final PrivateKey alicePrivateKey,
    final PrivateKey bobPrivateKey,
    final PrivateKey destinationPrivateKey
  ) throws JsonRpcClientErrorException, JsonProcessingException {

    Objects.requireNonNull(sourcePrivateKey);
    Objects.requireNonNull(alicePrivateKey);
    Objects.requireNonNull(bobPrivateKey);
    Objects.requireNonNull(destinationPrivateKey);

    /////////////////////////////
    // Wait for all accounts to show up in a validated ledger
    final AccountInfoResult sourceAccountInfo = scanForResult(
      () -> this.getValidatedAccountInfo(toAddress(sourcePrivateKey))
    );
    scanForResult(() -> this.getValidatedAccountInfo(toAddress(alicePrivateKey)));
    scanForResult(() -> this.getValidatedAccountInfo(toAddress(bobPrivateKey)));
    scanForResult(() -> this.getValidatedAccountInfo(toAddress(destinationPrivateKey)));

    /////////////////////////////
    // And validate that the source account has not set up any signer lists
    assertThat(sourceAccountInfo.accountData().signerLists()).isEmpty();

    /////////////////////////////
    // Then submit a SignerListSet transaction to add alice and bob as signers on the account
    FeeResult feeResult = xrplClient.fee();
    SignerListSet signerListSet = SignerListSet.builder()
      .account(toAddress(sourcePrivateKey))
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(sourceAccountInfo.accountData().sequence())
      .signerQuorum(UnsignedInteger.valueOf(2))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(toAddress(alicePrivateKey))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(toAddress(bobPrivateKey))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .signingPublicKey(toPublicKey(sourcePrivateKey))
      .build();

    SingleSignedTransaction<SignerListSet> signedSignerListSet = signatureService.sign(
      sourcePrivateKey, signerListSet
    );
    SubmitResult<SignerListSet> signerListSetResult = xrplClient.submit(signedSignerListSet);
    assertThat(signerListSetResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(signerListSetResult);

    /////////////////////////////
    // Then wait until the transaction enters a validated ledger and the source account's signer list
    // exists
    AccountInfoResult sourceAccountInfoAfterSignerListSet = scanForResult(
      () -> this.getValidatedAccountInfo(toAddress(sourcePrivateKey)),
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
      .account(toAddress(sourcePrivateKey))
      .fee(
        FeeUtils.computeMultisigNetworkFees(
          feeResult,
          sourceAccountInfoAfterSignerListSet.accountData().signerLists().get(0)
        ).recommendedFee()
      )
      .sequence(sourceAccountInfoAfterSignerListSet.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .destination(toAddress(destinationPrivateKey))
      .build();

    /////////////////////////////
    // Alice and Bob sign the transaction with their private keys using the "multiSign" method.
    Set<Signer> signers = Lists.newArrayList(alicePrivateKey, bobPrivateKey).stream()
      .map(privateKey -> signatureService.multiSignToSigner(privateKey, unsignedPayment))
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

  private PublicKey toPublicKey(final PrivateKey privateKey) {
    Objects.requireNonNull(privateKey);
    return signatureService.derivePublicKey(privateKey);
  }

  private Address toAddress(final PrivateKey privateKey) {
    return toPublicKey(privateKey).deriveAddress();
  }
}
