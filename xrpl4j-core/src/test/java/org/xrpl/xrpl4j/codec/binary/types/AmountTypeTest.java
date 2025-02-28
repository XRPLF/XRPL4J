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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * An extension of {@link BaseSerializerTypeTest} that tests serialization of amounts.
 */
class AmountTypeTest extends BaseSerializerTypeTest {

  private static final AmountType codec = new AmountType();

  static Stream<Arguments> dataDrivenFixtures() throws IOException {
    return dataDrivenFixturesForType(codec);
  }

  @Override
  SerializedType<?> getType() {
    return codec;
  }

  @Test
  void decodeXrpAmount() {
    // Positive
    assertThat(codec.fromHex("4000000000000064").toJson().asText()).isEqualTo("100");
    assertThat(codec.fromHex("416345785D8A0000").toJson().asText()).isEqualTo("100000000000000000");

    // Negative
    assertThat(codec.fromHex("0000000000000064").toJson().asText()).isEqualTo("-100");
    assertThat(codec.fromHex("016345785D8A0000").toJson().asText()).isEqualTo("-100000000000000000");
  }

  @Test
  void encodeXrpAmount() {
    // Positive
    assertThat(codec.fromJson("100").toHex()).isEqualTo("4000000000000064");
    assertThat(codec.fromJson("100000000000000000").toHex()).isEqualTo("416345785D8A0000");

    // Negative
    assertThat(codec.fromJson("-100").toHex()).isEqualTo("0000000000000064");
    assertThat(codec.fromJson("-100000000000000000").toHex()).isEqualTo("016345785D8A0000");
  }

  @Test
  void encodeDecodePositiveXrpAmount() throws JsonProcessingException {
    // Encode -> Decode -> Encode (Positive)
    {
      String encoded = codec.fromJson("100").toHex();  // encoded
      assertThat(encoded).isEqualTo("4000000000000064");

      AmountType decoded = codec.fromHex(encoded); // decoded
      assertThat(decoded.toJson().textValue()).isEqualTo("100");

      AmountType encodedAmountType = codec.fromJson(decoded.toJson()); // encoded
      assertThat(encodedAmountType.toHex()).isEqualTo("4000000000000064");
    }

    // Encode -> Decode -> Encode (Negative)
    {
      // Encode -> Decode -> Encode (Positive)
      String encoded = codec.fromJson("-100").toHex();  // encoded
      assertThat(encoded).isEqualTo("0000000000000064");

      AmountType decoded = codec.fromHex(encoded); // decoded
      assertThat(decoded.toJson().textValue()).isEqualTo("-100");

      AmountType encodedAmountType = codec.fromJson(decoded.toJson()); // encoded
      assertThat(encodedAmountType.toHex()).isEqualTo("0000000000000064");
    }

    // Decode -> Encode -> Decode (Positive)
    {
      AmountType decoded = codec.fromHex("4000000000000064"); // decoded
      assertThat(decoded.toJson().textValue()).isEqualTo("100");

      AmountType encodedAmountType = codec.fromJson(decoded.toJson()); // encoded
      assertThat(encodedAmountType.toHex()).isEqualTo("4000000000000064");

      decoded = codec.fromHex(encodedAmountType.toHex()); // decoded
      assertThat(decoded.toJson().textValue()).isEqualTo("100");
    }

    // Decode -> Encode -> Decode (Negative)
    {
      AmountType decoded = codec.fromHex("0000000000000064"); // decoded
      assertThat(decoded.toJson().textValue()).isEqualTo("-100");

      AmountType encodedAmountType = codec.fromJson(decoded.toJson()); // encoded
      assertThat(encodedAmountType.toHex()).isEqualTo("0000000000000064");

      decoded = codec.fromHex(encodedAmountType.toHex()); // decoded
      assertThat(decoded.toJson().textValue()).isEqualTo("-100");
    }
  }

