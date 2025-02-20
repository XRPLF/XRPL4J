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

/**
 * Cancels an unredeemed Check, removing it from the ledger without sending any money. The source or the
 * destination of the check can cancel a Check at any time using this transaction type.
 * If the Check has expired, any address can cancel it.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCheckCancel.class)
@JsonDeserialize(as = ImmutableCheckCancel.class)
public interface CheckCancel extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableCheckCancel.Builder}.
   */
  static ImmutableCheckCancel.Builder builder() {
    return ImmutableCheckCancel.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link CheckCancel}, which only allows the
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
   * The ID of the Check ledger object to cancel, as a 64-character hexadecimal string.
   *
   * @return A {@link Hash256} containing the ID of the Check ledger object in hexadecimal form.
   */
  @JsonProperty("CheckID")
  Hash256 checkId();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default CheckCancel normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.CHECK_CANCEL);
    return this;
  }
}
