package org.xrpl.xrpl4j.codec.binary.types;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.definitions.FieldInstance;

import java.util.stream.Stream;

public class UInt64TypeUnitTest {
  private final UInt64Type type = new UInt64Type();

  @Test
  void testFromHex() {
    assertThat(type.fromHex("0000000000000000").toHex()).isEqualTo("0000000000000000");
    assertThat(type.fromHex("000000000000000F").toHex()).isEqualTo("000000000000000F");
    assertThat(type.fromHex("00000000FFFFFFFF").toHex()).isEqualTo("00000000FFFFFFFF");
    assertThat(type.fromHex("FFFFFFFFFFFFFFFF").toHex()).isEqualTo("FFFFFFFFFFFFFFFF");
  }

  @ParameterizedTest
  @MethodSource(value = "base16JsonArguments")
  void testFromJsonBase16(TextNode json) {
    FieldInstance base16FieldInstance = mock(FieldInstance.class);
    when(base16FieldInstance.name()).thenReturn("Base16Field");
    assertThat(type.fromJson(json, base16FieldInstance).toHex())
      .isEqualTo(Strings.padStart(json.asText(), 16, '0'));
    assertThat(type.fromJson(json, base16FieldInstance).toJson(base16FieldInstance)).isEqualTo(json);
  }

  @ParameterizedTest
  @MethodSource(value = "base10JsonArguments")
  void testFromJsonBase10(TextNode json) {
    UInt64Type.BASE_10_UINT64_FIELD_NAMES.forEach(
      b10FieldName -> {
        FieldInstance base10FieldInstance = mock(FieldInstance.class);
        when(base10FieldInstance.name()).thenReturn(b10FieldName);
        String expectedHex = Strings.padStart(UnsignedLong.valueOf(json.asText()).toString(16).toUpperCase(), 16, '0');
        assertThat(type.fromJson(json, base10FieldInstance).toHex())
          .isEqualTo(expectedHex);
        assertThat(type.fromJson(json, base10FieldInstance).toJson(base10FieldInstance)).isEqualTo(json);
      }
    );
  }

  @Test
  void fromJsonThrowsWithoutFieldInstance() {
    assertThatThrownBy(() -> type.fromJson(new TextNode("0")))
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void toJsonThrowsWithoutFieldInstance() {
    assertThatThrownBy(type::toJson)
      .isInstanceOf(UnsupportedOperationException.class);
  }

  private static Stream<TextNode> base16JsonArguments() {
    return Stream.of(
      new TextNode("0"),
      new TextNode("F"),
      new TextNode("FFFF"),
      new TextNode("FFFFFFFF"),
      new TextNode("FFFFFFFFFFFFFFFF")
    );
  }

  private static Stream<TextNode> base10JsonArguments() {
    return Stream.of(
      new TextNode("0"),
      new TextNode("15"),
      new TextNode("65535"),
      new TextNode("4294967295"),
      new TextNode("18446744073709551615")
    );
  }

  @Test
  void encodeOutOfBounds() {
    FieldInstance field = mock(FieldInstance.class);
    when(field.name()).thenReturn("Field");
    assertThrows(IllegalArgumentException.class, () -> type.fromJson(new TextNode("18446744073709551616"), field));
  }
}