  @Test
  void encodeOutOfBounds() {
    // Positive (too big)
    {
      IllegalArgumentException thrownError = assertThrows(
        IllegalArgumentException.class, () -> codec.fromJson("100000000000000001")
      );
      assertThat(thrownError.getMessage()).isEqualTo("100000000000000001 is an illegal amount");
    }
    // Positive (too small)
    {
      IllegalArgumentException thrownError = assertThrows(
        IllegalArgumentException.class, () -> codec.fromJson("0.0000005") // <-- 0.000001 is smallest positive XRP
      );
      assertThat(thrownError.getMessage()).isEqualTo("5.0E-7 is an illegal amount");
    }
    // Negative (too small)
    {
      IllegalArgumentException thrownError = assertThrows(
        IllegalArgumentException.class, () -> codec.fromJson("-100000000000000001")
      );
      assertThat(thrownError.getMessage()).isEqualTo("-100000000000000001 is an illegal amount");
    }
    // Negative (too big)
    {
      IllegalArgumentException thrownError = assertThrows(
        IllegalArgumentException.class, () -> codec.fromJson("-0.0000005") // <-- 0.000001 is largest negative XRP
      );
      assertThat(thrownError.getMessage()).isEqualTo("-5.0E-7 is an illegal amount");
    }
  }

  @Test
  void encodeCurrencyAmount() {
    assertThat(
      codec.fromJson("{\"value\":\"0\",\"currency\":\"USD\",\"issuer\":\"rDgZZ3wyprx4ZqrGQUkquE9Fs2Xs8XBcdw\"}")
        .toHex())
      .isEqualTo("800000000000000000000000000000000000000055534400000000008B1CE810C13D6F337DAC85863B3D70265A24DF44");

    assertThat(
      codec.fromJson("{\"value\":\"1\",\"currency\":\"USD\",\"issuer\":\"rDgZZ3wyprx4ZqrGQUkquE9Fs2Xs8XBcdw\"}")
        .toHex())
      .isEqualTo("D4838D7EA4C6800000000000000000000000000055534400000000008B1CE810C13D6F337DAC85863B3D70265A24DF44");

    assertThat(
      codec.fromJson("{\"value\":\"2\",\"currency\":\"USD\",\"issuer\":\"rDgZZ3wyprx4ZqrGQUkquE9Fs2Xs8XBcdw\"}")
        .toHex())
      .isEqualTo("D4871AFD498D000000000000000000000000000055534400000000008B1CE810C13D6F337DAC85863B3D70265A24DF44");

    assertThat(
      codec.fromJson("{\"value\":\"-2\",\"currency\":\"USD\",\"issuer\":\"rDgZZ3wyprx4ZqrGQUkquE9Fs2Xs8XBcdw\"}")
        .toHex())
      .isEqualTo("94871AFD498D000000000000000000000000000055534400000000008B1CE810C13D6F337DAC85863B3D70265A24DF44");

    assertThat(
      codec.fromJson("{\"value\":\"2.1\",\"currency\":\"USD\",\"issuer\":\"rDgZZ3wyprx4ZqrGQUkquE9Fs2Xs8XBcdw\"}")
        .toHex())
      .isEqualTo("D48775F05A07400000000000000000000000000055534400000000008B1CE810C13D6F337DAC85863B3D70265A24DF44");
  }

  @Test
  void decodeCurrencyAmount() {
    assertThat(
      codec.fromHex("D48775F05A07400000000000000000000000000000000000000000000000000000000000000000000000000000000000")
        .toJson().toString())
      .isEqualTo("{\"currency\":\"XRP\",\"value\":\"2.1\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrrhoLvTp\"}");
  }

  @Test
  void decodeNegativeCurrencyAmount() {
    assertThat(
      codec.fromHex("94838D7EA4C6800000000000000000000000000055534400000000000000000000000000000000000000000000000001")
        .toJson().toString())
      .isEqualTo("{\"currency\":\"USD\",\"value\":\"-1\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\"}");
  }

  @Test
  void encodeZeroCurrencyAmount() {
    String json = "{\"currency\":\"USD\",\"value\":\"0.0\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\"}";
    String hex = "800000000000000000000000000000000000000055534400000000000000000000000000000000000000000000000001";
    assertThat(codec.fromJson(json).toHex()).isEqualTo(hex);
  }

