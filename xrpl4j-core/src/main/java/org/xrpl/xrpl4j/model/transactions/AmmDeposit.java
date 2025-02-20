package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.AmmDepositFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

import java.util.Optional;

/**
 * Object mapping for the AMMDeposit transaction.
 *
 * <p>This class will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
 * change.</p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAmmDeposit.class)
@JsonDeserialize(as = ImmutableAmmDeposit.class)
@Beta
public interface AmmDeposit extends Transaction {

  /**
   * Construct a {@code AmmDeposit} builder.
   *
   * @return An {@link ImmutableAmmDeposit.Builder}.
   */
  static ImmutableAmmDeposit.Builder builder() {
    return ImmutableAmmDeposit.builder();
  }

  /**
   * A {@link AmmDepositFlags} for this transaction. This field must be set manually.
   *
   * @return A {@link AmmDepositFlags} for this transaction.
   */
  @JsonProperty("Flags")
  AmmDepositFlags flags();

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
   * The amount of one asset to deposit to the AMM. If present, this must match the type of one of the assets (tokens or
   * XRP) in the AMM's pool.
   *
   * @return An optionally present {@link CurrencyAmount}.
   */
  @JsonProperty("Amount")
  Optional<CurrencyAmount> amount();

  /**
   * The amount of another asset to add to the AMM. If present, this must match the type of the other asset in the AMM's
   * pool and cannot be the same asset as Amount.
   *
   * @return An optionally present {@link CurrencyAmount}.
   */
  @JsonProperty("Amount2")
  Optional<CurrencyAmount> amount2();

  /**
   * The maximum effective price, in the deposit asset, to pay for each LP Token received.
   *
   * @return An optionally present {@link CurrencyAmount}.
   */
  @JsonProperty("EPrice")
  Optional<CurrencyAmount> effectivePrice();

  /**
   * How many of the AMM's LP Tokens to buy.
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
  @JsonProperty("LPTokenOut")
  Optional<CurrencyAmount> lpTokenOut();

  /**
   * An optional {@link TradingFee} to set on the AMM instance. This field is only honored if the AMM's LP token balance
   * is zero, and can only be set if flags is {@link AmmDepositFlags#TWO_ASSET_IF_EMPTY}.
   *
   * @return An {@link Optional} {@link TradingFee}.
   */
  @JsonProperty("TradingFee")
  Optional<TradingFee> tradingFee();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AmmDeposit normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.AMM_DEPOSIT);
    return this;
  }
}
