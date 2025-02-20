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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.jackson.modules.BaseFeeDropsDeserializer;

import java.util.Optional;

/**
 * A {@link SetFee} pseudo-transaction marks a change in transaction cost or reserve requirements as a result of Fee
 * Voting.
 *
 * @see "https://xrpl.org/setfee.html"
 */
@Value.Immutable
@JsonSerialize(as = ImmutableSetFee.class)
@JsonDeserialize(as = ImmutableSetFee.class)
public interface SetFee extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableSetFee.Builder}.
   */
  static ImmutableSetFee.Builder builder() {
    return ImmutableSetFee.builder();
  }

  /**
   * The charge, in drops of XRP, for the reference transaction, as hex. (This is the transaction cost before scaling
   * for load.)
   *
   * <p>This method only exists for historical purposes. When deserialized from a {@link SetFee} transaction from
   * ledgers prior to the {@code XRPFees} amendment, this field will still be set based on {@link #baseFeeDrops()}.
   *
   * @return A hex {@link String} baseFee value.
   */
  @Value.Derived
  @JsonIgnore
  default String baseFee() {
    return baseFeeDrops().value().toString(16);
  }

  /**
   * The charge, in drops of XRP, for the reference transaction (This is the transaction cost before scaling for load).
   *
   * <p>This field will also be populated with the {@code BaseFee} value from any {@link SetFee} transactions
   * that occurred before the XRPFees amendment took effect.</p>
   *
   * @return An {@link XrpCurrencyAmount}.
   */
  @JsonProperty("BaseFeeDrops")
  @JsonAlias("BaseFee")
  @JsonDeserialize(using = BaseFeeDropsDeserializer.class)
  XrpCurrencyAmount baseFeeDrops();

  /**
   * The cost, in fee units, of the reference transaction. {@link SetFee} transactions deserialized from ledgers prior
   * to the {@code XRPFees} amendment will always have this field, but transactions deserialized from ledgers post
   * {@code XRPFees} activation will never have this field.
   *
   * @return An {@link UnsignedInteger} cost of ref transaction.
   */
  @JsonProperty("ReferenceFeeUnits")
  Optional<UnsignedInteger> referenceFeeUnits();

  /**
   * The base reserve, in drops.
   *
   * <p>This method only exists for historical purposes. When deserialized from a {@link SetFee} transaction from
   * ledgers prior to the {@code XRPFees} amendment, this field will still be set based on {@link #reserveBaseDrops()}}.
   *
   * @return An {@link UnsignedInteger} base reserve value in {@link org.xrpl.xrpl4j.model.client.fees.FeeDrops}.
   */
  @Value.Derived
  @JsonIgnore
  default UnsignedInteger reserveBase() {
    return UnsignedInteger.valueOf(reserveBaseDrops().value().longValue());
  }

  /**
   * The base reserve, as an {@link XrpCurrencyAmount}.
   *
   * <p>This field will also be populated with the {@code ReserveBase} value from any {@link SetFee} transactions
   * that occurred before the XRPFees amendment took effect.</p>
   *
   * @return An {@link XrpCurrencyAmount}.
   */
  @JsonProperty("ReserveBaseDrops")
  @JsonAlias("ReserveBase")
  XrpCurrencyAmount reserveBaseDrops();

  /**
   * The incremental reserve, in drops.
   *
   * <p>This method only exists for historical purposes. When deserialized from a {@link SetFee} transaction from
   * ledgers prior to the {@code XRPFees} amendment, this field will still be set based on
   * {@link #reserveIncrementDrops()}.
   *
   * @return An {@link UnsignedInteger} incremental reserve in {@link org.xrpl.xrpl4j.model.client.fees.FeeDrops}.
   */
  @Value.Derived
  @JsonIgnore
  default UnsignedInteger reserveIncrement() {
    return UnsignedInteger.valueOf(reserveIncrementDrops().value().longValue());
  }

  /**
   * The incremental reserve, as an {@link XrpCurrencyAmount}.
   *
   * <p>This field will also be populated with the {@code ReserveIncrement} value from any {@link SetFee} transactions
   * that occurred before the XRPFees amendment took effect.</p>
   *
   * @return An {@link XrpCurrencyAmount}.
   */
  @JsonProperty("ReserveIncrementDrops")
  @JsonAlias("ReserveIncrement")
  XrpCurrencyAmount reserveIncrementDrops();

  /**
   * The index of the ledger version where this pseudo-transaction appears. This distinguishes the pseudo-transaction
   * from other occurrences of the same change. Omitted for some historical SetFee pseudo-transactions hence making it
   * optional.
   *
   * @return A {@link LedgerIndex} to indicates where the tx appears.
   */
  @JsonProperty("LedgerSequence")
  Optional<LedgerIndex> ledgerSequence();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default SetFee normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.SET_FEE);
    return this;
  }
}
