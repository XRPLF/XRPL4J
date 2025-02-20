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
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.Optional;

/**
 * A SetRegularKey transaction assigns, changes, or removes the regular key pair associated with an account.
 *
 * <p>You can protect your account by assigning a regular key pair to it and using it instead of the master key
 * pair to sign transactions whenever possible. If your regular key pair is compromised, but your master key
 * pair is not, you can use a SetRegularKey transaction to regain control of your account.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableSetRegularKey.class)
@JsonDeserialize(as = ImmutableSetRegularKey.class)
public interface SetRegularKey extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableSetRegularKey.Builder}.
   */
  static ImmutableSetRegularKey.Builder builder() {
    return ImmutableSetRegularKey.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link SetRegularKey}, which only allows the
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
   * An {@link Address} that indicates the regular key pair to be assigned to the account. If omitted,
   * removes any existing regular key pair from the account. Must not match the master key pair for the address.
   *
   * @return The {@link Optional} {@link Address} indicating the regular key pair to use.
   */
  @JsonProperty("RegularKey")
  Optional<Address> regularKey();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default SetRegularKey normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.SET_REGULAR_KEY);
    return this;
  }

}
