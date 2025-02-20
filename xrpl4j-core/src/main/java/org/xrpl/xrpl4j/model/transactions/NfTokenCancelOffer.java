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
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.List;

/**
 * The {@link NfTokenCancelOffer} transaction creates an NfToken object and adds it to the
 * relevant NfTokenPage object of the minter. If the transaction is
 * successful, the newly minted token will be owned by the minter(issuer) account
 * specified by the transaction.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableNfTokenCancelOffer.class)
@JsonDeserialize(as = ImmutableNfTokenCancelOffer.class)
public interface NfTokenCancelOffer extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableNfTokenCancelOffer.Builder}.
   */
  static ImmutableNfTokenCancelOffer.Builder builder() {
    return ImmutableNfTokenCancelOffer.builder();
  }

  /**
   * An array of NfTokenOffer Ids, each identifying an
   * NfTokenOffer object, which should be cancelled by this
   * transaction.
   *
   * <p>It is an error if an entry in this list points to an
   * object that is not an NfTokenOffer object. It is not an
   * error if an entry in this list points to an object that
   * does not exist.
   *
   * @return Array of TokenIDs, each identifying a unique NfToken object, to cancel the offers for.
   */
  @JsonProperty("NFTokenOffers")
  List<Hash256> tokenOffers();

  /**
   * Token Ids array should have atleast one value.
   */
  @Value.Check
  default void nonEmptyTokenOffers() {

    Preconditions.checkArgument(
      tokenOffers().size() > 0,
      String.format("List of token offers must be non-empty.")
    );
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link NfTokenCancelOffer}, which only allows the
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
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default NfTokenCancelOffer normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.NFTOKEN_CANCEL_OFFER);
    return this;
  }
}
