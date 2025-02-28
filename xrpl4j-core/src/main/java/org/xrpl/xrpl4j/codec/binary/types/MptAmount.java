package org.xrpl.xrpl4j.codec.binary.types;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: binary-codec
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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

/**
 * Model for XRPL MPT Amount JSON.
 */
@Immutable
@JsonSerialize(as = ImmutableMptAmount.class)
@JsonDeserialize(as = ImmutableMptAmount.class)
interface MptAmount {

  /**
   * Construct a {@code MptAmount} builder.
   *
   * @return An {@link ImmutableMptAmount.Builder}.
   */
  static ImmutableMptAmount.Builder builder() {
    return ImmutableMptAmount.builder();
  }

  String value();

  @JsonProperty("mpt_issuance_id")
  String mptIssuanceId();

  @Value.Derived
  @JsonIgnore
  default boolean isNegative() {
    return value().startsWith("-");
  }

  @Value.Derived
  @JsonIgnore
  default UnsignedLong unsignedLongValue() {
    return isNegative() ?
      UnsignedLong.valueOf(value().substring(1)) :
      UnsignedLong.valueOf(value());
  }
}
