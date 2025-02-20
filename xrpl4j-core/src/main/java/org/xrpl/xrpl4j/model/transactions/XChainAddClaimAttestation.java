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
 * Object mapping for the {@code XChainAddClaimAttestation} transaction.
 *
 * <p>This interface will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
 * is subject to change.</p>
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableXChainAddClaimAttestation.class)
@JsonDeserialize(as = ImmutableXChainAddClaimAttestation.class)
public interface XChainAddClaimAttestation extends Transaction {

  /**
   * Construct a {@code XChainAddClaimAttestation} builder.
   *
   * @return An {@link ImmutableXChainAddClaimAttestation.Builder}.
   */
  static ImmutableXChainAddClaimAttestation.Builder builder() {
    return ImmutableXChainAddClaimAttestation.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link XChainAddClaimAttestation}, which only allows the
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
   * The amount committed by the {@code XChainCommit} transaction on the source chain.
   *
   * @return A {@link CurrencyAmount}.
   */
  @JsonProperty("Amount")
  CurrencyAmount amount();

  /**
   * The account that should receive this signer's share of the {@code SignatureReward}.
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
   * The destination account for the funds on the destination chain (taken from the {@code XChainCommit} transaction).
   *
   * @return An {@link Address}.
   */
  @JsonProperty("Destination")
  Optional<Address> destination();

  /**
   * The account on the source chain that submitted the {@code XChainCommit} transaction that triggered the event
   * associated with the attestation.
   *
   * @return An {@link Address}.
   */
  @JsonProperty("OtherChainSource")
  Address otherChainSource();

  /**
   * The public key used to verify the attestation signature.
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
   * The bridge to use to transfer funds.
   *
   * @return An {@link XChainBridge}.
   */
  @JsonProperty("XChainBridge")
  @SuppressWarnings("MethodName")
  XChainBridge xChainBridge();

  /**
   * The {@link XChainClaimId} associated with the transfer, which was included in the {@code XChainCommit}
   * transaction.
   *
   * @return An {@link XChainClaimId}.
   */
  @JsonProperty("XChainClaimID")
  @SuppressWarnings("MethodName")
  XChainClaimId xChainClaimId();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default XChainAddClaimAttestation normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.XCHAIN_ADD_CLAIM_ATTESTATION);
    return this;
  }
}
