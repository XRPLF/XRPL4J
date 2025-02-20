package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

/**
 * Object mapping for the AMMVote transaction.
 *
 * <p>This class will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
 * change.</p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAmmVote.class)
@JsonDeserialize(as = ImmutableAmmVote.class)
@Beta
public interface AmmVote extends Transaction {

  /**
   * Construct a {@code AmmVote} builder.
   *
   * @return An {@link ImmutableAmmVote.Builder}.
   */
  static ImmutableAmmVote.Builder builder() {
    return ImmutableAmmVote.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link AmmVote}, which only allows the {@code tfFullyCanonicalSig} flag,
   * which is deprecated.
   *
   * <p>The value of the flags cannot be set manually, but exists for JSON serialization/deserialization only and for
   * proper signature computation in rippled.
   *
   * @return Always {@link TransactionFlags#EMPTY}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * The definition for one of the assets in the AMM's pool.
   *
   * @return An {@link Issue}.
   */
  @JsonProperty("Asset")
  Issue asset();

  /**
   * The definition for the other asset in the AMM's pool.
   *
   * @return An {@link Issue}.
   */
  @JsonProperty("Asset2")
  Issue asset2();

  /**
   * The proposed fee to vote for.
   *
   * @return A {@link TradingFee}.
   */
  @JsonProperty("TradingFee")
  TradingFee tradingFee();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AmmVote normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.AMM_VOTE);
    return this;
  }

}
