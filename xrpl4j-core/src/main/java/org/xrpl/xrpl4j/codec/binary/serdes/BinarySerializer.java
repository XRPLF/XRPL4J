package org.xrpl.xrpl4j.codec.binary.serdes;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByte;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.FieldHeaderCodec;
import org.xrpl.xrpl4j.codec.binary.definitions.FieldInstance;
import org.xrpl.xrpl4j.codec.binary.types.SerializedType;

import java.util.Objects;

/**
 * Serializes JSON to XRPL binary format.
 */
public class BinarySerializer {

  private final UnsignedByteArray sink;

  /**
   * Required-args Constructor.
   *
   * @param sink An {@link UnsignedByteArray}.
   */
  public BinarySerializer(final UnsignedByteArray sink) {
    this.sink = sink;
  }

  public void put(final String hexBytes) {
    sink.append(UnsignedByteArray.fromHex(hexBytes));
  }

  public void write(final UnsignedByteArray list) {
    this.sink.append(list);
  }

  /**
   * Calculate the header of Variable Length encoded bytes.
   *
   * @param length the length of the bytes.
   */
  private UnsignedByteArray encodeVariableLength(int length) {
    if (length <= 192) {
      return UnsignedByteArray.of(UnsignedByte.of(length));
    } else if (length <= 12480) {
      length -= 193;
      int byte1 = 193 + (length >>> 8);
      int byte2 = length & 0xff;
      return UnsignedByteArray.of(UnsignedByte.of(byte1), UnsignedByte.of(byte2));
    } else if (length <= 918744) {
      length -= 12481;
      int byte1 = 241 + (length >>> 16);
      int byte2 = (length >> 8) & 0xff;
      int byte3 = length & 0xff;
      return UnsignedByteArray.of(UnsignedByte.of(byte1), UnsignedByte.of(byte2), UnsignedByte.of(byte3));
    }
    throw new Error("Overflow error");
  }

  /**
   * Write field and value to BinarySerializer.
   *
   * @param field A {@link FieldInstance} to write into a {@link BinarySerializer}.
   * @param value A {@link SerializedType} to write into.
   */
  public void writeFieldAndValue(final FieldInstance field, final SerializedType value) {
    Objects.requireNonNull(field);
    Objects.requireNonNull(value);
    String fieldHeaderHex = FieldHeaderCodec.getInstance().encode(field.name());
    this.sink.append(UnsignedByteArray.fromHex(fieldHeaderHex));

    if (field.isVariableLengthEncoded()) {
      this.writeLengthEncoded(value);
    } else {
      value.toBytesSink(this.sink);
    }
  }

  /**
   * Write field and value to BinarySerializer.
   *
   * @param field A {@link FieldInstance} to write into a {@link BinarySerializer}.
   * @param value A {@link JsonNode} to write into.
   *
   * @throws JsonProcessingException if {@code value} is not properly formed JSON.
   */
  public void writeFieldAndValue(final FieldInstance field, final JsonNode value) throws JsonProcessingException {
    Objects.requireNonNull(field);
    Objects.requireNonNull(value);
    SerializedType typedValue = SerializedType.getTypeByName(field.type()).fromJson(value, field);

    writeFieldAndValue(field, typedValue);
  }

  /**
   * Write a variable length encoded value to the BinarySerializer.
   *
   * @param value length encoded value to write to BytesList.
   */
  public void writeLengthEncoded(final SerializedType value) {
    Objects.requireNonNull(value);
    UnsignedByteArray bytes = UnsignedByteArray.empty();
    value.toBytesSink(bytes);
    this.put(this.encodeVariableLength(bytes.length()).hexValue());
    this.write(bytes);
  }

}
