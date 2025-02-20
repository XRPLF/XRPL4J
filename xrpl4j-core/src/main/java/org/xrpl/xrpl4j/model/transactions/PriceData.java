package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value.Immutable;

import java.util.Optional;

/**
 * Represents price information for a token pair in an Oracle.
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutablePriceData.class)
@JsonDeserialize(as = ImmutablePriceData.class)
public interface PriceData {

  /**
   * Construct a {@code PriceData} builder.
   *
   * @return An {@link ImmutablePriceData.Builder}.
   */
  static ImmutablePriceData.Builder builder() {
    return ImmutablePriceData.builder();
  }

  /**
   * The primary asset in a trading pair. Any valid identifier, such as a stock symbol, bond CUSIP, or currency code is
   * allowed. For example, in the BTC/USD pair, BTC is the base asset; in 912810RR9/BTC, 912810RR9 is the base asset.
   *
   * @return A {@link String}.
   */
  @JsonProperty("BaseAsset")
  String baseAsset();

  /**
   * The quote asset in a trading pair. The quote asset denotes the price of one unit of the base asset. For example, in
   * the BTC/USD pair, BTC is the base asset; in 912810RR9/BTC, 912810RR9 is the base asset.
   *
   * @return A {@link String}.
   */
  @JsonProperty("QuoteAsset")
  String quoteAsset();

  /**
   * The asset price after applying the {@link #scale()} precision level. It's not included if the last update
   * transaction didn't include the {@link #baseAsset()}/{@link #quoteAsset()} pair.
   *
   * @return An {@link Optional} {@link AssetPrice}.
   */
  @JsonProperty("AssetPrice")
  Optional<AssetPrice> assetPrice();

  /**
   * The scaling factor to apply to an asset price. For example, if scale is 6 and original price is 0.155, then the
   * scaled price is 155000. Valid scale ranges are 0-10. It's not included if the last update transaction didn't
   * include the {@link #baseAsset()}/{@link #quoteAsset()} pair.
   *
   * @return An {@link Optional} {@link UnsignedInteger}.
   */
  @JsonProperty("Scale")
  Optional<UnsignedInteger> scale();

}
