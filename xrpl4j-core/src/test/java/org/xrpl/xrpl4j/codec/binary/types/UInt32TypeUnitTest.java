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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UInt32TypeUnitTest {

  private final UInt32Type codec = new UInt32Type();

  @Test
  void decode() {
    assertThat(codec.fromHex("00000000").toHex()).isEqualTo("00000000");
    assertThat(codec.fromHex("0000000F").toHex()).isEqualTo("0000000F");
    assertThat(codec.fromHex("FFFFFFFF").toHex()).isEqualTo("FFFFFFFF");
  }

  @Test
  void encode() {
    assertThat(codec.fromJson("0").toHex()).isEqualTo("00000000");
    assertThat(codec.fromJson("15").toHex()).isEqualTo("0000000F");
    assertThat(codec.fromJson("65535").toHex()).isEqualTo("0000FFFF");
    assertThat(codec.fromJson("4294967295").toHex()).isEqualTo("FFFFFFFF");
  }

  @Test
  void encodeOutOfBounds() {
    assertThrows(IllegalArgumentException.class, () -> codec.fromJson("4294967296"));
  }

}
