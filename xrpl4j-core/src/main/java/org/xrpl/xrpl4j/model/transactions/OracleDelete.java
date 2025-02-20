package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

/**
 * Delete an Oracle ledger entry.
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableOracleDelete.class)
@JsonDeserialize(as = ImmutableOracleDelete.class)
public interface OracleDelete extends Transaction {

  /**
   * Construct a {@code OracleDelete} builder.
   *
   * @return An {@link ImmutableOracleDelete.Builder}.
   */
  static ImmutableOracleDelete.Builder builder() {
    return ImmutableOracleDelete.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link OracleDelete}, which only allows the {@code tfFullyCanonicalSig}
   * flag, which is deprecated.
   *
   * <p>The value of the flags can be set manually, but exists mostly for JSON serialization/deserialization only and
   * for proper signature computation in rippled.
   *
   * @return Always {@link TransactionFlags#EMPTY}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * A unique identifier of the price oracle for the account.
   *
   * @return An {@link UnsignedInteger}.
   */
  @JsonProperty("OracleDocumentID")
  OracleDocumentId oracleDocumentId();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default OracleDelete normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.ORACLE_DELETE);
    return this;
  }
}
