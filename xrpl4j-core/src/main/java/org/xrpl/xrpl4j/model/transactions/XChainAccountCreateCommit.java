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
 * Object mapping for the {@code XChainAccountCreateCommit} transaction.
 *
 * <p>This interface will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
 * is subject to change.</p>
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableXChainAccountCreateCommit.class)
@JsonDeserialize(as = ImmutableXChainAccountCreateCommit.class)
public interface XChainAccountCreateCommit extends Transaction {

  /**
   * Construct a {@code XChainAccountCreateCommit} builder.
   *
   * @return An {@link ImmutableXChainAccountCreateCommit.Builder}.
   */
  static ImmutableXChainAccountCreateCommit.Builder builder() {
    return ImmutableXChainAccountCreateCommit.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link XChainAccountCreateCommit}, which only allows the
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
   * The amount, in drops of XRP, to use for account creation. This must be greater than or equal to the
   * {@code MinAccountCreateAmount} specified in the {@code Bridge} ledger object.
   *
   * @return An {@link XrpCurrencyAmount}.
   */
  @JsonProperty("Amount")
  XrpCurrencyAmount amount();

  /**
   * The destination account on the destination chain.
   *
   * @return The {@link Address} of the destination account.
   */
  @JsonProperty("Destination")
  Address destination();

  /**
   * The amount, in XRP, to be used to reward the witness servers for providing signatures. This must match the amount
   * on the {@code Bridge} ledger object.
   *
   * @return An optionally-present {@link XrpCurrencyAmount}.
   */
  @JsonProperty("SignatureReward")
  Optional<XrpCurrencyAmount> signatureReward();

  /**
   * The bridge to create accounts for.
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
  default XChainAccountCreateCommit normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.XCHAIN_ACCOUNT_CREATE_COMMIT);
    return this;
  }
}
