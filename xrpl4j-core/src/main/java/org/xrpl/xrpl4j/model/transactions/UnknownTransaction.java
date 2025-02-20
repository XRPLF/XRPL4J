package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

/**
 * Mapping for any transaction type that is unrecognized/unsupported by xrpl4j.
 */
@Immutable
@JsonSerialize(as = ImmutableUnknownTransaction.class)
@JsonDeserialize(as = ImmutableUnknownTransaction.class)
public interface UnknownTransaction extends Transaction {

  /**
   * Construct a {@code UnknownTransaction} builder.
   *
   * @return An {@link ImmutableUnknownTransaction.Builder}.
   */
  static ImmutableUnknownTransaction.Builder builder() {
    return ImmutableUnknownTransaction.builder();
  }

  /**
   * The actual transaction type found in the {@code "TransactionType"} field of the transaction JSON.
   *
   * <p>This has to be a {@link String} because {@link Transaction#transactionType()} is a {@link TransactionType},
   * which only has an UNKNOWN variant. Because this method is also annotated with {@link JsonProperty} of
   * "TransactionType", this essentially overrides the "TransactionType" field in JSON, but {@link #transactionType()}
   * will always be {@link TransactionType#UNKNOWN} and this field will contain the actual "TransactionType" field.
   *
   * @return A {@link String} containing the transaction type from JSON.
   */
  @JsonProperty("TransactionType")
  String unknownTransactionType();

  /**
   * The {@link TransactionType} of this UnknownTransaction, which will always be {@link TransactionType#UNKNOWN}.
   * {@link #unknownTransactionType()} contains the actual transaction type value.
   *
   * @return {@link TransactionType#UNKNOWN}.
   */
  @Override
  @JsonIgnore
  @Value.Derived
  default TransactionType transactionType() {
    return Transaction.super.transactionType();
  }

  /**
   * A set of {@link TransactionFlags}.
   *
   * @return A {@link TransactionFlags}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default UnknownTransaction normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.UNKNOWN);
    return this;
  }
}
