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
import com.google.common.base.Preconditions;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.client.channels.UnsignedClaim;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.DepositPreAuth;
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
   * @param transaction An unsigned {@link Transaction} to add a signature to. Note that {@link
   *                    Transaction#transactionSignature()} must not be provided, and {@link
   *                    Transaction#signingPublicKey()} must be provided.
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
        .transactionSignature(signature.base16Value())
        .build();
    } else if (AccountSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AccountSet.builder().from((AccountSet) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (AccountDelete.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = AccountDelete.builder().from((AccountDelete) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (CheckCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = CheckCancel.builder().from((CheckCancel) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (CheckCash.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = CheckCash.builder().from((CheckCash) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (CheckCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = CheckCreate.builder().from((CheckCreate) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (DepositPreAuth.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = DepositPreAuth.builder().from((DepositPreAuth) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (EscrowCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = EscrowCreate.builder().from((EscrowCreate) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (EscrowCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = EscrowCancel.builder().from((EscrowCancel) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (EscrowFinish.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = EscrowFinish.builder().from((EscrowFinish) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (TrustSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = TrustSet.builder().from((TrustSet) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (OfferCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = OfferCreate.builder().from((OfferCreate) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (OfferCancel.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = OfferCancel.builder().from((OfferCancel) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (PaymentChannelCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = PaymentChannelCreate.builder().from((PaymentChannelCreate) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (PaymentChannelClaim.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = PaymentChannelClaim.builder().from((PaymentChannelClaim) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (PaymentChannelFund.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = PaymentChannelFund.builder().from((PaymentChannelFund) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (SetRegularKey.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = SetRegularKey.builder().from((SetRegularKey) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (SignerListSet.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = SignerListSet.builder().from((SignerListSet) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (NfTokenAcceptOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenAcceptOffer.builder().from((NfTokenAcceptOffer) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (NfTokenBurn.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenBurn.builder().from((NfTokenBurn) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (NfTokenCancelOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenCancelOffer.builder().from((NfTokenCancelOffer) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (NfTokenCreateOffer.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenCreateOffer.builder().from((NfTokenCreateOffer) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (NfTokenMint.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = NfTokenMint.builder().from((NfTokenMint) transaction)
        .transactionSignature(signature.base16Value())
        .build();
    } else if (TicketCreate.class.isAssignableFrom(transaction.getClass())) {
      transactionWithSignature = TicketCreate.builder().from((TicketCreate) transaction)
        .transactionSignature(signature.base16Value())
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
   * Add {@link Transaction#signers()}} to the given transaction. Because {@link Transaction} is not an
   * Immutable object, it does not have a generated builder like its subclasses do. Thus, this method needs to rebuild
   * transactions based on their runtime type.
   *
   * @param transaction An unsigned {@link Transaction} to add a signature to. Note that {@link
   *                    Transaction#transactionSignature()} must not be provided, and {@link
   *                    Transaction#signingPublicKey()} must be an empty string.
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
    } else {
      // Should never happen, but will in a unit test if we miss one.
      throw new IllegalArgumentException("Signing fields could not be added to the transaction.");
    }

    return (T) transactionWithSignatures;
  }
}
