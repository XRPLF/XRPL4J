package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * Object mapping for the {@code XChainAddAccountCreateAttestation} transaction.
 *
 * <p>This interface will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
 * is subject to change.</p>
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableXChainAddAccountCreateAttestation.class)
@JsonDeserialize(as = ImmutableXChainAddAccountCreateAttestation.class)
public interface XChainAddAccountCreateAttestation extends Transaction {

  /**
   * Construct a {@code XChainAddAccountCreateAttestation} builder.
   *
   * @return An {@link ImmutableXChainAddAccountCreateAttestation.Builder}.
   */
  static ImmutableXChainAddAccountCreateAttestation.Builder builder() {
    return ImmutableXChainAddAccountCreateAttestation.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link XChainAddAccountCreateAttestation}, which only allows the
   * {@code tfFullyCanonicalSig} flag, which is deprecated.
   *
   * @return A set of {@link TransactionFlags}, default is {@link TransactionFlags#EMPTY}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * The amount committed by the {@code XChainAccountCreateCommit} transaction on the source chain.
   *
   * @return An {@link XrpCurrencyAmount}.
   */
  @JsonProperty("Amount")
  XrpCurrencyAmount amount();

  /**
   * The account that should receive this signer's share of the {@link #signatureReward()}.
   *
   * @return An {@link Address}.
   */
  @JsonProperty("AttestationRewardAccount")
  Address attestationRewardAccount();

  /**
   * The account on the door account's signer list that is signing the transaction.
   *
   * @return An {@link Address}.
   */
  @JsonProperty("AttestationSignerAccount")
  Address attestationSignerAccount();

  /**
   * The destination account for the funds on the destination chain.
   *
   * @return The {@link Address} of the destination account.
   */
  @JsonProperty("Destination")
  Address destination();

  /**
   * The account on the source chain that submitted the {@code XChainAccountCreateCommit} transaction that triggered the
   * event associated with the attestation.
   *
   * @return An {@link Address}.
   */
  @JsonProperty("OtherChainSource")
  Address otherChainSource();

  /**
   * The public key used to verify the signature.
   *
   * @return A {@link PublicKey}.
   */
  @JsonProperty("PublicKey")
  PublicKey publicKey();

  /**
   * The signature attesting to the event on the other chain.
   *
   * @return A {@link Signature}.
   */
  @JsonProperty("Signature")
  Signature signature();

  /**
   * The signature reward paid in the {@code XChainAccountCreateCommit} transaction.
   *
   * @return An optionally-present {@link XrpCurrencyAmount}.
   */
  @JsonProperty("SignatureReward")
  Optional<XrpCurrencyAmount> signatureReward();

  /**
   * A boolean representing the chain where the event occurred.
   *
   * <p>Note that this field is typed as a {@code boolean} but is represented by an integer (0 or 1) in JSON
   * and treated as a UInt8 in XRPL binary format.</p>
   *
   * @return {@code true} if the locking chain was the sender, otherwise {@code false}.
   */
  @JsonProperty("WasLockingChainSend")
  @JsonFormat(shape = Shape.NUMBER)
  boolean wasLockingChainSend();

  /**
   * The counter that represents the order that the claims must be processed in.
   *
   * @return An {@link XChainCount}.
   */
  @JsonProperty("XChainAccountCreateCount")
  @SuppressWarnings("MethodName")
  XChainCount xChainAccountCreateCount();

  /**
   * The bridge associated with the attestation.
   *
   * @return An {@link XChainBridge}.
   */
  @JsonProperty("XChainBridge")
  @SuppressWarnings("MethodName")
  XChainBridge xChainBridge();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default XChainAddAccountCreateAttestation normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.XCHAIN_ADD_ACCOUNT_CREATE_ATTESTATION);
    return this;
  }
}
