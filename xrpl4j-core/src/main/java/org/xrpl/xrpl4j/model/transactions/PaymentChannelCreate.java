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
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * Create a unidirectional channel and fund it with XRP. The address sending this transaction becomes the
 * "source address" of the payment channel.
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePaymentChannelCreate.class)
@JsonDeserialize(as = ImmutablePaymentChannelCreate.class)
public interface PaymentChannelCreate extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutablePaymentChannelCreate.Builder}.
   */
  static ImmutablePaymentChannelCreate.Builder builder() {
    return ImmutablePaymentChannelCreate.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link PaymentChannelCreate}, which only allows the
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
   * Amount of XRP, in drops, to deduct from the {@link #account()}'s balance and set aside in this channel. While
   * the channel is open, the XRP can only go to the {@link #destination()} address. When the channel closes,
   * any unclaimed XRP is returned to the {@link #account()}'s balance.
   *
   * @return An {@link XrpCurrencyAmount} representing the amount of the payment channel.
   */
  @JsonProperty("Amount")
  XrpCurrencyAmount amount();

  /**
   * {@link Address} to receive XRP claims against this channel. Cannot be the same as {@link #account()};
   *
   * @return The {@link Address} of the destination account.
   */
  @JsonProperty("Destination")
  Address destination();

  /**
   * Amount of time, in seconds, the {@link #account()} must wait before closing the channel if it has unclaimed XRP.
   *
   * @return An {@link UnsignedInteger} representing the settlement delay.
   */
  @JsonProperty("SettleDelay")
  UnsignedInteger settleDelay();

  /**
   * The public key of the key pair the {@link #account()} will use to sign claims against this channel,
   * in hexadecimal. This can be any secp256k1 or Ed25519 public key.
   *
   * @return A {@link String} containing the public key for the channel.
   */
  @JsonProperty("PublicKey")
  String publicKey();

  /**
   * The time, in <a href="https://xrpl.org/basic-data-types.html#specifying-time">seconds since the Ripple Epoch</a>,
   * when this channel expires. Any transaction that would modify the channel after this time closes the channel
   * without otherwise affecting it. This value is immutable; the channel can be closed earlier than this time but
   * cannot remain open after this time.
   *
   * @return An {@link Optional} of type {@link UnsignedLong} representing the cancel after time.
   */
  @JsonProperty("CancelAfter")
  Optional<UnsignedLong> cancelAfter();

  /**
   * Arbitrary tag to further specify the destination for this payment channel, such as a hosted recipient at
   * the {@link #destination()} address.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the tag of the destination account.
   */
  @JsonProperty("DestinationTag")
  Optional<UnsignedInteger> destinationTag();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default PaymentChannelCreate normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.PAYMENT_CHANNEL_CREATE);
    return this;
  }
}
