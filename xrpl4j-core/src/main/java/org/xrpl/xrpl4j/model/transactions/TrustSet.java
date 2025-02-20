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
import org.xrpl.xrpl4j.model.flags.TrustSetFlags;

import java.util.Optional;

/**
 * Create or modify a trust line linking two accounts.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTrustSet.class)
@JsonDeserialize(as = ImmutableTrustSet.class)
public interface TrustSet extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableTrustSet.Builder}.
   */
  static ImmutableTrustSet.Builder builder() {
    return ImmutableTrustSet.builder();
  }

  /**
   * Set of {@link TrustSetFlags}s for this {@link TrustSet}, which have been properly combined to yield a
   * {@link TrustSetFlags} object containing the {@link Long} representation of the set bits.
   *
   * <p>The value of the flags can either be set manually, or constructed using {@link TrustSetFlags.Builder}.
   *
   * @return The {@link TrustSetFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TrustSetFlags flags() {
    return TrustSetFlags.empty();
  }

  /**
   * The {@link IssuedCurrencyAmount} defining the trust line to create or modify.
   *
   * @return An {@link IssuedCurrencyAmount} containing the amount of the trust line.
   */
  @JsonProperty("LimitAmount")
  IssuedCurrencyAmount limitAmount();

  /**
   * Value incoming balances on this trust line at the ratio of this number per 1,000,000,000 units.
   * A value of 0 is shorthand for treating balances at face value.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} defining the inbound quality.
   */
  @JsonProperty("QualityIn")
  Optional<UnsignedInteger> qualityIn();

  /**
   * Value outgoing balances on this trust line at the ratio of this number per 1,000,000,000 units.
   * A value of 0 is shorthand for treating balances at face value.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} defining the outbound quality.
   */
  @JsonProperty("QualityOut")
  Optional<UnsignedInteger> qualityOut();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default TrustSet normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.TRUST_SET);
    return this;
  }
}
