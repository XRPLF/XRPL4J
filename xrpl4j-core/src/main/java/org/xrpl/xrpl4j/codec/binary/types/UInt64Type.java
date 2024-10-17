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
import com.google.common.primitives.UnsignedLong;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;

/**
 * Codec for XRPL UInt64 type.
 */
public class UInt64Type extends UIntType<UInt64Type> {

  private final int radix;

  public UInt64Type(int radix) {
    this(UnsignedLong.ZERO, radix);
  }

  public UInt64Type(UnsignedLong value, int radix) {
    super(value, 64);
    this.radix = radix;
  }

  @Override
  public UInt64Type fromParser(BinaryParser parser) {
    return new UInt64Type(parser.readUInt64(), radix);
  }

  @Override
  public UInt64Type fromJson(JsonNode value) {
    // STUInt64s are represented as hex-encoded Strings in JSON.
    return new UInt64Type(UnsignedLong.valueOf(value.asText(), radix), radix);
  }

  @Override
  public JsonNode toJson() {
    return new TextNode(UnsignedLong.valueOf(toHex(), 16).toString(radix).toUpperCase());
  }
}
