package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.AmmWithdrawFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

import java.util.Optional;

/**
 * Object mapping for the AMMWithdraw transaction.
 *
 * <p>This class will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
 * change.</p>
 */
@Immutable
@JsonSerialize(as = ImmutableAmmWithdraw.class)
@JsonDeserialize(as = ImmutableAmmWithdraw.class)
@Beta
public interface AmmWithdraw extends Transaction {

  /**
   * Construct a {@code AmmWithdraw} builder.
   *
   * @return An {@link ImmutableAmmWithdraw.Builder}.
   */
  static ImmutableAmmWithdraw.Builder builder() {
    return ImmutableAmmWithdraw.builder();
  }

  /**
   * A {@link AmmWithdrawFlags} for this transaction.
   *
   * @return A {@link AmmWithdrawFlags} for this transaction.
   */
  @JsonProperty("Flags")
  AmmWithdrawFlags flags();

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
   * @return An optionally present {@link IssuedCurrencyAmount}.
   */
  @JsonProperty("LPTokenIn")
  Optional<CurrencyAmount> lpTokensIn();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AmmWithdraw normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.AMM_WITHDRAW);
    return this;
  }
}
