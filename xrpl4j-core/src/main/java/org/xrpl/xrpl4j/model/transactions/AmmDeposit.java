package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.AmmDepositFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

import java.util.Optional;

/**
 * Object mapping for the AMMDeposit transaction.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAmmDeposit.class)
@JsonDeserialize(as = ImmutableAmmDeposit.class)
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
   * A {@link AmmDepositFlags} for this transaction. This flag will automatically be derived from the presence
   * of {@link #lpTokenOut()}, {@link #amount()}, {@link #amount2()} and {@link #effectivePrice()}.
   *
   * @return A {@link AmmDepositFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default AmmDepositFlags flags() {
    boolean lpTokenOutPresent = lpTokenOut().isPresent();
    boolean amountPresent = amount().isPresent();
    boolean amount2Present = amount2().isPresent();
    boolean effectivePricePresent = effectivePrice().isPresent();

    if (lpTokenOutPresent && !amountPresent && !amount2Present && !effectivePricePresent) {
      return AmmDepositFlags.LP_TOKEN;
    } else if (!lpTokenOutPresent && amountPresent && amount2Present &&  !effectivePricePresent) {
      return AmmDepositFlags.TWO_ASSET;
    } else if (!lpTokenOutPresent && amountPresent && !amount2Present && !effectivePricePresent) {
      return AmmDepositFlags.SINGLE_ASSET;
    } else if (lpTokenOutPresent && amountPresent && !amount2Present && !effectivePricePresent) {
      return AmmDepositFlags.ONE_ASSET_LP_TOKEN;
    } else if (!lpTokenOutPresent && amountPresent && !amount2Present) {
      return AmmDepositFlags.LIMIT_LP_TOKEN;
    } else {
      throw new IllegalStateException("Correct AmmDepositFlag could not be determined based on set fields.");
    }
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
   * The amount of one asset to deposit to the AMM. If present, this must match the type of one of the assets
   * (tokens or XRP) in the AMM's pool.
   *
   * @return An optionally present {@link CurrencyAmount}.
   */
  @JsonProperty("Amount")
  Optional<CurrencyAmount> amount();

  /**
   * The amount of another asset to add to the AMM. If present, this must match the type of the other asset in the
   * AMM's pool and cannot be the same asset as Amount.
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
   * @return An optionally present {@link IssuedCurrencyAmount}.
   */
  @JsonProperty("LPTokenOut")
  Optional<IssuedCurrencyAmount> lpTokenOut();

}
