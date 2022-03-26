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
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.Flags;

/**
 * Return escrowed XRP to the sender.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableEscrowCancel.class)
@JsonDeserialize(as = ImmutableEscrowCancel.class)
public interface EscrowCancel extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableEscrowCancel.Builder}.
   */
  static ImmutableEscrowCancel.Builder builder() {
    return ImmutableEscrowCancel.builder();
  }

  /**
   * Set of {@link Flags.TransactionFlags}s for this {@link EscrowCancel}, which only allows {@code tfFullyCanonicalSig}
   * flag.
   *
   * <p>The value of the flags cannot be set manually, but exists for JSON serialization/deserialization only and for
   * proper signature computation in rippled.
   *
   * @return Always {@link Flags.TransactionFlags} with {@code tfFullyCanonicalSig} set.
   */
  @JsonProperty("Flags")
  @Value.Derived
  default Flags.TransactionFlags flags() {
    return new Flags.TransactionFlags.Builder().tfFullyCanonicalSig(true).build();
  }

  /**
   * {@link Address} of the source account that funded the escrow payment.
   *
   * @return The {@link Address} of the escrow owner.
   */
  @JsonProperty("Owner")
  Address owner();

  /**
   * The {@link EscrowCreate#sequence()} of the transaction that created the escrow to cancel.
   *
   * @return An {@link UnsignedInteger} representing the sequence of the {@link EscrowCreate} transaction that created
   *   the escrow.
   */
  @JsonProperty("OfferSequence")
  UnsignedInteger offerSequence();

}
