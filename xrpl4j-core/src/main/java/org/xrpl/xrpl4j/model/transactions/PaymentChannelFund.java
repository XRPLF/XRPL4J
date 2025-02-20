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
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * Add additional XRP to an open payment channel, update the expiration time of the channel, or both.
 * Only the source address of the channel can use this transaction.
 *
 * @see "https://xrpl.org/paymentchannelfund.html"
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePaymentChannelFund.class)
@JsonDeserialize(as = ImmutablePaymentChannelFund.class)
public interface PaymentChannelFund extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutablePaymentChannelFund.Builder}.
   */
  static ImmutablePaymentChannelFund.Builder builder() {
    return ImmutablePaymentChannelFund.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link PaymentChannelFund}, which only allows the
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
   * The unique ID of the channel to fund.
   *
   * @return A {@link Hash256} containing the channel ID.
   */
  @JsonProperty("Channel")
  Hash256 channel();

  /**
   * Amount of XRP, in drops to add to the channel. This field is required, therefore it is not possible to
   * set the {@link #expiration()} without adding value to the channel.  However, you can change the expiration
   * and add a negligible amount of XRP (like 1 drop) to the channel.
   *
   * @return An {@link XrpCurrencyAmount} representing the amount of the payment channel.
   */
  @JsonProperty("Amount")
  XrpCurrencyAmount amount();

  /**
   * New Expiration time to set for the channel, in seconds since the Ripple Epoch. This must be later than
   * either the current time plus the SettleDelay of the channel, or the existing Expiration of the channel.
   * After the Expiration time, any transaction that would access the channel closes the channel without
   * taking its normal action. Any unspent XRP is returned to the source address when the channel closes.
   * (Expiration is separate from the channel's immutable CancelAfter time.)
   *
   * @return An {@link Optional} of type {@link UnsignedLong}.
   */
  @JsonProperty("Expiration")
  Optional<UnsignedLong> expiration();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default PaymentChannelFund normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.PAYMENT_CHANNEL_FUND);
    return this;
  }
}
