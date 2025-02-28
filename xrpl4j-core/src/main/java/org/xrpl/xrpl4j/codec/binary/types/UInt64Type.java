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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Sets;
import com.google.common.primitives.UnsignedLong;
import org.xrpl.xrpl4j.codec.binary.definitions.FieldInstance;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;

import java.util.Set;

/**
 * Codec for XRPL UInt64 type.
 */
public class UInt64Type extends UIntType<UInt64Type> {

  /**
   * These fields are represented as base 10 Strings in JSON, whereas all other STUInt64s are represented in base16.
   */
  protected static final Set<String> BASE_10_UINT64_FIELD_NAMES = Sets.newHashSet(
    "MaximumAmount", "OutstandingAmount", "MPTAmount"
  );

  public UInt64Type() {
    this(UnsignedLong.ZERO);
  }

  public UInt64Type(UnsignedLong value) {
    super(value, 64);
  }

  @Override
  public UInt64Type fromParser(BinaryParser parser) {
    return new UInt64Type(parser.readUInt64());
  }

  @Override
  public UInt64Type fromJson(JsonNode value) {
    throw new UnsupportedOperationException("Cannot construct UInt64Type from JSON without a FieldInstance. Call " +
      "the overload of this method that accepts a FieldInstance instead.");
  }

  @Override
  public UInt64Type fromJson(JsonNode value, FieldInstance fieldInstance) {
    int radix = getRadix(fieldInstance);
    return new UInt64Type(UnsignedLong.valueOf(value.asText(), radix));
  }

  @Override
  public JsonNode toJson() {
    throw new UnsupportedOperationException("Cannot convert UInt64Type to JSON without a FieldInstance. Call " +
      "the overload of this method that accepts a FieldInstance instead.");
  }

  @Override
  public JsonNode toJson(FieldInstance fieldInstance) {
    int radix = getRadix(fieldInstance);
    return new TextNode(UnsignedLong.valueOf(toHex(), 16).toString(radix).toUpperCase());
  }

  /**
   * Most UInt64s are represented as hex Strings in JSON. However, some MPT related fields are represented in base 10 in
   * JSON. This method determines the radix of the field based on the supplied {@link FieldInstance}'s name.
   *
   * @param fieldInstance A {@link FieldInstance}.
   *
   * @return An int representing the radix.
   */
  private static int getRadix(FieldInstance fieldInstance) {
    int radix = 16;
    if (BASE_10_UINT64_FIELD_NAMES.contains(fieldInstance.name())) {
      radix = 10;
    }
    return radix;
  }
}
