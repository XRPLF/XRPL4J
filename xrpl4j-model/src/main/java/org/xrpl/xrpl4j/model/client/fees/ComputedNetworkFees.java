package org.xrpl.xrpl4j.model.client.fees;

import org.immutables.value.Value;
import org.immutables.value.Value.Derived;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;

/**
 * An object that holds fee options that were calculated based upon current ledger stats.
 */
@Value.Immutable
public interface ComputedNetworkFees {

  /**
   * A builder of for {@link ComputedNetworkFees}.
   *
   * @return An {@link ImmutableComputedNetworkFees.Builder}.
   */
  static ImmutableComputedNetworkFees.Builder builder() {
    return ImmutableComputedNetworkFees.builder();
  }

  /**
   * The `low` fee, which is the fee that should be used if the transaction queue is empty.
   *
   * @return An {@link XrpCurrencyAmount} representing the `low` fee (in drops).
   */
  XrpCurrencyAmount feeLow();

  /**
   * The `medium` fee, which is the fee that should be used if the transaction queue is neither empty nor full.
   *
   * @return An {@link XrpCurrencyAmount} representing the `medium` fee (in drops).
   */
  XrpCurrencyAmount feeMedium();

  /**
   * The `high` fee, which is the fee that should be used if the transaction queue is full.
   *
   * @return An {@link XrpCurrencyAmount} representing the `high` fee (in drops).
   */
  XrpCurrencyAmount feeHigh();

  /**
   * Measures the fullness of the transaction queue by representing the percent full that the queue is. For example, if
   * the transaction queue can hold two transactions, and one is in the queue, then this value would be 50%, or 0.5.
   *
   * @return A {@link BigDecimal}.
   */
  // TODO: Introduce Percentage.
  BigDecimal queuePercentage();

  /**
   * Helper method to return the recommened fee to use.
   *
   * @return An {@link XrpCurrencyAmount} that is the recommended fee.
   */
  @Derived
  default XrpCurrencyAmount recommendedFee() {
    if (isTransactionQueueEmpty()) {
      return feeLow();
    } else if (isTranactionQueueFull()) {
      return feeHigh();
    } else { // queue is neither empty nor full
      return feeMedium();
    }
  }

  /**
   * Determines if the transaction queue is full.
   *
   * @return {@code true} if the transaction queue is full; {@code false} otherwise.
   */
  @Derived
  default boolean isTranactionQueueFull() {
    return FluentCompareTo.is(queuePercentage()).greaterThanEqualTo(BigDecimal.ONE);
  }

  /**
   * Helper method to determine if a transaction queue is empty.
   *
   * @return {@code true} if the queue is empty; {@code false} otherwise.
   */
  @Derived
  default boolean isTransactionQueueEmpty() {
    return FluentCompareTo.is(queuePercentage()).lessThanOrEqualTo(BigDecimal.ZERO);
  }

}
