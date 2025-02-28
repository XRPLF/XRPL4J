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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

class HashTypeTest {

  public static final char DOUBLE_QUOTE = '"';
  private final Hash128Type codec128 = new Hash128Type();
  private final Hash160Type codec160 = new Hash160Type();
  private final UInt192Type codec192 = new UInt192Type();
  private final Hash256Type codec256 = new Hash256Type();

  @Test
  void decode() {
    assertThat(codec128.fromHex(bytes(16)).toHex()).isEqualTo(bytes(16));
    assertThat(codec160.fromHex(bytes(20)).toHex()).isEqualTo(bytes(20));
    assertThat(codec192.fromHex(bytes(24)).toHex()).isEqualTo(bytes(24));
    assertThat(codec256.fromHex(bytes(32)).toHex()).isEqualTo(bytes(32));
  }

  @Test
  void encode() {
    assertThat(codec128.fromJson(DOUBLE_QUOTE + bytes(16) + DOUBLE_QUOTE).toHex()).isEqualTo(bytes(16));
    assertThat(codec160.fromJson(DOUBLE_QUOTE + bytes(20) + DOUBLE_QUOTE).toHex()).isEqualTo(bytes(20));
    assertThat(codec192.fromJson(DOUBLE_QUOTE + bytes(24) + DOUBLE_QUOTE).toHex()).isEqualTo(bytes(24));
    assertThat(codec256.fromJson(DOUBLE_QUOTE + bytes(32) + DOUBLE_QUOTE).toHex()).isEqualTo(bytes(32));
  }

  @Test
  void encodeOutOfBounds() {
    assertThrows(IllegalArgumentException.class,
      () -> codec128.fromJson(bytes(20)));
  }

  private String bytes(int size) {
    return Strings.repeat("0F", size);
  }

}
