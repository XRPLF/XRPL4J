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
import org.immutables.value.Value.Immutable;
import org.xrpl.xrpl4j.model.flags.OfferCreateFlags;

import java.util.Optional;

/**
 * An OfferCreate transaction is effectively a limit order. It defines an intent to exchange currencies, and
 * creates an offer object if not completely fulfilled when placed. Offers can be partially fulfilled.
 *
 * @see "https://xrpl.org/offercreate.html"
 */
@Immutable
@JsonSerialize(as = ImmutableOfferCreate.class)
@JsonDeserialize(as = ImmutableOfferCreate.class)
public interface OfferCreate extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableOfferCreate.Builder}.
   */
  static ImmutableOfferCreate.Builder builder() {
    return ImmutableOfferCreate.builder();
  }

  /**
   * Set of {@link OfferCreateFlags}s for this {@link OfferCreate}.
   *
   * @return The {@link OfferCreateFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default OfferCreateFlags flags() {
    return OfferCreateFlags.empty();
  }

  /**
   * An offer to delete first, specified in the same way as an Offer Cancel.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the offer sequence.
   */
  @JsonProperty("OfferSequence")
  Optional<UnsignedInteger> offerSequence();

  /**
   * The amount and type of currency being requested by the offer creator.
   *
   * @return A {@link CurrencyAmount} representing the amount being requested.
   */
  @JsonProperty("TakerPays")
  CurrencyAmount takerPays();

  /**
   * The amount and type of currency being provided by the offer creator.
   *
   * @return A {@link CurrencyAmount} representing the amount being offered.
   */
  @JsonProperty("TakerGets")
  CurrencyAmount takerGets();

  /**
   * Time after which the Check is no longer valid, in
   * <a href="https://xrpl.org/basic-data-types.html#specifying-time">seconds since the Ripple Epoch</a>.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the expiration time.
   */
  @JsonProperty("Expiration")
  Optional<UnsignedInteger> expiration();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default OfferCreate normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.OFFER_CREATE);
    return this;
  }
}
