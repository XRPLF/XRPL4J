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

import java.util.List;
import java.util.Optional;

/**
 * Creates a new Oracle ledger entry or updates the fields of an existing one, using the Oracle ID.
 */
@Beta
@Immutable
@JsonSerialize(as = ImmutableOracleSet.class)
@JsonDeserialize(as = ImmutableOracleSet.class)
public interface OracleSet extends Transaction {

  /**
   * Construct a {@code OracleSet} builder.
   *
   * @return An {@link ImmutableOracleSet.Builder}.
   */
  static ImmutableOracleSet.Builder builder() {
    return ImmutableOracleSet.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link OracleSet}, which only allows the {@code tfFullyCanonicalSig}
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
   * An arbitrary value that identifies an oracle provider, such as Chainlink, Band, or DIA. This field is a string, up
   * to 256 ASCII hex encoded characters (0x20-0x7E). This field is required when creating a new Oracle ledger entry,
   * but is optional for updates.
   *
   * @return An {@link Optional} {@link String}.
   */
  @JsonProperty("Provider")
  Optional<OracleProvider> provider();

  /**
   * An optional Universal Resource Identifier to reference price data off-chain. This field is limited to 256 bytes.
   *
   * @return An {@link Optional} {@link String}.
   */
  @JsonProperty("URI")
  Optional<OracleUri> uri();

  /**
   * The time the data was last updated, represented in Unix time.
   *
   * @return An {@link UnsignedInteger}.
   */
  @JsonProperty("LastUpdateTime")
  UnsignedInteger lastUpdateTime();

  /**
   * Describes the type of asset, such as "currency", "commodity", or "index". This field is a string, up to 16 ASCII
   * hex encoded characters (0x20-0x7E). This field is required when creating a new Oracle ledger entry, but is optional
   * for updates.
   *
   * @return An {@link Optional} {@link String}.
   */
  @JsonProperty("AssetClass")
  Optional<String> assetClass();

  /**
   * An array of up to 10 PriceData objects, each representing the price information for a token pair. More than five
   * PriceData objects require two owner reserves.
   *
   * @return A {@link List} of {@link PriceData}.
   */
  @JsonProperty("PriceDataSeries")
  List<PriceDataWrapper> priceDataSeries();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default OracleSet normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.ORACLE_SET);
    return this;
  }
}
