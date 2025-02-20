package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: model
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;
import org.xrpl.xrpl4j.model.immutables.Wrapped;
import org.xrpl.xrpl4j.model.immutables.Wrapper;
import org.xrpl.xrpl4j.model.jackson.modules.AddressDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.AddressSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.AssetPriceDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.AssetPriceSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.DidDataDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.DidDataSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.DidDocumentDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.DidDocumentSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.DidUriDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.DidUriSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.Hash256Deserializer;
import org.xrpl.xrpl4j.model.jackson.modules.Hash256Serializer;
import org.xrpl.xrpl4j.model.jackson.modules.MarkerDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.MarkerSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.NetworkIdDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.NetworkIdSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.NfTokenIdDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.NfTokenIdSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.NfTokenUriSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.OracleDocumentIdDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.OracleDocumentIdSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.OracleProviderDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.OracleUriDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.TradingFeeDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.TradingFeeSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.TransferFeeDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.TransferFeeSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.VoteWeightDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.VoteWeightSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.XChainClaimIdDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.XChainClaimIdSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.XChainCountDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.XChainCountSerializer;
import org.xrpl.xrpl4j.model.jackson.modules.XrpCurrencyAmountDeserializer;
import org.xrpl.xrpl4j.model.jackson.modules.XrpCurrencyAmountSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Wrapped immutable classes for providing type-safe objects.
 */
@SuppressWarnings("TypeName")
public class Wrappers {

  /**
   * A wrapped {@link String} representing an address on the XRPL.
   */
  @Value.Immutable(builder = true) // This is the default, but it's omitted without this.
  @Wrapped
  @JsonSerialize(as = Address.class, using = AddressSerializer.class)
  @JsonDeserialize(as = Address.class, using = AddressDeserializer.class)
  abstract static class _Address extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

