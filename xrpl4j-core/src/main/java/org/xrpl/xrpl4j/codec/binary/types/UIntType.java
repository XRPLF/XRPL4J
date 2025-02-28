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
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedLong;
import org.xrpl.xrpl4j.codec.addresses.ByteUtils;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;

/**
 * Base codec for XRPL UInt types.
 */
abstract class UIntType<T extends UIntType<T>> extends SerializedType<T> {

  public UIntType(UnsignedLong value, int bitSize) {
    super(UnsignedByteArray.fromHex(ByteUtils.padded(value.toString(16), bitSizeToHexLength(bitSize))));
  }

  public UIntType(UnsignedByteArray value, int bitSize) {
    super(UnsignedByteArray.fromHex(ByteUtils.padded(value.hexValue(), bitSizeToHexLength(bitSize))));
    Preconditions.checkArgument(
      value.length() == bitSize / 8,
      String.format("Invalid %s length: %s", this.getClass().getSimpleName(), value.length())
    );
  }

  private static int bitSizeToHexLength(int bitSize) {
    return bitSize / 4;
  }

  @Override
  public JsonNode toJson() {
    return new TextNode(UnsignedLong.valueOf(toHex(), 16).toString());
  }
}
