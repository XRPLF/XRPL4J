package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

/**
 * Object mapping for the AMMDelete transaction.
 *
 * <p>This class will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
 * change.</p>
 */
@Immutable
@JsonSerialize(as = ImmutableAmmDelete.class)
@JsonDeserialize(as = ImmutableAmmDelete.class)
@Beta
public interface AmmDelete extends Transaction {

  /**
   * Construct a {@code AmmDelete} builder.
   *
   * @return An {@link ImmutableAmmDelete.Builder}.
   */
  static ImmutableAmmDelete.Builder builder() {
    return ImmutableAmmDelete.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link AmmDelete}, which only allows the {@code tfFullyCanonicalSig}
   * flag, which is deprecated.
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
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AmmDelete normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.AMM_DELETE);
    return this;
  }
}
