package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * Object mapping for the {@code XChainCreateBridge} transaction.
 *
 * <p>This interface will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
 * is subject to change.</p>
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableXChainCreateBridge.class)
@JsonDeserialize(as = ImmutableXChainCreateBridge.class)
public interface XChainCreateBridge extends Transaction {

  /**
   * Construct a {@code XChainCreateBridge} builder.
   *
   * @return An {@link ImmutableXChainCreateBridge.Builder}.
   */
  static ImmutableXChainCreateBridge.Builder builder() {
    return ImmutableXChainCreateBridge.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link XChainCreateBridge}, which only allows the
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
   * The minimum amount, in XRP, required for a {@link XChainAccountCreateCommit} transaction. If this isn't present,
   * the {@link XChainAccountCreateCommit} transaction will fail. This field can only be present on XRP-XRP bridges.
   *
   * @return An optionally-present {@link XrpCurrencyAmount}.
   */
  @JsonProperty("MinAccountCreateAmount")
  Optional<XrpCurrencyAmount> minAccountCreateAmount();

  /**
   * The total amount to pay the witness servers for their signatures. This amount will be split among the signers.
   *
   * @return An {@link XrpCurrencyAmount}.
   */
  @JsonProperty("SignatureReward")
  XrpCurrencyAmount signatureReward();

  /**
   * The bridge (door accounts and assets) to create.
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
  default XChainCreateBridge normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.XCHAIN_CREATE_BRIDGE);
    return this;
  }
}
