package org.xrpl.xrpl4j.crypto.signing;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.client.channels.UnsignedClaim;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;
import org.xrpl.xrpl4j.model.ledger.Attestation;
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.AmmBid;
import org.xrpl.xrpl4j.model.transactions.AmmClawback;
import org.xrpl.xrpl4j.model.transactions.AmmCreate;
import org.xrpl.xrpl4j.model.transactions.AmmDelete;
import org.xrpl.xrpl4j.model.transactions.AmmDeposit;
import org.xrpl.xrpl4j.model.transactions.AmmVote;
import org.xrpl.xrpl4j.model.transactions.AmmWithdraw;
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.Clawback;
import org.xrpl.xrpl4j.model.transactions.DepositPreAuth;
import org.xrpl.xrpl4j.model.transactions.DidDelete;
import org.xrpl.xrpl4j.model.transactions.DidSet;
import org.xrpl.xrpl4j.model.transactions.EscrowCancel;
import org.xrpl.xrpl4j.model.transactions.EscrowCreate;
import org.xrpl.xrpl4j.model.transactions.EscrowFinish;
import org.xrpl.xrpl4j.model.transactions.NfTokenAcceptOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenBurn;
import org.xrpl.xrpl4j.model.transactions.NfTokenCancelOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenCreateOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenMint;
import org.xrpl.xrpl4j.model.transactions.OfferCancel;
import org.xrpl.xrpl4j.model.transactions.OfferCreate;
import org.xrpl.xrpl4j.model.transactions.OracleDelete;
import org.xrpl.xrpl4j.model.transactions.OracleSet;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.SetRegularKey;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.SignerWrapper;
import org.xrpl.xrpl4j.model.transactions.TicketCreate;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
import org.xrpl.xrpl4j.model.transactions.XChainAccountCreateCommit;
import org.xrpl.xrpl4j.model.transactions.XChainAddAccountCreateAttestation;
import org.xrpl.xrpl4j.model.transactions.XChainAddClaimAttestation;
import org.xrpl.xrpl4j.model.transactions.XChainClaim;
import org.xrpl.xrpl4j.model.transactions.XChainCommit;
import org.xrpl.xrpl4j.model.transactions.XChainCreateBridge;
import org.xrpl.xrpl4j.model.transactions.XChainCreateClaimId;
import org.xrpl.xrpl4j.model.transactions.XChainModifyBridge;

import java.util.List;
import java.util.Objects;

/**
 * Utility methods to help with generating, validating, and manipulating digital signatures.
 */
public class SignatureUtils {

  private static final SignatureUtils INSTANCE = new SignatureUtils(
    ObjectMapperFactory.create(),
    XrplBinaryCodec.getInstance()
  );

  /**
   * Obtain the singleton instance of {@link SignatureUtils}.
   *
   * @return An {@link SignatureUtils}.
   */
  public static SignatureUtils getInstance() {
    return INSTANCE;
  }

  private final ObjectMapper objectMapper;
  private final XrplBinaryCodec binaryCodec;

  /**
   * Required-args constructor.
   *
   * @param objectMapper A {@link ObjectMapper}.
   * @param binaryCodec  A {@link XrplBinaryCodec}.
   */
  public SignatureUtils(final ObjectMapper objectMapper, final XrplBinaryCodec binaryCodec) {
    this.objectMapper = Objects.requireNonNull(objectMapper);
    this.binaryCodec = Objects.requireNonNull(binaryCodec);
  }