  @Test
  void encodeLargeCurrencyAmount() {
    String json = "{\"currency\":\"USD\",\"value\":\"1111111111111111.0\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\"}";
    String hex = "D843F28CB71571C700000000000000000000000055534400000000000000000000000000000000000000000000000001";
    assertThat(codec.fromJson(json).toHex()).isEqualTo(hex);
  }

  @Test
  void encodeLargeNegativeCurrencyAmount() {
    String json = "{\"currency\":\"USD\",\"value\":\"-1111111111111111.0\",\"issuer\":\"rrrrrrrrrrrrrrrrrrrrBZbvji\"}";
    String hex = "9843F28CB71571C700000000000000000000000055534400000000000000000000000000000000000000000000000001";
    assertThat(codec.fromJson(json).toHex()).isEqualTo(hex);
  }

  @Test
  void encodeDecodeMptAmount() {
    String json = "{\"value\":\"100\",\"mpt_issuance_id\":\"00002403C84A0A28E0190E208E982C352BBD5006600555CF\"}";
    AmountType fromJson = codec.fromJson(json);
    assertThat(fromJson.toHex())
      .isEqualTo("60000000000000006400002403C84A0A28E0190E208E982C352BBD5006600555CF");
    assertThat(fromJson.toJson().toString()).isEqualTo(json);
  }

  @Test
  void encodeDecodeMptAmountNegative() {
    String json = "{\"value\":\"-100\",\"mpt_issuance_id\":\"00002403C84A0A28E0190E208E982C352BBD5006600555CF\"}";
    AmountType fromJson = codec.fromJson(json);
    assertThat(fromJson.toHex())
      .isEqualTo("20000000000000006400002403C84A0A28E0190E208E982C352BBD5006600555CF");
    assertThat(fromJson.toJson().toString()).isEqualTo(json);
  }

  @Test
  void encodeDecodeLargestAmount() {
    String json = "{\"value\":\"9223372036854775807\"," +
      "\"mpt_issuance_id\":\"00002403C84A0A28E0190E208E982C352BBD5006600555CF\"}";
    AmountType fromJson = codec.fromJson(json);
    assertThat(fromJson.toHex())
      .isEqualTo("607FFFFFFFFFFFFFFF00002403C84A0A28E0190E208E982C352BBD5006600555CF");
    assertThat(fromJson.toJson().toString()).isEqualTo(json);
  }

  @Test
  void encodeDecodeLargestAmountNegative() {
    String json = "{\"value\":\"-9223372036854775807\"," +
      "\"mpt_issuance_id\":\"00002403C84A0A28E0190E208E982C352BBD5006600555CF\"}";
    AmountType fromJson = codec.fromJson(json);
    assertThat(fromJson.toHex())
      .isEqualTo("207FFFFFFFFFFFFFFF00002403C84A0A28E0190E208E982C352BBD5006600555CF");
    assertThat(fromJson.toJson().toString()).isEqualTo(json);
  }

  @Test
  void encodeMptAmountWithMoreThan63BitAmountThrows() {
    UnsignedLong maxLongPlusOne = UnsignedLong.valueOf(Long.MAX_VALUE).plus(UnsignedLong.ONE);
    String json = "{\"value\":\"" + maxLongPlusOne + "\"," +
      "\"mpt_issuance_id\":\"00002403C84A0A28E0190E208E982C352BBD5006600555CF\"}";
    assertThatThrownBy(() -> codec.fromJson(json)).isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid MPT amount. Maximum MPT value is (2^63 - 1)");
  }

  @Test
  void encodeMptAmountWithMoreThan63BitAmountThrowsNegative() {
    UnsignedLong maxLongPlusOne = UnsignedLong.valueOf(Long.MAX_VALUE).plus(UnsignedLong.ONE);
    String json = "{\"value\":\"-" + maxLongPlusOne + "\"," +
      "\"mpt_issuance_id\":\"00002403C84A0A28E0190E208E982C352BBD5006600555CF\"}";
    assertThatThrownBy(() -> codec.fromJson(json)).isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid MPT amount. Maximum MPT value is (2^63 - 1)");
  }
}
