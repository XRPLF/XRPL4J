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
import org.xrpl.xrpl4j.model.flags.PaymentFlags;

import java.util.List;
import java.util.Optional;

/**
 * A Payment transaction represents a transfer of value from one account to another. (Depending on the path taken, this
 * can involve additional exchanges of value, which occur atomically.) This transaction type can be used for several
 * types of payments, including direct XRP-to-XRP payments, creating or redeeming issued currency, cross-currency
 * payments, partial payments, and currency conversions on the XRPL Decentralized Exchange.
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePayment.class)
@JsonDeserialize(as = ImmutablePayment.class)
public interface Payment extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutablePayment.Builder}.
   */
  static ImmutablePayment.Builder builder() {
    return ImmutablePayment.builder();
  }

  /**
   * Set of {@link PaymentFlags}s for this {@link Payment}, which have been properly combined to yield a {@link
   * PaymentFlags} object containing the {@link Long} representation of the set bits.
   *
   * <p>The value of the flags can either be set manually, or constructed using {@link PaymentFlags.Builder}.
   *
   * @return The {@link PaymentFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default PaymentFlags flags() {
    return PaymentFlags.empty();
  }

  /**
   * The amount of currency to deliver. If the {@link PaymentFlags#tfPartialPayment()} flag is set, deliver up to
   * this amount instead.
   *
   * @return A {@link CurrencyAmount} representing the amount of a specified currency to deliver.
   */
  @JsonProperty("Amount")
  CurrencyAmount amount();

  /**
   * The unique {@link Address} of the account receiving the payment. Maybe be empty for an AccountSet or other
   * transaction that is not a payment.
   *
   * @return The {@link Address} of the payment destination account.
   */
  @JsonProperty("Destination")
  Address destination();

  /**
   * Arbitrary {@link UnsignedInteger} tag that identifies the reason for the payment to the destination, or a hosted
   * recipient to pay.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the tag of the destination account.
   */
  @JsonProperty("DestinationTag")
  Optional<UnsignedInteger> destinationTag();

  /**
   * Arbitrary 256-bit hash representing a specific reason or identifier for this payment.
   *
   * @return An {@link Optional} of type {@link Hash256} containing the invoice ID.
   */
  @JsonProperty("InvoiceID")
  Optional<Hash256> invoiceId();

  /**
   * A {@link List} of {@link List}s of payment paths to be used for this transaction. Must be omitted for XRP-to-XRP
   * transactions.
   *
   * <p>This field is auto-fillable
   *
   * @return A {@link List} of {@link List}s of {@link PathStep}s.
   * @see "https://xrpl.org/transaction-common-fields.html#auto-fillable-fields"
   */
  @JsonProperty("Paths")
  List<List<PathStep>> paths();

  /**
   * Highest amount of source currency this transaction is allowed to cost, including transfer fees, exchange rates, and
   * slippage. Does not include the XRP destroyed as a cost for submitting the transaction.
   *
   * <p>Must be supplied for cross-currency/cross-issue payments. Must be omitted for XRP-to-XRP payments.
   *
   * @return An {@link Optional} of type {@link CurrencyAmount}.
   * @see "https://xrpl.org/transfer-fees.html"
   * @see "https://en.wikipedia.org/wiki/Slippage_%28finance%29"
   */
  @JsonProperty("SendMax")
  Optional<CurrencyAmount> sendMax();

  /**
   * Minimum amount of destination currency this {@link Payment} should deliver. Only valid if this the {@link
   * PaymentFlags#tfPartialPayment()}* flag is set.
   *
   * @return An {@link Optional} of type {@link CurrencyAmount}.
   */
  @JsonProperty("DeliverMin")
  Optional<CurrencyAmount> deliverMin();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default Payment normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.PAYMENT);
    return this;
  }
}
