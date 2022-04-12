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
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.common.primitives.UnsignedLong;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;

/**
 * Codec for XRPL UInt8 type.
 */
public class UInt8Type extends UIntType<UInt8Type> {

  public UInt8Type() {
    this(UnsignedLong.ZERO);
  }

  public UInt8Type(UnsignedLong value) {
    super(value, 8);
  }

  @Override
  public UInt8Type fromParser(BinaryParser parser) {
    return new UInt8Type(parser.readUInt8());
  }

  @Override
  public UInt8Type fromJson(JsonNode value) {
    return new UInt8Type(UnsignedLong.valueOf(value.asText()));
  }

  @Override
  public JsonNode toJson() {
    return new IntNode(UnsignedLong.valueOf(toHex(), 16).intValue());
  }

}