  /**
   * Helper method to convert a {@link Transaction} into bytes that can be used directly for signing.
   *
   * @param transaction A {@link Transaction} to be signed.
   *
   * @return An {@link UnsignedByteArray}.
   */
  public UnsignedByteArray toSignableBytes(final Transaction transaction) {
    Objects.requireNonNull(transaction);
    try {
      final String unsignedJson = objectMapper.writeValueAsString(transaction);
      final String unsignedBinaryHex = binaryCodec.encodeForSigning(unsignedJson);
      return UnsignedByteArray.fromHex(unsignedBinaryHex);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Helper method to convert an {@link UnsignedClaim} into bytes that can be used directly for signing.
   *
   * @param unsignedClaim An {@link UnsignedClaim} to be signed.
   *
   * @return An {@link UnsignedByteArray}.
   */
  public UnsignedByteArray toSignableBytes(final UnsignedClaim unsignedClaim) {
    Objects.requireNonNull(unsignedClaim);
    try {
      final String unsignedJson = objectMapper.writeValueAsString(unsignedClaim);
      final String unsignedBinaryHex = binaryCodec.encodeForSigningClaim(unsignedJson);
      return UnsignedByteArray.fromHex(unsignedBinaryHex);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Helper method to convert an {@link Attestation} into bytes that can be used directly for signing.
   *
   * <p>This method will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   *
   * @param attestation An {@link Attestation} to be signed.
   *
   * @return An {@link UnsignedByteArray}.
   */
  @Beta
  public UnsignedByteArray toSignableBytes(final Attestation attestation) {
    Objects.requireNonNull(attestation);
    try {
      final String unsignedJson = objectMapper.writeValueAsString(attestation);
      final String unsignedBinaryHex = binaryCodec.encode(unsignedJson);
      return UnsignedByteArray.fromHex(unsignedBinaryHex);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Helper method to convert a {@link Transaction} into bytes that can be signed by multiple signers, as is the case
   * when the source account has set a SignerList.
   *
   * @param transaction   A {@link Transaction} to be signed.
   * @param signerAddress The {@link Address} of the signer of the transaction.
   *
   * @return An {@link UnsignedByteArray}.
   */
  public UnsignedByteArray toMultiSignableBytes(final Transaction transaction, final Address signerAddress) {
    Objects.requireNonNull(transaction);
    Objects.requireNonNull(signerAddress);

    try {
      final String unsignedJson = objectMapper.writeValueAsString(transaction);
      final String unsignedBinaryHex = binaryCodec.encodeForMultiSigning(unsignedJson, signerAddress.value());
      return UnsignedByteArray.fromHex(unsignedBinaryHex);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Add {@link Transaction#transactionSignature()} to the given transaction. Because {@link Transaction} is not an
   * Immutable object, it does not have a generated builder like its subclasses do. Thus, this method needs to rebuild
   * transactions based on their runtime type.
   *
   * @param transaction An unsigned {@link Transaction} to add a signature to. Note that
   *                    {@link Transaction#transactionSignature()} must not be provided, and
   *                    {@link Transaction#signingPublicKey()} must be provided.
   * @param signature   A {@link Signature} containing the transaction signature.
   * @param <T>         extends {@link Transaction}.
   *
   * @return A copy of {@code transaction} with the {@link Transaction#transactionSignature()} field added.
   */
  public <T extends Transaction> SingleSignedTransaction<T> addSignatureToTransaction(
    final T transaction, final Signature signature
  ) {
    Objects.requireNonNull(transaction);
    Objects.requireNonNull(signature);

    Preconditions.checkArgument(
      !transaction.transactionSignature().isPresent(),
      "Transactions to be signed must not already include a signature."
    );

    final Transaction transactionWithSignature;
    if (Payment.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = Payment.builder().from((Payment) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AccountSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AccountSet.builder().from((AccountSet) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AccountDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AccountDelete.builder().from((AccountDelete) transaction)
        .transactionSignature(signature)
        .build();
    } else if (CheckCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = CheckCancel.builder().from((CheckCancel) transaction)
        .transactionSignature(signature)
        .build();
    } else if (CheckCash.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = CheckCash.builder().from((CheckCash) transaction)
        .transactionSignature(signature)
        .build();
    } else if (CheckCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = CheckCreate.builder().from((CheckCreate) transaction)
        .transactionSignature(signature)
        .build();
    } else if (DepositPreAuth.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = DepositPreAuth.builder().from((DepositPreAuth) transaction)
        .transactionSignature(signature)
        .build();
    } else if (EscrowCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = EscrowCreate.builder().from((EscrowCreate) transaction)
        .transactionSignature(signature)
        .build();
    } else if (EscrowCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = EscrowCancel.builder().from((EscrowCancel) transaction)
        .transactionSignature(signature)
        .build();
    } else if (EscrowFinish.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = EscrowFinish.builder().from((EscrowFinish) transaction)
        .transactionSignature(signature)
        .build();
    } else if (TrustSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = TrustSet.builder().from((TrustSet) transaction)
        .transactionSignature(signature)
        .build();
    } else if (OfferCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = OfferCreate.builder().from((OfferCreate) transaction)
        .transactionSignature(signature)
        .build();
    } else if (OfferCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = OfferCancel.builder().from((OfferCancel) transaction)
        .transactionSignature(signature)
        .build();
    } else if (PaymentChannelCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = PaymentChannelCreate.builder().from((PaymentChannelCreate) transaction)
        .transactionSignature(signature)
        .build();
    } else if (PaymentChannelClaim.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = PaymentChannelClaim.builder().from((PaymentChannelClaim) transaction)
        .transactionSignature(signature)
        .build();
    } else if (PaymentChannelFund.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = PaymentChannelFund.builder().from((PaymentChannelFund) transaction)
        .transactionSignature(signature)
        .build();
    } else if (SetRegularKey.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = SetRegularKey.builder().from((SetRegularKey) transaction)
        .transactionSignature(signature)
        .build();
    } else if (SignerListSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = SignerListSet.builder().from((SignerListSet) transaction)
        .transactionSignature(signature)
        .build();
    } else if (NfTokenAcceptOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenAcceptOffer.builder().from((NfTokenAcceptOffer) transaction)
        .transactionSignature(signature)
        .build();
    } else if (NfTokenBurn.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenBurn.builder().from((NfTokenBurn) transaction)
        .transactionSignature(signature)
        .build();
    } else if (NfTokenCancelOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenCancelOffer.builder().from((NfTokenCancelOffer) transaction)
        .transactionSignature(signature)
        .build();
    } else if (NfTokenCreateOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenCreateOffer.builder().from((NfTokenCreateOffer) transaction)
        .transactionSignature(signature)
        .build();
    } else if (NfTokenMint.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenMint.builder().from((NfTokenMint) transaction)
        .transactionSignature(signature)
        .build();
    } else if (TicketCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = TicketCreate.builder().from((TicketCreate) transaction)
        .transactionSignature(signature)
        .build();
    } else if (Clawback.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = Clawback.builder().from((Clawback) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmBid.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmBid.builder().from((AmmBid) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmCreate.builder().from((AmmCreate) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmDeposit.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmDeposit.builder().from((AmmDeposit) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmVote.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmVote.builder().from((AmmVote) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmWithdraw.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmWithdraw.builder().from((AmmWithdraw) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmDelete.builder().from((AmmDelete) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainAccountCreateCommit.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainAccountCreateCommit.builder().from((XChainAccountCreateCommit) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainAddAccountCreateAttestation.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainAddAccountCreateAttestation.builder()
        .from((XChainAddAccountCreateAttestation) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainAddClaimAttestation.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainAddClaimAttestation.builder().from((XChainAddClaimAttestation) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainClaim.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainClaim.builder().from((XChainClaim) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainCommit.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainCommit.builder().from((XChainCommit) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainCreateBridge.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainCreateBridge.builder().from((XChainCreateBridge) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainCreateClaimId.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainCreateClaimId.builder().from((XChainCreateClaimId) transaction)
        .transactionSignature(signature)
        .build();
    } else if (XChainModifyBridge.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = XChainModifyBridge.builder().from((XChainModifyBridge) transaction)
        .transactionSignature(signature)
        .build();
    } else if (DidSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = DidSet.builder().from((DidSet) transaction)
        .transactionSignature(signature)
        .build();
    } else if (DidDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = DidDelete.builder().from((DidDelete) transaction)
        .transactionSignature(signature)
        .build();
    } else if (OracleSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = OracleSet.builder().from((OracleSet) transaction)
        .transactionSignature(signature)
        .build();
    } else if (OracleDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = OracleDelete.builder().from((OracleDelete) transaction)
        .transactionSignature(signature)
        .build();
    } else if (AmmClawback.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AmmClawback.builder().from((AmmClawback) transaction)
          .transactionSignature(signature)
          .build();
    } else {
      // Should never happen, but will in a unit test if we miss one.
      throw new IllegalArgumentException("Signing fields could not be added to the transaction.");
    }
    return SingleSignedTransaction.<T>builder()
      .unsignedTransaction(transaction)
      .signature(signature)
      .signedTransaction((T) transactionWithSignature)
      .build();
  }

  /**
   * Add {@link Transaction#signers()}} to the given transaction. Because {@link Transaction} is not an Immutable
   * object, it does not have a generated builder like its subclasses do. Thus, this method needs to rebuild
   * transactions based on their runtime type.
   *
   * @param transaction An unsigned {@link Transaction} to add a signature to. Note that
   *                    {@link Transaction#transactionSignature()} must not be provided, and
   *                    {@link Transaction#signingPublicKey()} must be an empty string.
   * @param signers     A {@link List} of {@link SignerWrapper}s containing the transaction signatures.
   * @param <T>         extends {@link Transaction}.
   *
   * @return A copy of {@code transaction} with the {@link Transaction#signers()}} field added.
   */
  public <T extends Transaction> T addMultiSignaturesToTransaction(T transaction, List<SignerWrapper> signers) {
    Objects.requireNonNull(transaction);
    Objects.requireNonNull(signers);

    Preconditions.checkArgument(
      !transaction.transactionSignature().isPresent(),
      "Transactions to be signed must not already include a signature."
    );
    Preconditions.checkArgument(
      transaction.signingPublicKey().equals(PublicKey.MULTI_SIGN_PUBLIC_KEY),
      "Transactions to be multisigned must set signingPublicKey to an empty String."
    );

    final Transaction transactionWithSignatures;
    if (Payment.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = Payment.builder().from((Payment) transaction)
        .signers(signers)
        .build();
    } else if (AccountSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AccountSet.builder().from((AccountSet) transaction)
        .signers(signers)
        .build();
    } else if (AccountDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AccountDelete.builder().from((AccountDelete) transaction)
        .signers(signers)
        .build();
    } else if (CheckCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = CheckCancel.builder().from((CheckCancel) transaction)
        .signers(signers)
        .build();
    } else if (CheckCash.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = CheckCash.builder().from((CheckCash) transaction)
        .signers(signers)
        .build();
    } else if (CheckCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = CheckCreate.builder().from((CheckCreate) transaction)
        .signers(signers)
        .build();
    } else if (DepositPreAuth.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = DepositPreAuth.builder().from((DepositPreAuth) transaction)
        .signers(signers)
        .build();
    } else if (EscrowCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = EscrowCreate.builder().from((EscrowCreate) transaction)
        .signers(signers)
        .build();
    } else if (EscrowCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = EscrowCancel.builder().from((EscrowCancel) transaction)
        .signers(signers)
        .build();
    } else if (EscrowFinish.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = EscrowFinish.builder().from((EscrowFinish) transaction)
        .signers(signers)
        .build();
    } else if (TrustSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = TrustSet.builder().from((TrustSet) transaction)
        .signers(signers)
        .build();
    } else if (OfferCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = OfferCreate.builder().from((OfferCreate) transaction)
        .signers(signers)
        .build();
    } else if (OfferCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = OfferCancel.builder().from((OfferCancel) transaction)
        .signers(signers)
        .build();
    } else if (PaymentChannelCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = PaymentChannelCreate.builder().from((PaymentChannelCreate) transaction)
        .signers(signers)
        .build();
    } else if (PaymentChannelClaim.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = PaymentChannelClaim.builder().from((PaymentChannelClaim) transaction)
        .signers(signers)
        .build();
    } else if (PaymentChannelFund.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = PaymentChannelFund.builder().from((PaymentChannelFund) transaction)
        .signers(signers)
        .build();
    } else if (SetRegularKey.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = SetRegularKey.builder().from((SetRegularKey) transaction)
        .signers(signers)
        .build();
    } else if (SignerListSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = SignerListSet.builder().from((SignerListSet) transaction)
        .signers(signers)
        .build();
    } else if (NfTokenAcceptOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = NfTokenAcceptOffer.builder().from((NfTokenAcceptOffer) transaction)
        .signers(signers)
        .build();
    } else if (NfTokenBurn.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = NfTokenBurn.builder().from((NfTokenBurn) transaction)
        .signers(signers)
        .build();
    } else if (NfTokenCancelOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = NfTokenCancelOffer.builder().from((NfTokenCancelOffer) transaction)
        .signers(signers)
        .build();
    } else if (NfTokenCreateOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = NfTokenCreateOffer.builder().from((NfTokenCreateOffer) transaction)
        .signers(signers)
        .build();
    } else if (NfTokenMint.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = NfTokenMint.builder().from((NfTokenMint) transaction)
        .signers(signers)
        .build();
    } else if (TicketCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = TicketCreate.builder().from((TicketCreate) transaction)
        .signers(signers)
        .build();
    } else if (Clawback.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = Clawback.builder().from((Clawback) transaction)
        .signers(signers)
        .build();
    } else if (AmmBid.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmBid.builder().from((AmmBid) transaction)
        .signers(signers)
        .build();
    } else if (AmmCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmCreate.builder().from((AmmCreate) transaction)
        .signers(signers)
        .build();
    } else if (AmmDeposit.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmDeposit.builder().from((AmmDeposit) transaction)
        .signers(signers)
        .build();
    } else if (AmmVote.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmVote.builder().from((AmmVote) transaction)
        .signers(signers)
        .build();
    } else if (AmmWithdraw.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmWithdraw.builder().from((AmmWithdraw) transaction)
        .signers(signers)
        .build();
    } else if (AmmDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmDelete.builder().from((AmmDelete) transaction)
        .signers(signers)
        .build();
    } else if (XChainAccountCreateCommit.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainAccountCreateCommit.builder().from((XChainAccountCreateCommit) transaction)
        .signers(signers)
        .build();
    } else if (XChainAddAccountCreateAttestation.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainAddAccountCreateAttestation.builder()
        .from((XChainAddAccountCreateAttestation) transaction)
        .signers(signers)
        .build();
    } else if (XChainAddClaimAttestation.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainAddClaimAttestation.builder().from((XChainAddClaimAttestation) transaction)
        .signers(signers)
        .build();
    } else if (XChainClaim.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainClaim.builder().from((XChainClaim) transaction)
        .signers(signers)
        .build();
    } else if (XChainCommit.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainCommit.builder().from((XChainCommit) transaction)
        .signers(signers)
        .build();
    } else if (XChainCreateBridge.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainCreateBridge.builder().from((XChainCreateBridge) transaction)
        .signers(signers)
        .build();
    } else if (XChainCreateClaimId.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainCreateClaimId.builder().from((XChainCreateClaimId) transaction)
        .signers(signers)
        .build();
    } else if (XChainModifyBridge.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = XChainModifyBridge.builder().from((XChainModifyBridge) transaction)
        .signers(signers)
        .build();
    } else if (DidSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = DidSet.builder().from((DidSet) transaction)
        .signers(signers)
        .build();
    } else if (DidDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = DidDelete.builder().from((DidDelete) transaction)
        .signers(signers)
        .build();
    } else if (OracleSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = OracleSet.builder().from((OracleSet) transaction)
        .signers(signers)
        .build();
    } else if (OracleDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = OracleDelete.builder().from((OracleDelete) transaction)
        .signers(signers)
        .build();
    } else if (AmmClawback.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignatures = AmmClawback.builder().from((AmmClawback) transaction)
          .signers(signers)
          .build();
    } else {
      // Should never happen, but will in a unit test if we miss one.
      throw new IllegalArgumentException("Signing fields could not be added to the transaction.");
    }

    return (T) transactionWithSignatures;
  }
}
