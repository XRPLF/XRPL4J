package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: model
 * %%
 * Copyright (C) 2020 - 2022 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * Create a Check object in the ledger, which is a deferred payment that can be cashed by its intended destination.
 * The sender of this transaction is the sender of the Check.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCheckCreate.class)
@JsonDeserialize(as = ImmutableCheckCreate.class)
public interface CheckCreate extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableCheckCreate.Builder}.
   */
  static ImmutableCheckCreate.Builder builder() {
    return ImmutableCheckCreate.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link CheckCreate}, which only allows the
   * {@code tfFullyCanonicalSig} flag, which is deprecated.
   *
   * <p>The value of the flags cannot be set manually, but exists for JSON serialization/deserialization only and for
   * proper signature computation in rippled.
   *
   * @return Always {@link TransactionFlags#EMPTY}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * The unique {@link Address} of the account that can cash the Check.
   *
   * @return The unique {@link Address} of the account that can cash the Check.
   */
  @JsonProperty("Destination")
  Address destination();

  /**
   * Arbitrary tag that identifies the reason for the Check, or a hosted recipient to pay.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the tag of the destination account.
   */
  @JsonProperty("DestinationTag")
  Optional<UnsignedInteger> destinationTag();

  /**
   * Maximum amount of source currency the Check is allowed to debit the sender, including transfer fees on
   * non-XRP currencies. The Check can only credit the destination with the same currency
   * (from the same issuer, for non-XRP currencies). For non-XRP amounts, the nested field names MUST be lower-case.
   *
   * @return A {@link CurrencyAmount} containing the maximum amount this check is allowed to send.
   */
  @JsonProperty("SendMax")
  CurrencyAmount sendMax();

  /**
   * Time after which the Check is no longer valid, in
   * <a href="https://xrpl.org/basic-data-types.html#specifying-time">seconds since the Ripple Epoch</a>.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} denoting the expiration time.
   */
  @JsonProperty("Expiration")
  Optional<UnsignedInteger> expiration();

  /**
   * Arbitrary 256-bit hash representing a specific reason or identifier for this Check.
   *
   * @return An {@link Optional} of type {@link Hash256} containing the invoice ID.
   */
  @JsonProperty("InvoiceID")
  Optional<Hash256> invoiceId();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default CheckCreate normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.CHECK_CREATE);
    return this;
  }
}
