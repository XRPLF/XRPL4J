package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

/**
 * Object mapping for the AMMCreate transaction.
 *
 * <p>This class will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
 * change.</p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAmmCreate.class)
@JsonDeserialize(as = ImmutableAmmCreate.class)
@Beta
public interface AmmCreate extends Transaction {

  /**
   * Construct a {@code AmmCreate} builder.
   *
   * @return An {@link ImmutableAmmCreate.Builder}.
   */
  static ImmutableAmmCreate.Builder builder() {
    return ImmutableAmmCreate.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link AmmCreate}, which only allows the {@code tfFullyCanonicalSig}
   * flag, which is deprecated.
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
   * The first of the two assets to fund this AMM with.
   *
   * @return A {@link CurrencyAmount}.
   */
  @JsonProperty("Amount")
  CurrencyAmount amount();

  /**
   * The second of the two assets to fund this AMM with.
   *
   * @return A {@link CurrencyAmount}.
   */
  @JsonProperty("Amount2")
  CurrencyAmount amount2();

  /**
   * The fee to charge for trades against this AMM instance.
   *
   * @return A {@link TradingFee}.
   */
  @JsonProperty("TradingFee")
  TradingFee tradingFee();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AmmCreate normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.AMM_CREATE);
    return this;
  }
}
