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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedLong;
import org.xrpl.xrpl4j.codec.addresses.ByteUtils;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByte;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.BinaryCodecObjectMapperFactory;
import org.xrpl.xrpl4j.codec.binary.math.MathUtils;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Codec for XRPL Amount type.
 */
class AmountType extends SerializedType<AmountType> {

  public static final BigDecimal MAX_DROPS = new BigDecimal("1e17");
  public static final BigDecimal MIN_DROPS = new BigDecimal("-1e17");
  public static final BigDecimal MIN_XRP = new BigDecimal("1e-6");
  public static final BigDecimal MAX_NEGATIVE_XRP = new BigDecimal("-1e-6");

  public static final String DEFAULT_AMOUNT_HEX = "4000000000000000";
  public static final String ZERO_CURRENCY_AMOUNT_HEX = "8000000000000000";
  public static final int NATIVE_AMOUNT_BYTE_LENGTH = 8;
  public static final int CURRENCY_AMOUNT_BYTE_LENGTH = 48;
  public static final int MPT_AMOUNT_BYTE_LENGTH = 33;
  private static final int MAX_IOU_PRECISION = 16;

  /**
   * According to <a href=https://xrpl.org/currency-formats.html#currency-formats>xrpl.org</a>, the minimum token value
   * exponent is -96. However, because the value field is converted from a {@link String} to a {@link BigDecimal} when
   * encoding/decoding, and because {@link BigDecimal} defaults to using single digit number, the minimum exponent in
   * this context is -96 + 15, as XRPL amounts have a precision of 15 digits.
   */
  private static final int MIN_IOU_EXPONENT = -81;

  /**
   * According to <a href=https://xrpl.org/currency-formats.html#currency-formats>xrpl.org</a>, the maximum token value
   * exponent is 80. However, because the value field is converted from a {@link String} to a {@link BigDecimal} when
   * encoding/decoding, and because {@link BigDecimal} defaults to using single digit number, the maximum exponent in
   * this context is 80 + 15, as XRPL amounts have a precision of 15 digits.
   */
  private static final int MAX_IOU_EXPONENT = 95;

  private static final ObjectMapper objectMapper = BinaryCodecObjectMapperFactory.getObjectMapper();

  public AmountType() {
    this(UnsignedByteArray.fromHex(DEFAULT_AMOUNT_HEX));
  }

  public AmountType(UnsignedByteArray byteList) {
    super(byteList);
  }

  /**
   * Validate XRP amount.
   *
   * @param amount String representing XRP amount
   *
   * @throws IllegalArgumentException if invalid amount
   */
  private static void assertXrpIsValid(String amount) {
    if (amount.contains(".")) {
      throw new IllegalArgumentException(amount + " is an illegal amount");
    }
    BigDecimal value = new BigDecimal(amount);
    if (!value.equals(BigDecimal.ZERO)) {
      final FluentCompareTo<BigDecimal> fluentValue = FluentCompareTo.is(value);
      if (value.signum() < 0) { // `value` is negative
        if (fluentValue.greaterThan(MAX_NEGATIVE_XRP) || fluentValue.lessThan(MIN_DROPS)) {
          throw new IllegalArgumentException(String.format("%s is an illegal amount", amount));
        }
      } else { // `value` is positive
        if (fluentValue.lessThan(MIN_XRP) || fluentValue.greaterThan(MAX_DROPS)) {
          throw new IllegalArgumentException(String.format("%s is an illegal amount", amount));
        }
      }
    }
  }

  /**
   * Validate IOU.value amount
   *
   * @param decimal Decimal.js object representing IOU.value
   *
   * @throws IllegalArgumentException if invalid amount
   */
  private static void assertIouIsValid(BigDecimal decimal) {
    if (!decimal.equals(BigDecimal.ZERO)) {
      int precision = decimal.precision();
      int exponent = MathUtils.getExponent(decimal);
      if (precision > MAX_IOU_PRECISION ||
        exponent > MAX_IOU_EXPONENT ||
        exponent < MIN_IOU_EXPONENT
      ) {
        throw new Error("Decimal precision out of range");
      }
      verifyNoDecimal(decimal);
    }
  }

