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
 * The {@link CheckCash} transaction attempts to redeem a Check object in the ledger to receive up to the amount
 * authorized by the corresponding {@link CheckCreate} transaction. Only the Destination address of a Check can cash
 * it with a CheckCash transaction. Cashing a check this way is similar to executing a {@link Payment} initiated by
 * the destination.
 *
 * <p>Since the funds for a check are not guaranteed, redeeming a Check can fail because the sender does not have a
 * high enough balance or because there is not enough liquidity to deliver the funds. If this happens, the Check
 * remains in the ledger and the destination can try to cash it again later, or for a different amount.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCheckCash.class)
@JsonDeserialize(as = ImmutableCheckCash.class)
public interface CheckCash extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableCheckCash.Builder}.
   */
  static ImmutableCheckCash.Builder builder() {
    return ImmutableCheckCash.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link CheckCash}, which only allows the
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
   * The ID of the Check ledger object to cash, as a 64-character hexadecimal string.
   *
   * @return A {@link Hash256} containing the Check ID.
   */
  @JsonProperty("CheckID")
  Hash256 checkId();

  /**
   * Redeem the Check for exactly this amount, if possible.
   * The currency must match that of the {@link CheckCreate#sendMax()}SendMax of the corresponding {@link CheckCreate}
   * transaction. You must provide either this field or {@link CheckCash#deliverMin()}.
   *
   * @return An {@link Optional} of type {@link CurrencyAmount} containing the check amount.
   */
  @JsonProperty("Amount")
  Optional<CurrencyAmount> amount();

  /**
   * Redeem the Check for at least this amount and for as much as possible.
   * The currency must match that of the {@link CheckCreate#sendMax()}SendMax of the corresponding {@link CheckCreate}
   * transaction. You must provide either this field or {@link CheckCash#amount()}.
   *
   * @return An {@link Optional} of type {@link CurrencyAmount} containing the minimum delivery amount for this check.
   */
  @JsonProperty("DeliverMin")
  Optional<CurrencyAmount> deliverMin();

  /**
   * Ensure that either {@link CheckCash#amount()} or {@link CheckCash#deliverMin()} is present, but not both.
   */
  @Value.Check
  default void validateOnlyOneAmountSet() {
    Preconditions.checkArgument((amount().isPresent() || deliverMin().isPresent()) &&
        !(amount().isPresent() && deliverMin().isPresent()),
      "The CheckCash transaction must include either amount or deliverMin, but not both.");
  }

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default CheckCash normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.CHECK_CASH);
    return this;
  }
}
