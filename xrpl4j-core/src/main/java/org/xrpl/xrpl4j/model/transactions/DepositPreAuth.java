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
 * A {@link DepositPreAuth} transaction gives another account pre-approval to deliver payments to the sender of
 * this transaction. This is only useful if the sender of this transaction is using (or plans to use)
 * <a href="https://xrpl.org/depositauth.html">Deposit Authorization</a>.
 *
 * <p>You can use this transaction to preauthorize certain counterparties before you enable Deposit Authorization.
 * This may be useful to ensure a smooth transition from not requiring deposit authorization to requiring it.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableDepositPreAuth.class)
@JsonDeserialize(as = ImmutableDepositPreAuth.class)
public interface DepositPreAuth extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableDepositPreAuth.Builder}.
   */
  static ImmutableDepositPreAuth.Builder builder() {
    return ImmutableDepositPreAuth.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link DepositPreAuth}, which only allows the
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
   * The XRP Ledger {@link Address} of the sender to preauthorize.
   *
   * @return An {@link Optional} of type {@link Address} of the sender to preauthorize.
   */
  @JsonProperty("Authorize")
  Optional<Address> authorize();

  /**
   * The XRP Ledger {@link Address} of a sender whose preauthorization should be revoked.
   *
   * @return An {@link Optional} of type {@link Address} of the sender to unauthorize.
   */
  @JsonProperty("Unauthorize")
  Optional<Address> unauthorize();

  /**
   * Validate that either {@link DepositPreAuth#authorize()} or {@link DepositPreAuth#unauthorize()} is present,
   * but not both.
   */
  @Value.Check
  default void validateFieldPresence() {
    Preconditions.checkArgument((authorize().isPresent() || unauthorize().isPresent()) &&
        !(authorize().isPresent() && unauthorize().isPresent()),
      "The DepositPreAuth transaction must include either Authorize or Unauthorize, but not both.");
  }

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default DepositPreAuth normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.DEPOSIT_PRE_AUTH);
    return this;
  }
}