  /**
   * Ensure that the value after being multiplied by the exponent does not contain a decimal.
   *
   * @param decimal a Decimal object
   *
   * @throws IllegalArgumentException if invalid amount
   */
  private static void verifyNoDecimal(BigDecimal decimal) {
    BigDecimal exponent = new BigDecimal("1e" + -(MathUtils.getExponent(decimal) - 15));
    String integerNumberString = decimal.multiply(exponent).toPlainString();
    if (integerNumberString.indexOf(".") > 0) {
      throw new Error("Decimal place found in integerNumberString");
    }
  }

  @Override
  public AmountType fromParser(BinaryParser parser) {
    UnsignedByte nextByte = parser.peek();
    int numBytes;
    if (nextByte.isNthBitSet(1)) {
      numBytes = CURRENCY_AMOUNT_BYTE_LENGTH;
    } else {
      boolean isMpt = nextByte.isNthBitSet(3);

      numBytes = isMpt ? MPT_AMOUNT_BYTE_LENGTH : NATIVE_AMOUNT_BYTE_LENGTH;
    }

    return new AmountType(parser.read(numBytes));
  }

  @Override
  public AmountType fromJson(JsonNode value) throws JsonProcessingException {
    if (value.isValueNode()) {
      // XRP Amount
      assertXrpIsValid(value.asText());

      final boolean isValueNegative = value.asText().startsWith("-");
      final UnsignedByteArray number = UnsignedByteArray.fromHex(
        ByteUtils.padded(
          UnsignedLong
            .valueOf(isValueNegative ? value.asText().substring(1) : value.asText())
            .toString(16),
          16 // <-- 64 / 4
        )
      );
      final byte[] rawBytes = number.toByteArray();
      if (!isValueNegative) {
        rawBytes[0] |= 0x40;
      }
      return new AmountType(UnsignedByteArray.of(rawBytes));
    } else if (!value.has("mpt_issuance_id")) {
      // IOU Amount
      Amount amount = objectMapper.treeToValue(value, Amount.class);
      BigDecimal number = new BigDecimal(amount.value());

      UnsignedByteArray result = number.unscaledValue().equals(BigInteger.ZERO) ?
        UnsignedByteArray.fromHex(ZERO_CURRENCY_AMOUNT_HEX) :
        getAmountBytes(number);

      UnsignedByteArray currency = new CurrencyType().fromJson(value.get("currency")).value();
      UnsignedByteArray issuer = new AccountIdType().fromJson(value.get("issuer")).value();

      result.append(currency);
      result.append(issuer);

      return new AmountType(result);
    } else {
      // MPT Amount
      MptAmount amount = objectMapper.treeToValue(value, MptAmount.class);

      if (FluentCompareTo.is(amount.unsignedLongValue()).greaterThan(UnsignedLong.valueOf(Long.MAX_VALUE))) {
        throw new IllegalArgumentException("Invalid MPT amount. Maximum MPT value is (2^63 - 1)");
      }

      UnsignedByteArray amountBytes =  UnsignedByteArray.fromHex(
        ByteUtils.padded(
          amount.unsignedLongValue().toString(16),
          16 // <-- 64 / 4
        )
      );
      UnsignedByteArray issuanceIdBytes = new UInt192Type().fromJson(new TextNode(amount.mptIssuanceId())).value();

      // MPT Amounts always have 0110000 as its first byte.
      int leadingByte = amount.isNegative() ? 0x20 : 0x60;
      UnsignedByteArray result = UnsignedByteArray.of(UnsignedByte.of(leadingByte));
      result.append(amountBytes);
      result.append(issuanceIdBytes);

      return new AmountType(result);
    }
  }

