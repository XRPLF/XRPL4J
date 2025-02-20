package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.AuthAccountWrapper;
import org.xrpl.xrpl4j.model.ledger.Issue;

import java.util.List;
import java.util.Optional;

/**
 * Object mapping for the AMMBid transaction.
 *
 * <p>This class will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
 * change.</p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAmmBid.class)
@JsonDeserialize(as = ImmutableAmmBid.class)
@Beta
public interface AmmBid extends Transaction {

  /**
   * Construct a {@code AmmBid} builder.
   *
   * @return An {@link ImmutableAmmBid.Builder}.
   */
  static ImmutableAmmBid.Builder builder() {
    return ImmutableAmmBid.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link AmmBid}, which only allows the {@code tfFullyCanonicalSig} flag,
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
   * Pay at least this amount for the slot. Setting this value higher makes it harder for others to outbid you. If
   * omitted, pay the minimum necessary to win the bid.
   *
   * <p>
   * In a well-formed transaction, this field is always an {@link IssuedCurrencyAmount}. However, the XRPL will fail AMM
   * transactions that specify {@link XrpCurrencyAmount}s with a {@code tec} error code, which means these malformed
   * transactions can be included in validated ledgers. Therefore, this field is typed as a {@link CurrencyAmount} so
   * that malformed transactions can be correctly deserialized. See <a
   * href="https://github.com/XRPLF/xrpl4j/issues/529">#529</a>
   * </p>
   *
   * @return An optionally present {@link CurrencyAmount}.
   */
  @JsonProperty("BidMin")
  Optional<CurrencyAmount> bidMin();

  /**
   * Pay at most this amount for the slot. If the cost to win the bid is higher than this amount, the transaction fails.
   * If omitted, pay as much as necessary to win the bid.
   *
   * <p>
   * In a well-formed transaction, this field is always an {@link IssuedCurrencyAmount}. However, the XRPL will fail AMM
   * transactions that specify {@link XrpCurrencyAmount}s with a {@code tec} error code, which means these malformed
   * transactions can be included in validated ledgers. Therefore, this field is typed as a {@link CurrencyAmount} so
   * that malformed transactions can be correctly deserialized. See <a
   * href="https://github.com/XRPLF/xrpl4j/issues/529">#529</a>
   * </p>
   *
   * @return An optionally present {@link CurrencyAmount}.
   */
  @JsonProperty("BidMax")
  Optional<CurrencyAmount> bidMax();

  /**
   * A list of up to 4 additional accounts that you allow to trade at the discounted fee. This cannot include the
   * address of the transaction sender
   *
   * @return A {@link List}
   */
  @JsonProperty("AuthAccounts")
  List<AuthAccountWrapper> authAccounts();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AmmBid normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.AMM_BID);
    return this;
  }
}
