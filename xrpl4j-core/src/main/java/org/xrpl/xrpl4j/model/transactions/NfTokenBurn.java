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

import java.util.Optional;

/**
 * The {@link NfTokenBurn} transaction is used to remove an NfToken object from the
 * NfTokenPage in which it is being held, effectively removing the token from
 * the ledger ("burning" it).
 *
 * <p>If this operation succeeds, the corresponding NfToken is removed. If this
 * operation empties the NfTokenPage holding the NfToken or results in the
 * consolidation, thus removing an NfTokenPage, the owner’s reserve requirement
 * is reduced by one.</p>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableNfTokenBurn.class)
@JsonDeserialize(as = ImmutableNfTokenBurn.class)
public interface NfTokenBurn extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableNfTokenBurn.Builder}.
   */
  static ImmutableNfTokenBurn.Builder builder() {
    return ImmutableNfTokenBurn.builder();
  }

  /**
   * Indicates the {@link Address} of the account that submitted this transaction. The account MUST
   * be either the present owner of the token or, if the lsfBurnable flag is set
   * in the NfToken, either the issuer account or an account authorized by the
   * issuer, i.e. Minter field in {@link AccountSet} set as the address of the issuer.
   *
   * @return An {@link Address} of the account initiating the burning of the NfToken.
   */
  @JsonProperty("Account")
  Address account();

  /**
   * Identifies the NfToken object to be removed by the transaction.
   *
   * @return The TokenID of the NfToken to be burned.
   */
  @JsonProperty("NFTokenID")
  NfTokenId nfTokenId();

  /**
   * Indicates the {@link Address} of the account that owns the NFToken if it is different
   * than the Account field. Only used to burn tokens which have the lsfBurnable flag enabled
   * and are not owned by the signing account.
   *
   * @return An {@link Address} of the account that owns the NFToken specified by TokenID.
   */
  @JsonProperty("Owner")
  Optional<Address> owner();

  /**
   * Set of {@link TransactionFlags}s for this {@link NfTokenBurn}, which only allows the
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
  default NfTokenBurn normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.NFTOKEN_BURN);
    return this;
  }
}
