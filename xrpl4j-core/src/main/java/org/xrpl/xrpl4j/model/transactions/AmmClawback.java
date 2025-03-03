package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.AmmClawbackFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;
import org.xrpl.xrpl4j.model.transactions.Address;

import java.util.Optional;

/**
 * An {@link AmmClawback} transaction claws back tokens from a holder that has funds in an AMM pool.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAmmClawback.class)
@JsonDeserialize(as = ImmutableAmmClawback.class)
@Beta
public interface AmmClawback extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableAmmClawback.Builder}.
   */
  static ImmutableAmmClawback.Builder builder() {
    return ImmutableAmmClawback.builder();
  }

  /**
   * The address of the holder that has funds deposited in the AMM pool.
   *
   * @return An {@link Address}.
   */
  @JsonProperty("Holder")
  Address holder();

  /**
   * The asset in the AMM pool that the issuer is looking to claw back.
   *
   * @return An {@link Issue}.
   */
  @JsonProperty("Asset")
  Issue asset();

  /**
   * Other asset in the AMM pool that the issuer is looking to claw back.
   *
   * @return An {@link Issue}.
   */
  @JsonProperty("Asset2")
  Issue asset2();

  /**
   * Optional field that specifies the maximum amount to clawback from the AMM pool.
   *
   * @return An {@link CurrencyAmount}.
   */
  @JsonProperty("Amount")
  Optional<CurrencyAmount> amount();

  /**
   * Transaction Flags for {@link AmmClawback}, with the only option being tfClawTwoAssets.
   *
   * @return {@link AmmClawbackFlags#UNSET} if field was not set, otherwise returns with the set flag.
   */
  @JsonProperty("Flags")
  @Value.Default
  default AmmClawbackFlags flags() {
    return AmmClawbackFlags.empty();
  }
}
