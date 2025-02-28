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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.BinaryCodecObjectMapperFactory;
import org.xrpl.xrpl4j.codec.binary.definitions.FieldInstance;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Defines an abstract type serialization parent-class for all XRPL serialized type definitions.
 *
 * @param <T> The actual type of this {@link SerializedType}.
 */
public abstract class SerializedType<T extends SerializedType<T>> {

  @SuppressWarnings("all")
  private static final Map<String, Supplier<SerializedType<?>>> typeMap =
    new ImmutableMap.Builder<String, Supplier<SerializedType<?>>>()
      .put("AccountID", () -> new AccountIdType())
      .put("Amount", () -> new AmountType())
      .put("Blob", () -> new BlobType())
      .put("Currency", () -> new CurrencyType())
      .put("Hash128", () -> new Hash128Type())
      .put("Hash160", () -> new Hash160Type())
      .put("UInt192", () -> new UInt192Type())
      .put("Hash256", () -> new Hash256Type())
      .put("PathSet", () -> new PathSetType())
      .put("STArray", () -> new STArrayType())
      .put("STObject", () -> new STObjectType())
      .put("UInt8", () -> new UInt8Type())
      .put("UInt16", () -> new UInt16Type())
      .put("UInt32", () -> new UInt32Type())
      .put("UInt64", () -> new UInt64Type())
      .put("Vector256", () -> new Vector256Type())
      .put("Issue", () -> new IssueType())
      .put("XChainBridge", () -> new XChainBridgeType())
      .build();
  private final UnsignedByteArray bytes;

  public SerializedType(UnsignedByteArray bytes) {
    this.bytes = bytes;
  }

  /**
   * Get the {@link SerializedType} for the supplied {@code name}.
   *
   * @param name A {@link String} representing the name of a {@link SerializedType}.
   *
   * @return A {@link SerializedType} for the supplied {@code name}.
   */
  public static SerializedType<?> getTypeByName(String name) {
    return typeMap.get(name).get();
  }

  /**
   * Get the name of the supplied type.
   *
   * @param type A {@link SerializedType} representing the type to name.
   *
   * @return A {@link String} representing the name of {@code type}.
   */
  public static String getNameByType(SerializedType<?> type) {
    return typeMap.entrySet()
      .stream()
      .filter(entry -> entry.getValue().get().getClass().equals(type.getClass()))
      .map(Map.Entry::getKey)
      .findAny()
      .orElse(null);
  }

  /**
   * Obtain a {@link SerializedType} of type {@link T} using the supplied {@code parser}.
   *
   * @param parser A {@link BinaryParser} to use.
   *
   * @return A {@link T} based upon the information found in {@code parser}.
   */
  public T fromParser(BinaryParser parser) {
    throw new UnsupportedOperationException("This operation is only supported by specific sub-classes.");
  }

  /**
   * Obtain a {@link SerializedType} of type {@link T} using the supplied {@code parser}.
   *
   * @param parser     A {@link BinaryParser} to use.
   * @param lengthHint A hint/suggestion for the length of the content in {@code parser}.
   *
   * @return A {@link T} based upon the information found in {@code parser}.
   */
  public T fromParser(BinaryParser parser, int lengthHint) {
    return fromParser(parser);
  }

  /**
   * Obtain a {@link T} using the supplied {@code node}. Prefer using {@link #fromJson(JsonNode, FieldInstance)} over
   * this method, as some {@link SerializedType}s require a {@link FieldInstance} to accurately serialize and
   * deserialize.
   *
   * @param node A {@link JsonNode} to use.
   *
   * @return A {@link T} based upon the information found in {@code node}.
   *
   * @throws JsonProcessingException if {@code node} is not well-formed JSON.
   */
  public abstract T fromJson(JsonNode node) throws JsonProcessingException;

  /**
   * Obtain a {@link T} using the supplied {@link JsonNode} as well as a {@link FieldInstance}. Prefer using this method
   * where possible over {@link #fromJson(JsonNode)}, as some {@link SerializedType}s require a {@link FieldInstance} to
   * accurately serialize and deserialize.
   *
   * @param node          A {@link JsonNode} to serialize to binary.
   * @param fieldInstance The {@link FieldInstance} describing the field being serialized.
   *
   * @return A {@link T}.
   *
   * @throws JsonProcessingException If {@code node} is not well-formed JSON.
   */
  public T fromJson(JsonNode node, FieldInstance fieldInstance) throws JsonProcessingException {
    return fromJson(node);
  }

  /**
   * Construct a concrete instance of {@link SerializedType} from the supplied {@code json}.
   *
   * @param json A {@link String} containing JSON content.
   *
   * @return A {@link T}.
   */
  public T fromJson(String json) {
    try {
      JsonNode node = BinaryCodecObjectMapperFactory.getObjectMapper().readTree(json);
      UnsignedByteArray byteList = UnsignedByteArray.empty();
      T newValue = fromJson(node);
      newValue.toBytesSink(byteList);
      return newValue;
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Construct a concrete instance of {@link SerializedType} from the supplied {@code hex}.
   *
   * @param hex A String of hex-encoded binary data.
   *
   * @return A {@link T}.
   */
  public T fromHex(String hex) {
    return fromParser(new BinaryParser(hex));
  }

  /**
   * Construct a concrete instance of {@link SerializedType} from the supplied {@code hex}.
   *
   * @param hex        A {@link String} containing hex-encoded binary content.
   * @param lengthHint An int representing the expected length of {@code hex}.
   *
   * @return A {@link T}.
   */
  public T fromHex(String hex, int lengthHint) {
    Objects.requireNonNull(hex);
    return fromParser(new BinaryParser(hex), lengthHint);
  }

  /**
   * Append this type's bytes to {@code list}.
   *
   * @param list An {@link UnsignedByteArray}.
   */
  public void toBytesSink(final UnsignedByteArray list) {
    Objects.requireNonNull(list);
    list.append(this.bytes);
  }

  /**
   * Convert this {@link SerializedType} to a byte array.
   *
   * @return An array of bytes.
   */
  public byte[] toBytes() {
    return bytes.toByteArray();
  }

  /**
   * Convert this {@link SerializedType} to a {@link JsonNode}. Prefer using {@link #toJson(FieldInstance)} over this
   * method where possible, as some {@link SerializedType}s require a {@link FieldInstance} to accurately serialize and
   * deserialize.
   *
   * @return A {@link JsonNode}.
   */
  public JsonNode toJson() {
    return new TextNode(toHex());
  }

  /**
   * Convert this {@link SerializedType} to a {@link JsonNode} based on the supplied {@link FieldInstance}. Prefer using
   * this method where possible over {@link #fromJson(JsonNode)}, as some {@link SerializedType}s require a
   * {@link FieldInstance} to accurately serialize and deserialize.
   *
   * @param fieldInstance A {@link FieldInstance} describing the field being deserialized.
   *
   * @return A {@link JsonNode}.
   */
  public JsonNode toJson(FieldInstance fieldInstance) {
    return toJson();
  }

  /**
   * Convert this {@link SerializedType} to a hex-encoded {@link String}.
   *
   * @return A {@link String}.
   */
  public final String toHex() {
    return bytes.hexValue();
  }

  /**
   * Convert this {@link SerializedType} to an {@link UnsignedByteArray}.
   *
   * @return A {@link UnsignedByteArray}.
   */
  protected UnsignedByteArray value() {
    return bytes;
  }

  @Override
  public String toString() {
    return this.toHex();
  }
}
