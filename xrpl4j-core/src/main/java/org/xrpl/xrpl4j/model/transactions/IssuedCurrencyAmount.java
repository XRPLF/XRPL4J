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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Derived;

/**
 * A {@link CurrencyAmount} for Issued Currencies on the XRP Ledger.
 *
 * @see "https://xrpl.org/rippleapi-reference.html#value"
 */
@Value.Immutable
@JsonSerialize(as = ImmutableIssuedCurrencyAmount.class)
@JsonDeserialize(as = ImmutableIssuedCurrencyAmount.class)
public interface IssuedCurrencyAmount extends CurrencyAmount {

  /**
   * The maximum value that an {@link IssuedCurrencyAmount} can have.
   */
  String MAX_VALUE = "9999999999999999e80";

  /**
   * The minimum value that an {@link IssuedCurrencyAmount} can have.
   */
  String MIN_VALUE = "-9999999999999999e80";

  /**
   * The smallest possible positive value that an {@link IssuedCurrencyAmount} can have. Put another way, this value is
   * the closest an {@link IssuedCurrencyAmount}'s {@link #value()} can be to zero if it is positive.
   */
  String MIN_POSITIVE_VALUE = "1000000000000000e-96";

  /**
   * The largest possible negative value that an {@link IssuedCurrencyAmount} can have. Put another way, this value is
   * the closest an {@link IssuedCurrencyAmount}'s {@link #value()} can be to zero if it is negative.
   */
  String MAX_NEGATIVE_VALUE = "-1000000000000000e-96";

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableIssuedCurrencyAmount.Builder}.
   */
  static ImmutableIssuedCurrencyAmount.Builder builder() {
    return ImmutableIssuedCurrencyAmount.builder();
  }

  /**
   * Quoted decimal representation of the amount of currency. This can include scientific notation, such as 1.23e11
   * meaning 123,000,000,000. Both e and E may be used. Note that while this implementation merely holds a
   * {@link String} with no value restrictions, the XRP Ledger does not tolerate unlimited precision values. Instead,
   * non-XRP values (i.e., values held in this object) can have up to 16 decimal digits of precision, with a maximum
   * value of 9999999999999999e80. The smallest positive non-XRP value is 1e-81.
   *
   * @return A {@link String} containing the amount of this issued currency.
   */
  String value();

  /**
   * Arbitrary code for currency to issue. Cannot be XRP.
   *
   * @return A {@link String} containing the currency code.
   */
  String currency();

  /**
   * Unique account {@link Address} of the entity issuing the currency. In other words, the person or business where the
   * currency can be redeemed.
   *
   * @return The {@link Address} of the account of the issuer of this currency.
   */
  Address issuer();

  /**
   * Indicates whether this amount is positive or negative.
   *
   * @return {@code true} if this amount is negative; {@code false} otherwise (i.e., if the value is 0 or positive).
   */
  @Derived
  @JsonIgnore // <-- This is not actually part of the binary serialization format, so exclude from JSON
  @Auxiliary
  default boolean isNegative() {
    return value().startsWith("-");
  }

}
