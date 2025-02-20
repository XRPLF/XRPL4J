package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.XChainModifyBridgeFlags;

import java.util.Optional;

/**
 * Object mapping for the {@code XChainModifyBridge} transaction.
 *
 * <p>This interface will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
 * is subject to change.</p>
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableXChainModifyBridge.class)
@JsonDeserialize(as = ImmutableXChainModifyBridge.class)
public interface XChainModifyBridge extends Transaction {

  /**
   * Construct a {@code XChainModifyBridge} builder.
   *
   * @return An {@link ImmutableXChainModifyBridge.Builder}.
   */
  static ImmutableXChainModifyBridge.Builder builder() {
    return ImmutableXChainModifyBridge.builder();
  }

  /**
   * Set of {@link XChainModifyBridgeFlags}s for this {@link XChainModifyBridge}, which have been properly combined to
   * yield a {@link XChainModifyBridgeFlags} object containing the {@link Long} representation of the set bits.
   *
   * <p>The value of the flags can either be set manually, or constructed using {@link XChainModifyBridgeFlags.Builder}.
   *
   * @return The {@link XChainModifyBridgeFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default XChainModifyBridgeFlags flags() {
    return XChainModifyBridgeFlags.empty();
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
   * The signature reward split between the witnesses for submitting attestations.
   *
   * @return An optionally-present {@link XrpCurrencyAmount}.
   */
  @JsonProperty("SignatureReward")
  Optional<XrpCurrencyAmount> signatureReward();

  /**
   * The bridge to create the claim ID for.
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
  default XChainModifyBridge normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.XCHAIN_MODIFY_BRIDGE);
    return this;
  }

}