    /**
     * Validates that a {@link Address}'s value's length is equal to 34 characters and starts with `r`.
     */
    @Value.Check
    public void validateAddress() {
      Preconditions.checkArgument(this.value().startsWith("r"), "Invalid Address: Bad Prefix");
      Preconditions.checkArgument(this.value().length() >= 25 && this.value().length() <= 35,
        "Classic Addresses must be (25,35) characters long inclusive.");
    }

  }

  /**
   * A wrapped {@link String} representing an X-Address on the XRPL.
   */
  @Value.Immutable(builder = true) // This is the default, but it's omitted without this.
  @Wrapped
  @JsonSerialize(as = XAddress.class)
  @JsonDeserialize(as = XAddress.class)
  abstract static class _XAddress extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link String} containing the Hex representation of a 256-bit Hash.
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = Hash256.class, using = Hash256Serializer.class)
  @JsonDeserialize(as = Hash256.class, using = Hash256Deserializer.class)
  abstract static class _Hash256 extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

    /**
     * Validates that a {@link Hash256}'s value's length is equal to 64 characters.
     */
    @Value.Check
    public void validateLength() {
      Preconditions.checkArgument(this.value().length() == 64, "Hash256 Strings must be 64 characters long.");
    }

    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof Hash256) {
        String otherValue = ((Hash256) obj).value();
        if (otherValue != null) {
          return otherValue.toUpperCase(Locale.ENGLISH).equals(value().toUpperCase(Locale.ENGLISH));
        }
      }
      return false;
    }

    @Override
    public int hashCode() {
      return value().toUpperCase(Locale.ENGLISH).hashCode();
    }
  }

  /**
   * A {@link CurrencyAmount} for the XRP currency (non-issued). {@link XrpCurrencyAmount}s are a {@link String}
   * representation of an unsigned integer representing the amount in XRP drops.
   */
  @Value.Immutable(builder = true) // This is the default, but it's omitted without this.
  @Wrapped
  @JsonSerialize(as = XrpCurrencyAmount.class, using = XrpCurrencyAmountSerializer.class)
  @JsonDeserialize(as = XrpCurrencyAmount.class, using = XrpCurrencyAmountDeserializer.class)
  abstract static class _XrpCurrencyAmount extends Wrapper<UnsignedLong> implements Serializable, CurrencyAmount {

    static final BigDecimal SMALLEST_XRP = new BigDecimal("0.000001");
    static final DecimalFormat FORMATTER = new DecimalFormat("###,###");

    /**
     * Constructs an {@link XrpCurrencyAmount} using a number of drops. Because XRP is capped to 100B units (1e17
     * drops), this value will never overflow Java's signed long number.
     *
     * @param drops A long representing the number of drops of XRP of this amount.
     *
     * @return An {@link XrpCurrencyAmount} of {@code drops}.
     */
    public static XrpCurrencyAmount ofDrops(final long drops) {
      if (drops < 0) {
        // Normalize the drops value to be a positive number; indicate negativity via property.
        return ofDrops(UnsignedLong.valueOf(Math.abs(drops)), true);
      } else {
        // Default to positive number and negativity indicator of `false`. No need for Math.abs(drops) because negative
        // values cannot enter this else-condition per the above if-check.
        return ofDrops(UnsignedLong.valueOf(drops), false);
      }
    }

    /**
     * Constructs an {@link XrpCurrencyAmount} using a number of drops.
     *
     * @param drops An {@link UnsignedLong} representing the number of drops of XRP of this amount.
     *
     * @return An {@link XrpCurrencyAmount} of {@code drops}.
     */
    public static XrpCurrencyAmount ofDrops(final UnsignedLong drops) {
      Objects.requireNonNull(drops);

      // Note: ofDrops() throws an exception if too big.
      return _XrpCurrencyAmount.ofDrops(drops, false);
    }

    /**
     * Constructs an {@link XrpCurrencyAmount} using a number of drops.
     *
     * @param drops      An {@link UnsignedLong} representing the number of drops of XRP of this amount.
     * @param isNegative Indicates whether this amount is positive or negative.
     *
     * @return An {@link XrpCurrencyAmount} of {@code drops}.
     */
    public static XrpCurrencyAmount ofDrops(final UnsignedLong drops, final boolean isNegative) {
      Objects.requireNonNull(drops);

      return XrpCurrencyAmount.builder()
        .value(drops)
        .isNegative(isNegative)
        .build();
    }

    /**
     * Constructs an {@link XrpCurrencyAmount} using decimal amount of XRP.
     *
     * @param amount A {@link BigDecimal} amount of XRP.
     *
     * @return An {@link XrpCurrencyAmount} of the amount of drops in {@code amount}.
     */
    public static XrpCurrencyAmount ofXrp(final BigDecimal amount) {
      Objects.requireNonNull(amount);

      if (FluentCompareTo.is(amount).equalTo(BigDecimal.ZERO)) {
        return ofDrops(UnsignedLong.ZERO);
      }

      final BigDecimal absAmount = amount.abs();
      // Whether positive or negative, ensure the amount is not too small and not too big.
      Preconditions.checkArgument(
        FluentCompareTo.is(absAmount).greaterThanEqualTo(SMALLEST_XRP),
        String.format("Amount must be greater-than-or-equal-to %s", SMALLEST_XRP)
      );
      Preconditions.checkArgument(
        FluentCompareTo.is(absAmount).lessThanOrEqualTo(MAX_XRP_BD),
        String.format("Amount must be less-than-or-equal-to %s", MAX_XRP_BD)
      );

      if (amount.signum() == 0) { // zero
        return ofDrops(UnsignedLong.ZERO); // <-- Should never happen per the first check above, but just in case.
      } else { // positive or negative
        final boolean isNegative = amount.signum() < 0;
        return ofDrops(UnsignedLong.valueOf(absAmount.scaleByPowerOfTen(6).toBigIntegerExact()), isNegative);
      }
    }

    /**
     * Indicates whether this amount is positive or negative.
     *
     * <p>Note that the use of the `@Default` annotation and the default implementation are suitable for a few
     * reasons. First, deserialization will parse the payload properly, setting this value correctly (despite this
     * default settings). Second, using a default value here will not break legacy code that is using a builder to
     * construct an {@link XrpCurrencyAmount} correctly (i.e., we assume that no developer is constructing a negative
     * XRP amount because the {@link UnsignedLong} precondition in any legacy code would not allow them to do such a
     * thing without throwing an exception). Finally, due to the way this class merely adds new static builders to
     * augment existing code, legacy code should continue to work normally.
     *
     * @return {@code true} if this amount is negative; {@code false} otherwise (i.e., if the value is 0 or positive).
     */
    @Default
    @Override
    // No `@JsonIgnore` because isNegative isn't a "serializable field" in the definitions.json file, so this won't
    // get serialized even if it's included in the JSON. Rationale for including in the generated JSON is so that
    // any software using the JSON variant of an XrpCurrencyAmount will have this information available.
    public boolean isNegative() {
      return false;
    }

    /**
     * Convert this XRP amount into a decimal representing a value denominated in whole XRP units. For example, a value
     * of `1.0` represents 1 unit of XRP; a value of `0.5` represents a half of an XRP unit.
     *
     * @return A {@link BigDecimal} representing this value denominated in whole XRP units.
     */
    public BigDecimal toXrp() {
      final BigDecimal amount = new BigDecimal(this.value().bigIntegerValue())
        .divide(BigDecimal.valueOf(ONE_XRP_IN_DROPS), MathContext.DECIMAL128);
      if (this.isNegative()) {
        return amount.negate();
      } else {
        return amount;
      }
    }

    /**
     * Adds another {@link XrpCurrencyAmount} to this amount.
     *
     * @param other An {@link XrpCurrencyAmount} to add to this.
     *
     * @return The sum of this amount and the {@code other} amount, as an {@link XrpCurrencyAmount}.
     */
    public XrpCurrencyAmount plus(XrpCurrencyAmount other) {
      // Convert each value to a long (positive or negative works)
      long result =
        (this.value().longValue() * (this.isNegative() ? -1 : 1)) +
        (other.value().longValue() * (other.isNegative() ? -1 : 1));
      return XrpCurrencyAmount.ofDrops(result);
    }

    /**
     * Subtract another {@link XrpCurrencyAmount} from this amount.
     *
     * @param other An {@link XrpCurrencyAmount} to subtract from this.
     *
     * @return The difference of this amount and the {@code other} amount, as an {@link XrpCurrencyAmount}.
     */
    public XrpCurrencyAmount minus(XrpCurrencyAmount other) {
      // Convert each value to a long (positive or negative works)
      long result =
        (this.value().longValue() * (this.isNegative() ? -1 : 1)) -
        (other.value().longValue() * (other.isNegative() ? -1 : 1));
      return XrpCurrencyAmount.ofDrops(result);
    }

    /**
     * Multiplies this amount by another {@link XrpCurrencyAmount}.
     *
     * @param other An {@link XrpCurrencyAmount} to multiply to this by.
     *
     * @return The product of this amount and the {@code other} amount, as an {@link XrpCurrencyAmount}.
     */
    public XrpCurrencyAmount times(XrpCurrencyAmount other) {
      return XrpCurrencyAmount.ofDrops(
        this.value().times(other.value()),
        this.isNegative() || other.isNegative()
      );
    }

    @Override
    public String toString() {
      return String.format("%s%s", isNegative() ? "-" : "", this.value().toString());
    }

    /**
     * Validates that this {@link XrpCurrencyAmount} does not exceed the maximum number of drops.
     */
    @Value.Check
    protected void check() {
      Preconditions.checkState(
        FluentCompareTo.is(value()).lessThanOrEqualTo(UnsignedLong.valueOf(MAX_XRP_IN_DROPS)),
        String.format(
          "XRP Amounts may not exceed %s drops (100B XRP, denominated in Drops)", FORMATTER.format(MAX_XRP_IN_DROPS)
        )
      );
    }

  }

  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = Marker.class, using = MarkerSerializer.class)
  @JsonDeserialize(as = Marker.class, using = MarkerDeserializer.class)
  abstract static class _Marker extends Wrapper<String> implements Serializable {

    @Override
    @JsonRawValue
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link String} containing the NFT Id.
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = NfTokenId.class, using = NfTokenIdSerializer.class)
  @JsonDeserialize(as = NfTokenId.class, using = NfTokenIdDeserializer.class)
  abstract static class _NfTokenId extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

    /**
     * Validates that a NfTokenId value's length is equal to 64 characters.
     */
    @Value.Check
    public void validateLength() {
      Preconditions.checkArgument(this.value().length() == 64, "TokenId must be 64 characters long.");
    }

    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof NfTokenId) {
        String otherValue = ((NfTokenId) obj).value();
        if (otherValue != null) {
          return otherValue.toUpperCase(Locale.ENGLISH).equals(value().toUpperCase(Locale.ENGLISH));
        }
      }
      return false;
    }
  }

  /**
   * A wrapped {@link String} containing the Uri.
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = NfTokenUri.class, using = NfTokenUriSerializer.class)
  @JsonDeserialize(as = NfTokenUri.class)
  abstract static class _NfTokenUri extends Wrapper<String> implements Serializable {

    /**
     * Constructs an {@link NfTokenUri} using a String value.
     *
     * @param plaintext A string value representing the Uri in plaintext.
     *
     * @return An {@link NfTokenUri} of plaintext.
     */
    public static NfTokenUri ofPlainText(String plaintext) {
      return NfTokenUri.of(BaseEncoding.base16().encode(plaintext.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public boolean equals(Object obj) {
      if (obj != null && obj instanceof NfTokenUri) {
        String otherValue = ((NfTokenUri) obj).value();
        if (otherValue != null) {
          return otherValue.toUpperCase(Locale.ENGLISH).equals(value().toUpperCase(Locale.ENGLISH));
        }
      }
      return false;
    }
  }

  /**
   * A wrapped {@link com.google.common.primitives.UnsignedInteger} containing the TransferFee.
   *
   * <p>Valid values for this field are between 0 and 50000 inclusive, allowing transfer rates of between 0.00% and
   * 50.00% in increments of 0.001. If this field is provided in a {@link NfTokenMint} transaction, the transaction MUST
   * have the {@code tfTransferable} flag enabled.
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = TransferFee.class, using = TransferFeeSerializer.class)
  @JsonDeserialize(as = TransferFee.class, using = TransferFeeDeserializer.class)
  abstract static class _TransferFee extends Wrapper<UnsignedInteger> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

    /**
     * Construct {@link TransferFee} as a percentage value.
     *
     * <p>The given percentage value must have at most 3 decimal places of precision, and must be
     * between {@code 0} and {@code 50.000}.</p>
     *
     * @param percent of type {@link BigDecimal}
     *
     * @return {@link TransferFee}
     */
    public static TransferFee ofPercent(BigDecimal percent) {
      Objects.requireNonNull(percent);
      Preconditions.checkArgument(
        Math.max(0, percent.stripTrailingZeros().scale()) <= 3,
        "Percent value should have a maximum of 3 decimal places."
      );
      return TransferFee.of(UnsignedInteger.valueOf(percent.scaleByPowerOfTen(3).toBigIntegerExact()));
    }


    /**
     * Validates that a NfTokenId value's length is equal to 64 characters.
     */
    @Value.Check
    public void validateBounds() {
      Preconditions.checkArgument(
        FluentCompareTo.is(value()).lessThanOrEqualTo(UnsignedInteger.valueOf(50000)) &&
        FluentCompareTo.is(value()).greaterThanEqualTo(UnsignedInteger.valueOf(0)),
        "TransferFee should be in the range 0 to 50000.");
    }

  }

  /**
   * A wrapped {@link com.google.common.primitives.UnsignedInteger} containing a Network ID.
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = NetworkId.class, using = NetworkIdSerializer.class)
  @JsonDeserialize(as = NetworkId.class, using = NetworkIdDeserializer.class)
  abstract static class _NetworkId extends Wrapper<UnsignedInteger> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

    /**
     * Construct a {@link NetworkId} from a {@code long}. The supplied value must be less than or equal to
     * 4,294,967,295, the largest unsigned 32-bit integer.
     *
     * @param networkId A {@code long}.
     *
     * @return A {@link NetworkId}.
     */
    public static NetworkId of(long networkId) {
      return NetworkId.of(UnsignedInteger.valueOf(networkId));
    }
  }

  /**
   * A wrapped {@link com.google.common.primitives.UnsignedInteger} containing the TransferFee.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the AMM amendment is enabled on
   * mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = TradingFee.class, using = TradingFeeSerializer.class)
  @JsonDeserialize(as = TradingFee.class, using = TradingFeeDeserializer.class)
  @Beta
  abstract static class _TradingFee extends Wrapper<UnsignedInteger> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

    /**
     * Construct {@link TradingFee} as a percentage value.
     *
     * @param percent The trading fee, as a {@link BigDecimal}.
     *
     * @return A {@link TradingFee}.
     */
    public static TradingFee ofPercent(BigDecimal percent) {
      Preconditions.checkArgument(
        Math.max(0, percent.stripTrailingZeros().scale()) <= 3,
        "Percent value should have a maximum of 3 decimal places."
      );
      return TradingFee.of(UnsignedInteger.valueOf(percent.scaleByPowerOfTen(3).toBigIntegerExact()));
    }

    /**
     * Get the {@link TradingFee} as a {@link BigDecimal}.
     *
     * @return A {@link BigDecimal}.
     */
    public BigDecimal bigDecimalValue() {
      return BigDecimal.valueOf(value().longValue(), 3);
    }

  }

  /**
   * A wrapped {@link com.google.common.primitives.UnsignedInteger} containing the VoteWeight.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the AMM amendment is enabled on
   * mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = VoteWeight.class, using = VoteWeightSerializer.class)
  @JsonDeserialize(as = VoteWeight.class, using = VoteWeightDeserializer.class)
  @Beta
  abstract static class _VoteWeight extends Wrapper<UnsignedInteger> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

    /**
     * Get the {@link VoteWeight} as a {@link BigDecimal}.
     *
     * @return A {@link BigDecimal}.
     */
    public BigDecimal bigDecimalValue() {
      return BigDecimal.valueOf(value().longValue(), 3);
    }

  }

  /**
   * A wrapped {@link com.google.common.primitives.UnsignedLong} containing an XChainClaimID.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featureXChainBridge amendment
   * is enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = XChainClaimId.class, using = XChainClaimIdSerializer.class)
  @JsonDeserialize(as = XChainClaimId.class, using = XChainClaimIdDeserializer.class)
  @Beta
  abstract static class _XChainClaimId extends Wrapper<UnsignedLong> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

  }

  /**
   * A wrapped {@link com.google.common.primitives.UnsignedLong} representing a counter for XLS-38 sidechains. This
   * wrapper mostly exists to ensure we serialize fields of this type as a hex String in JSON, as these fields are
   * STUInt64s in rippled, which are hex encoded in JSON.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featureXChainBridge amendment
   * is enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = XChainCount.class, using = XChainCountSerializer.class)
  @JsonDeserialize(as = XChainCount.class, using = XChainCountDeserializer.class)
  @Beta
  abstract static class _XChainCount extends Wrapper<UnsignedLong> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

  }

  /**
   * A wrapped {@link String} containing a DID Document.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featureDID amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = DidDocument.class, using = DidDocumentSerializer.class)
  @JsonDeserialize(as = DidDocument.class, using = DidDocumentDeserializer.class)
  @Beta
  abstract static class _DidDocument extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link String} containing a DID URI.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featureDID amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = DidUri.class, using = DidUriSerializer.class)
  @JsonDeserialize(as = DidUri.class, using = DidUriDeserializer.class)
  @Beta
  abstract static class _DidUri extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link String} containing DID Data.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featureDID amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = DidData.class, using = DidDataSerializer.class)
  @JsonDeserialize(as = DidData.class, using = DidDataDeserializer.class)
  @Beta
  abstract static class _DidData extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link UnsignedInteger} containing an Oracle document ID.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featurePriceOracle amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = OracleDocumentId.class, using = OracleDocumentIdSerializer.class)
  @JsonDeserialize(as = OracleDocumentId.class, using = OracleDocumentIdDeserializer.class)
  @Beta
  abstract static class _OracleDocumentId extends Wrapper<UnsignedInteger> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

  }

  /**
   * A wrapped {@link String} containing an Oracle provider.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featurePriceOracle amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = OracleProvider.class, using = ToStringSerializer.class)
  @JsonDeserialize(as = OracleProvider.class, using = OracleProviderDeserializer.class)
  @Beta
  abstract static class _OracleProvider extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link String} containing an Oracle URI.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featurePriceOracle amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = OracleUri.class, using = ToStringSerializer.class)
  @JsonDeserialize(as = OracleUri.class, using = OracleUriDeserializer.class)
  @Beta
  abstract static class _OracleUri extends Wrapper<String> implements Serializable {

    @Override
    public String toString() {
      return this.value();
    }

  }

  /**
   * A wrapped {@link String} containing an Oracle asset price.
   *
   * <p>This class will be marked {@link com.google.common.annotations.Beta} until the featurePriceOracle amendment is
   * enabled on mainnet. Its API is subject to change.</p>
   */
  @Value.Immutable
  @Wrapped
  @JsonSerialize(as = AssetPrice.class, using = AssetPriceSerializer.class)
  @JsonDeserialize(as = AssetPrice.class, using = AssetPriceDeserializer.class)
  @Beta
  abstract static class _AssetPrice extends Wrapper<UnsignedLong> implements Serializable {

    @Override
    public String toString() {
      return this.value().toString();
    }

  }
}
