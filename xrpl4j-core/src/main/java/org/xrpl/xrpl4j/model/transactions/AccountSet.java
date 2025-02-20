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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.AccountSetTransactionFlags;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * An {@link AccountSet} transaction modifies the properties of an account in the XRP Ledger.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAccountSet.class)
@JsonDeserialize(as = ImmutableAccountSet.class)
public interface AccountSet extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableAccountSet.Builder}.
   */
  static ImmutableAccountSet.Builder builder() {
    return ImmutableAccountSet.builder();
  }

  /**
   * Set of {@link AccountSetTransactionFlags}s for this {@link AccountSet}. Defaults to
   * {@link AccountSetTransactionFlags#empty()}.
   *
   * @return An {@link AccountSetTransactionFlags}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default AccountSetTransactionFlags flags() {
    return AccountSetTransactionFlags.empty();
  }

  /**
   * Unique identifier of a flag to disable for this account.
   *
   * <p>If this field is empty, developers should check if {@link #clearFlagRawValue()} is also empty. If
   * {@link #clearFlagRawValue()} is present, it means that the {@code ClearFlag} field of the transaction was not a
   * valid {@link AccountSetFlag} but was still present in a validated transaction on ledger.</p>
   *
   * <p>Because the preferred way of setting account flags is with {@link AccountSetFlag}s, this field should
   * not be set in conjunction with the {@link AccountSet#flags()} field.
   *
   * @return An {@link Optional} of type {@link AccountSetFlag} representing the flag to disable on this account.
   */
  @JsonIgnore
  Optional<AccountSetFlag> clearFlag();

  /**
   * A flag to disable for this account, as an {@link UnsignedInteger}.
   *
   * <p>Developers should prefer setting {@link #clearFlag()} and leaving this field empty when constructing
   * a new {@link AccountSet}.  This field is used to serialize and deserialize the {@code "ClearFlag"} field in JSON,
   * as some {@link AccountSet} transactions on the XRPL set the "ClearFlag" field to a number that is not recognized as
   * an asf flag by rippled. Without this field, xrpl4j would fail to deserialize those transactions, as
   * {@link AccountSetFlag} does not support arbitrary integer values.</p>
   *
   * <p>Additionally, using this field as the source of truth for JSON serialization/deserialization rather than
   * {@link #clearFlag()} allows developers to recompute the hash of a transaction that was deserialized from a rippled
   * RPC/WS result accurately. An alternative to this field would be to add an enum variant to {@link AccountSetFlag}
   * for unknown values, but binary serializing an {@link AccountSet} that was constructed by deserializing JSON would
   * result in a different binary blob than what exists on ledger.</p>
   *
   * @return An {@link Optional} {@link UnsignedInteger}.
   */
  @JsonProperty("ClearFlag")
  Optional<UnsignedInteger> clearFlagRawValue();

  /**
   * Normalization method to try to get {@link #clearFlag()}and {@link #clearFlagRawValue()} to match.
   *
   * <p>If neither field is present, there is nothing to do.</p>
   * <p>If both fields are present, there is nothing to do, but we will check that {@link #clearFlag()}'s
   * underlying value equals {@link #clearFlagRawValue()}.</p>
   * <p>If {@link #clearFlag()} is present but {@link #clearFlagRawValue()} is empty, we set
   * {@link #clearFlagRawValue()} to the underlying value of {@link #clearFlag()}.</p>
   * <p>If {@link #clearFlag()} is empty and {@link #clearFlagRawValue()} is present, we will set
   * {@link #clearFlag()} to the {@link AccountSetFlag} variant associated with {@link #clearFlagRawValue()}, or leave
   * {@link #clearFlag()} empty if {@link #clearFlagRawValue()} does not map to an {@link AccountSetFlag}.</p>
   *
   * @return A normalized {@link AccountSet}.
   */
  @Value.Check
  default AccountSet normalizeClearFlag() {
    if (!clearFlag().isPresent() && !clearFlagRawValue().isPresent()) {
      // If both are empty, nothing to do.
      return this;
    } else if (clearFlag().isPresent() && clearFlagRawValue().isPresent()) {
      // Both will be present if:
      //  1. A developer set them both manually (in the builder)
      //  2. This normalize method has already been called.

      // We should still check that the clearFlagRawValue matches the inner value of AccountSetFlag.
      Preconditions.checkState(
        clearFlag().get().getValue() == clearFlagRawValue().get().longValue(),
        String.format("clearFlag and clearFlagRawValue should be equivalent, but clearFlag's underlying " +
            "value was %s and clearFlagRawValue was %s",
          clearFlag().get().getValue(),
          clearFlagRawValue().get().longValue()
        )
      );
      return this;
    } else if (clearFlag().isPresent() && !clearFlagRawValue().isPresent()) {
      // This can only happen if the developer only set clearFlag(). In this case, we need to set clearFlagRawValue to
      // match clearFlag.
      return AccountSet.builder().from(this)
        .clearFlagRawValue(UnsignedInteger.valueOf(clearFlag().get().getValue()))
        .build();
    } else { // clearFlag not present and clearFlagRawValue is present
      // This can happen if:
      //   1. A developer sets clearFlagRawValue manually in the builder
      //   2. JSON has ClearFlag and jackson sets clearFlagRawValue.
      // This value will never be negative due to XRPL representing this kind of flag as an unsigned number,
      // so no lower bound check is required.
      if (clearFlagRawValue().get().longValue() <= AccountSetFlag.MAX_VALUE) {
        // Set clearFlag to clearFlagRawValue if clearFlagRawValue matches a valid AccountSetFlag variant.
        return AccountSet.builder().from(this)
          .clearFlag(AccountSetFlag.forValue(clearFlagRawValue().get().intValue()))
          .build();
      } else {
        // Otherwise, leave clearFlag empty.
        return this;
      }
    }
  }

  /**
   * Unique identifier of a flag to enable for this account.
   *
   * <p>If this field is empty, developers should check if {@link #setFlagRawValue()} is also empty. If
   * {@link #setFlagRawValue()} is present, it means that the {@code ClearFlag} field of the transaction was not a
   * valid {@link AccountSetFlag} but was still present in a validated transaction on ledger.</p>
   *
   * <p>Because the preferred way of setting account flags is with {@link AccountSetFlag}s, this field should not be
   * set in conjunction with the {@link AccountSet#flags()} field.
   *
   * @return An {@link Optional} of type {@link AccountSetFlag} representing the flag to enable on this account.
   */
  @JsonIgnore
  Optional<AccountSetFlag> setFlag();

  /**
   * A flag to disable for this account, as an {@link UnsignedInteger}.
   *
   * <p>Developers should prefer setting {@link #setFlag()} and leaving this field empty when constructing
   * a new {@link AccountSet}.  This field is used to serialize and deserialize the {@code "ClearFlag"} field in JSON,
   * as some {@link AccountSet} transactions on the XRPL set the "ClearFlag" field to a number that is not recognized as
   * an asf flag by rippled. Without this field, xrpl4j would fail to deserialize those transactions, as
   * {@link AccountSetFlag} does not support arbitrary integer values.</p>
   *
   * <p>Additionally, using this field as the source of truth for JSON serialization/deserialization rather than
   * {@link #setFlag()} allows developers to recompute the hash of a transaction that was deserialized from a rippled
   * RPC/WS result accurately. An alternative to this field would be to add an enum variant to {@link AccountSetFlag}
   * for unknown values, but binary serializing an {@link AccountSet} that was constructed by deserializing JSON would
   * result in a different binary blob than what exists on ledger.</p>
   *
   * @return An {@link Optional} {@link UnsignedInteger}
   */
  @JsonProperty("SetFlag")
  Optional<UnsignedInteger> setFlagRawValue();

  /**
   * Normalization method to try to get {@link #setFlag()}and {@link #setFlagRawValue()} to match.
   *
   * <p>If neither field is present, there is nothing to do.</p>
   * <p>If both fields are present, there is nothing to do, but we will check that {@link #setFlag()}'s
   * underlying value equals {@link #setFlagRawValue()}.</p>
   * <p>If {@link #setFlag()} is present but {@link #setFlagRawValue()} is empty, we set
   * {@link #setFlagRawValue()} to the underlying value of {@link #setFlag()}.</p>
   * <p>If {@link #setFlag()} is empty and {@link #setFlagRawValue()} is present, we will set
   * {@link #setFlag()} to the {@link AccountSetFlag} variant associated with {@link #setFlagRawValue()}, or leave
   * {@link #setFlag()} empty if {@link #setFlagRawValue()} does not map to an {@link AccountSetFlag}.</p>
   *
   * @return A normalized {@link AccountSet}.
   */
  @Value.Check
  default AccountSet normalizeSetFlag() {
    if (!setFlag().isPresent() && !setFlagRawValue().isPresent()) {
      // If both are empty, nothing to do.
      return this;
    } else if (setFlag().isPresent() && setFlagRawValue().isPresent()) {
      // Both will be present if:
      //  1. A developer set them both manually (in the builder)
      //  2. This normalize method has already been called.

      // We should still check that the setFlagRawValue matches the inner value of AccountSetFlag.
      Preconditions.checkState(
        setFlag().get().getValue() == setFlagRawValue().get().longValue(),
        String.format("setFlag and setFlagRawValue should be equivalent, but setFlag's underlying " +
            "value was %s and setFlagRawValue was %s",
          setFlag().get().getValue(),
          setFlagRawValue().get().longValue()
        )
      );
      return this;
    } else if (setFlag().isPresent() && !setFlagRawValue().isPresent()) {
      // This can only happen if the developer only set setFlag(). In this case, we need to set setFlagRawValue to
      // match setFlag.
      return AccountSet.builder().from(this)
        .setFlagRawValue(UnsignedInteger.valueOf(setFlag().get().getValue()))
        .build();
    } else { // setFlag is empty and setFlagRawValue is present
      // This can happen if:
      //   1. A developer sets setFlagRawValue manually in the builder
      //   2. JSON has ClearFlag and jackson sets setFlagRawValue.
      // This value will never be negative due to XRPL representing this kind of flag as an unsigned number,
      // so no lower bound check is required.
      if (setFlagRawValue().get().longValue() <= AccountSetFlag.MAX_VALUE) {
        // Set setFlag to setFlagRawValue if setFlagRawValue matches a valid AccountSetFlag variant.
        return AccountSet.builder().from(this)
          .setFlag(AccountSetFlag.forValue(setFlagRawValue().get().intValue()))
          .build();
      } else {
        // Otherwise, leave setFlag empty.
        return this;
      }
    }
  }

  /**
   * The hex string of the lowercase ASCII of the domain for the account. For example, the domain example.com would be
   * represented as "6578616D706C652E636F6D".
   *
   * <p>To remove the Domain field from an account, send an {@link AccountSet} with the {@link AccountSet#domain()}
   * set to an empty string.
   *
   * @return An {@link Optional} of type {@link String} containing the domain.
   */
  @JsonProperty("Domain")
  @JsonInclude(Include.NON_ABSENT)
  Optional<String> domain();

  /**
   * Hash of an email address to be used for generating an avatar image. Conventionally, clients use <a
   * href="http://en.gravatar.com/site/implement/hash/">Gravatar</a> to display this image.
   *
   * @return An {@link Optional} of type {@link String} containing the hash of the email.
   */
  @JsonProperty("EmailHash")
  Optional<String> emailHash();

  /**
   * Hexadecimal encoded public key for sending encrypted messages to this account.
   *
   * @return An {@link Optional} of type {@link String} containing the messaging public key.
   */
  @JsonProperty("MessageKey")
  @JsonInclude(Include.NON_ABSENT)
  Optional<String> messageKey();

  /**
   * The fee to charge when users transfer this account's issued currencies, represented as billionths of a unit. Cannot
   * be more than 2000000000 or less than 1000000000, except for the special case 0 meaning no fee.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the transfer rate.
   */
  @JsonProperty("TransferRate")
  Optional<UnsignedInteger> transferRate();

  /**
   * Tick size to use for offers involving a currency issued by this address. The exchange rates of those offers is
   * rounded to this many significant digits. Valid values are 3 to 15 inclusive, or 0 to disable.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the tick size.
   */
  @JsonProperty("TickSize")
  Optional<UnsignedInteger> tickSize();

  /**
   * Sets an alternate account that is allowed to mint NFTokens on this account's behalf using NFTokenMint's `Issuer`
   * field.
   *
   * @return An {@link Optional} field of type {@link Address}.
   */
  @JsonProperty("NFTokenMinter")
  Optional<Address> mintAccount();

  /**
   * An arbitrary 256-bit value. If specified, the value is stored as part of the account but has no inherent meaning
   * or requirements.
   *
   * @return The 256-bit value as a hex encoded {@link String}.
   */
  @JsonProperty("WalletLocator")
  Optional<String> walletLocator();

  /**
   * Not used. This field is valid in AccountSet transactions but does nothing.
   *
   * @return An optionally present {@link UnsignedInteger}.
   */
  @JsonProperty("WalletSize")
  Optional<UnsignedInteger> walletSize();

  /**
   * Check email hash length.
   */
  @Value.Check
  default void checkEmailHashLength() {
    emailHash()
      .ifPresent(hash ->
        Preconditions.checkArgument(
          hash.length() == 32,
          String.format("emailHash must be 32 characters (128 bits), but was %s characters long.", hash.length())
        )
      );
  }

  /**
   * Check transfer rate.
   */
  @Value.Check
  default void checkTransferRate() {
    transferRate()
      .ifPresent(rate ->
        Preconditions.checkArgument(rate.equals(UnsignedInteger.ZERO) ||
            (rate.compareTo(UnsignedInteger.valueOf(1000000000L)) >= 0 &&
              rate.compareTo(UnsignedInteger.valueOf(2000000000L)) <= 0),
          "transferRate must be between 1,000,000,000 and 2,000,000,000 or equal to 0."
        )
      );
  }

  /**
   * Check tick size.
   */
  @Value.Check
  default void checkTickSize() {
    tickSize()
      .ifPresent(tickSize ->
        Preconditions.checkArgument(tickSize.equals(UnsignedInteger.ZERO) ||
            (tickSize.compareTo(UnsignedInteger.valueOf(3)) >= 0 &&
              tickSize.compareTo(UnsignedInteger.valueOf(15)) <= 0),
          "tickSize must be between 3 and 15 inclusive or be equal to 0."
        )
      );

  }

  /**
   * There are several options which can be either enabled or disabled for an account. Account options are represented
   * by different types of flags depending on the situation.
   * <ul>
   *   <li>
   *     The AccountSet transaction type has several "AccountSet Flags" (prefixed asf) that can enable an option when
   *     passed as the SetFlag parameter, or disable an option when passed as the ClearFlag parameter.
   *   </li>
   *   <li>
   *     The AccountSet transaction type has several transaction flags (prefixed tf) that can be used to enable or
   *     disable specific account options when passed in the Flags parameter. This style is discouraged.
   *     New account options do not have corresponding transaction (tf) flags.
   *   </li>
   *   <li>
   *     The AccountRoot ledger object type has several ledger-state-flags (prefixed lsf) which represent the state of
   *     particular account options within a particular ledger. These settings apply until a transaction changes them.
   *   </li>
   * </ul>
   */
  enum AccountSetFlag {
    /**
     * This flag will do nothing but exists to accurately deserialize AccountSet transactions whose {@code SetFlag} or
     * {@code ClearFlag} fields are zero.
     */
    NONE(0),
    /**
     * Require a destination tag to send transactions to this account.
     */
    REQUIRE_DEST(1),
    /**
     * Require authorization for users to hold balances issued by this address. Can only be enabled if the address has
     * no trust lines connected to it.
     */
    REQUIRE_AUTH(2),
    /**
     * XRP should not be sent to this account. (Enforced by client applications, not by rippled).
     */
    DISALLOW_XRP(3),
    /**
     * Disallow use of the master key pair. Can only be enabled if the account has configured another way to sign
     * transactions, such as a Regular Key or a Signer List.
     */
    DISABLE_MASTER(4),
    /**
     * Track the ID of this account's most recent transaction. Required for {@link Transaction#accountTransactionId()}.
     */
    ACCOUNT_TXN_ID(5),
    /**
     * Permanently give up the ability to freeze individual trust lines or disable Global Freeze. This flag can never be
     * disabled after being enabled.
     *
     * @see "https://xrpl.org/freezes.html"
     */
    NO_FREEZE(6),
    /**
     * Freeze all assets issued by this account.
     *
     * @see "https://xrpl.org/freezes.html"
     */
    GLOBAL_FREEZE(7),
    /**
     * Enable rippling on this account's trust lines by default.
     *
     * @see "https://xrpl.org/rippling.html"
     */
    DEFAULT_RIPPLE(8),
    /**
     * Enable Deposit Authorization on this account.
     *
     * @see "https://xrpl.org/depositauth.html"
     */
    DEPOSIT_AUTH(9),
    /**
     * Allow another account to mint and burn tokens on behalf of this account.
     */
    AUTHORIZED_MINTER(10),
    /**
     * Block incoming NFTokenOffers.
     */
    DISALLOW_INCOMING_NFT_OFFER(12),
    /**
     * Block incoming Checks.
     */
    DISALLOW_INCOMING_CHECK(13),
    /**
     * Block incoming Payment Channels.
     */
    DISALLOW_INCOMING_PAY_CHAN(14),
    /**
     * Block incoming Trustlines.
     */
    DISALLOW_INCOMING_TRUSTLINE(15),
    /**
     * Enable clawback on the account's trustlines.
     *
     * <p>This value will be marked {@link Beta} until the Clawback amendment is enabled on mainnet. Its API is subject
     * to change.</p>
     */
    @Beta
    ALLOW_TRUSTLINE_CLAWBACK(16);

    final int value;

    AccountSetFlag(int value) {
      this.value = value;
    }

    /**
     * The maximum underlying value of AccountSetFlags. This is useful for the normalization methods of AccountSet
     * so that adding a new AccountSetFlag does not require a change to those normalization functions.
     */
    static final int MAX_VALUE = Collections.max(Arrays.asList(AccountSetFlag.values())).getValue();

    /**
     * To deserialize enums with integer values, you need to specify this factory method with the {@link JsonCreator}
     * annotation, otherwise Jackson treats the JSON integer value as an ordinal.
     *
     * @param value The int value of the flag.
     *
     * @return The {@link AccountSetFlag} for the given integer value.
     *
     * @see "https://github.com/FasterXML/jackson-databind/issues/1850"
     */
    @JsonCreator
    public static AccountSetFlag forValue(int value) {
      for (AccountSetFlag flag : AccountSetFlag.values()) {
        if (flag.value == value) {
          return flag;
        }
      }

      throw new IllegalArgumentException("No matching AccountSetFlag enum value for int value " + value);
    }

    /**
     * Get the underlying value of this {@link AccountSetFlag}.
     *
     * @return The underlying {@code int} value of this {@link AccountSetFlag}.
     */
    @JsonValue
    public int getValue() {
      return value;
    }
  }

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default AccountSet normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.ACCOUNT_SET);
    return this;
  }
}
