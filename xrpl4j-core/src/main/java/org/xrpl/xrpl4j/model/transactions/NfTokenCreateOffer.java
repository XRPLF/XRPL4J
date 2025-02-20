package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
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
import org.xrpl.xrpl4j.model.flags.NfTokenCreateOfferFlags;

import java.util.Optional;

/**
 * The {@link NfTokenCreateOffer} transaction creates either a new Sell offer
 * for an NfToken owned by the account executing the transaction or a new Buy offer
 * for NfToken owned by another account.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableNfTokenCreateOffer.class)
@JsonDeserialize(as = ImmutableNfTokenCreateOffer.class)
public interface NfTokenCreateOffer extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableNfTokenCreateOffer.Builder}.
   */
  static ImmutableNfTokenCreateOffer.Builder builder() {
    return ImmutableNfTokenCreateOffer.builder();
  }

  /**
   * Indicates the AccountID of the account that initiated the
   * transaction.
   *
   * @return The {@link Address} of the account that created the Offer.
   */
  @JsonProperty("Account")
  Address account();

  /**
   * Identifies the TokenID of the NfToken object that the
   * offer references.
   *
   * @return Offer is created for the token with TokenID.
   */
  @JsonProperty("NFTokenID")
  NfTokenId nfTokenId();

  /**
   * Indicates the amount expected or offered for the Token.
   *
   * <p>The amount must be non-zero, except where this is an
   * offer is an offer to sell and the asset is XRP; then it
   * is legal to specify an amount of zero, which means that
   * the current owner of the token is giving it away, gratis,
   * either to anyone at all, or to the account identified by
   * the Destination field.
   *
   * @return Expected Amount of type {@link CurrencyAmount} for the token.
   */
  @JsonProperty("Amount")
  CurrencyAmount amount();

  /**
   * Indicates the AccountID of the account that owns the
   * corresponding NfToken.
   *
   * <p>If the offer is to buy a token, this field must be present
   * and it must be different than Account (since an offer to
   * buy a token one already holds is meaningless).
   *
   * If the offer is to sell a token, this field must not be
   * present, as the owner is, implicitly, the same as Account
   * (since an offer to sell a token one doesn't already hold
   * is meaningless).</p>
   *
   * @return An {@link Optional} field Owner of type {@link Address}.
   */
  @JsonProperty("Owner")
  Optional<Address> owner();

  /**
   * Indicates the time after which the offer will no longer
   * be valid. The value is the number of seconds since the
   * Ripple Epoch.
   *
   * @return An {@link Optional} field Owner of type {@link UnsignedLong}.
   */
  @JsonProperty("Expiration")
  Optional<UnsignedLong> expiration();

  /**
   * If present, indicates that this offer may only be
   * accepted by the specified account. Attempts by other
   * accounts to accept this offer MUST fail.
   *
   * @return An {@link Optional} field Destination of type {@link Address}.
   */
  @JsonProperty("Destination")
  Optional<Address> destination();

  /**
   * Set of {@link NfTokenCreateOfferFlags}s for this {@link NfTokenCreateOffer}.
   *
   * @return The {@link NfTokenCreateOfferFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default NfTokenCreateOfferFlags flags() {
    return NfTokenCreateOfferFlags.empty();
  }

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default NfTokenCreateOffer normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.NFTOKEN_CREATE_OFFER);
    return this;
  }
}