  private UnsignedByteArray getAmountBytes(BigDecimal number) {
    BigInteger paddedNumber = MathUtils.toPaddedBigInteger(number, 16);
    byte[] amountBytes = ByteUtils.toByteArray(paddedNumber, 8);
    amountBytes[0] |= (byte) 0x80;
    if (number.compareTo(BigDecimal.ZERO) > 0) {
      amountBytes[0] |= 0x40;
    }

    int exponent = MathUtils.getExponent(number);
    if (exponent > MAX_IOU_EXPONENT || exponent < MIN_IOU_EXPONENT) {
      throw new IllegalArgumentException("exponent out of range");
    }
    UnsignedByte exponentByte = UnsignedByte.of(97 + exponent - 15);
    amountBytes[0] |= (byte) (exponentByte.asInt() >>> 2);
    amountBytes[1] |= (byte) ((exponentByte.asInt() & 0x03) << 6);

    return UnsignedByteArray.of(amountBytes);
  }

  @Override
  public JsonNode toJson() {
    if (this.isNative()) {
      byte[] rawBytes = toBytes();
      rawBytes[0] &= 0x3f;
      BigInteger value = new BigInteger(rawBytes);
      if (!this.isPositive()) {
        value = value.negate();
      }
      return new TextNode(value.toString());
    } else if (this.isMpt()) {
      BinaryParser parser = new BinaryParser(this.toHex());
      // We know the first byte already based on this.isMpt()
      UnsignedByte leadingByte = parser.read(1).get(0);
      boolean isNegative = !leadingByte.isNthBitSet(2);
      UnsignedLong amount = parser.readUInt64();
      UnsignedByteArray issuanceId = new UInt192Type().fromParser(parser).value();

      String amountBase10 = amount.toString(10);
      MptAmount mptAmount = MptAmount.builder()
        .value(isNegative ? "-" + amountBase10 : amountBase10)
        .mptIssuanceId(issuanceId.hexValue())
        .build();

      return objectMapper.valueToTree(mptAmount);
    } else {
      // Must be IOU if it's not XRP or MPT
      BinaryParser parser = new BinaryParser(this.toHex());
      UnsignedByteArray mantissa = parser.read(8);
      final SerializedType<?> currency = new CurrencyType().fromParser(parser);
      final SerializedType<?> issuer = new AccountIdType().fromParser(parser);

      UnsignedByte b1 = mantissa.get(0);
      UnsignedByte b2 = mantissa.get(1);

      boolean isPositive = b1.isNthBitSet(2);
      String sign = isPositive ? "" : "-";

      int exponent = ((b1.asInt() & 0x3f) << 2) + ((b2.asInt() & 0xff) >> 6) - 97;
      mantissa.set(0, UnsignedByte.of(0));
      mantissa.set(1, UnsignedByte.of(b2.asInt() & 0x3f));

      BigDecimal value = new BigDecimal(new BigInteger(sign + mantissa.hexValue(), 16))
        .multiply(new BigDecimal("1e" + exponent))
        .stripTrailingZeros();

      assertIouIsValid(value);

      Amount amount = Amount.builder()
        .currency(currency.toJson().asText())
        .issuer(issuer.toJson().asText())
        .value(value.toPlainString())
        .build();

      return objectMapper.valueToTree(amount);
    }
  }

  /**
   * Returns true if this amount is a "native" XRP amount.
   *
   * @return {@code true} if this AmountType is native; {@code false} otherwise.
   */
  public boolean isNative() {
    // 1st bit in 1st byte is set to 0 for native XRP, 3rd bit is also 0.
    byte leadingByte = toBytes()[0];
    return (leadingByte & 0x80) == 0 && (leadingByte & 0x20) == 0;
  }

  public boolean isMpt() {
    // 1st bit in 1st byte is 0, and 3rd bit is 1
    byte leadingByte = toBytes()[0];
    return (leadingByte & 0x80) == 0 && (leadingByte & 0x20) != 0;
  }

  /**
   * Determines if this AmountType is positive.
   *
   * @return {@code true} if this AmountType is positive; {@code false} otherwise.
   */
  public boolean isPositive() {
    // 2nd bit in 1st byte is set to 1 for positive amounts
    return (toBytes()[0] & 0x40) > 0;
  }

}
