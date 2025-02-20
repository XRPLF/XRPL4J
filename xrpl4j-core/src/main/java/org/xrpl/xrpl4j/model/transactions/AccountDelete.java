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
 * An {@link AccountDelete} transaction deletes an account and any objects it owns in the XRP Ledger, if possible,
 * sending the account's remaining XRP to a specified destination account.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAccountDelete.class)
@JsonDeserialize(as = ImmutableAccountDelete.class)
public interface AccountDelete extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableAccountDelete.Builder}.
   */
  static ImmutableAccountDelete.Builder builder() {
    return ImmutableAccountDelete.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link AccountDelete}, which only allows the
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
   * The {@link Address} of an account to receive any leftover XRP after deleting the sending account. Must be a funded
   * account in the ledger, and must not be the sending account.
   *
   * @return The {@link Address} of the leftover XRP destination account.
   */
  @JsonProperty("Destination")
  Address destination();

  /**
   * Arbitrary destination tag that identifies a hosted recipient or other information for the recipient of the deleted
   * account's leftover XRP.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the tag of the destination account.
   */
  @JsonProperty("DestinationTag")
  Optional<UnsignedInteger> destinationTag();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AccountDelete normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.ACCOUNT_DELETE);
    return this;
  }
}
