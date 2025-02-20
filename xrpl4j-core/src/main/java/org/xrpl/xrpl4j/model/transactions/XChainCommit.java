package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * Object mapping for the {@code XChainCommit} transaction.
 *
 * <p>This interface will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
 * is subject to change.</p>
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableXChainCommit.class)
@JsonDeserialize(as = ImmutableXChainCommit.class)
public interface XChainCommit extends Transaction {

  /**
   * Construct a {@code XChainCommit} builder.
   *
   * @return An {@link ImmutableXChainCommit.Builder}.
   */
  static ImmutableXChainCommit.Builder builder() {
    return ImmutableXChainCommit.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link XChainCommit}, which only allows the {@code tfFullyCanonicalSig}
   * flag, which is deprecated.
   *
   * @return A set of {@link TransactionFlags}, default is {@link TransactionFlags#EMPTY}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * The asset to commit, and the quantity. This must match the door account's {@code LockingChainIssue} (if on the
   * locking chain) or the door account's {@code IssuingChainIssue} (if on the issuing chain).
   *
   * @return A {@link CurrencyAmount}.
   */
  @JsonProperty("Amount")
  CurrencyAmount amount();

  /**
   * The destination account on the destination chain. If this is not specified, the account that submitted the
   * {@link org.xrpl.xrpl4j.model.transactions.XChainCreateClaimId} transaction on the destination chain will need to
   * submit a {@link XChainClaim} transaction to claim the funds.
   *
   * @return An optionally-present {@link Address}.
   */
  @JsonProperty("OtherChainDestination")
  Optional<Address> otherChainDestination();

  /**
   * The bridge to use to transfer funds.
   *
   * @return An {@link XChainBridge}.
   */
  @JsonProperty("XChainBridge")
  @SuppressWarnings("MethodName")
  XChainBridge xChainBridge();

  /**
   * The unique integer ID for a cross-chain transfer. This must be acquired on the destination chain (via a
   * {@link org.xrpl.xrpl4j.model.transactions.XChainCreateClaimId} transaction) and checked from a validated ledger
   * before submitting this transaction. If an incorrect sequence number is specified, the funds will be lost.
   *
   * @return An {@link XChainClaimId}.
   */
  @JsonProperty("XChainClaimID")
  @SuppressWarnings("MethodName")
  XChainClaimId xChainClaimId();

}
