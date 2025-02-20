package org.xrpl.xrpl4j.model.transactions.json;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutablePaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class PaymentChannelFundJsonTests extends AbstractTransactionJsonTest<
  ImmutablePaymentChannelFund, ImmutablePaymentChannelFund.Builder, PaymentChannelFund
  > {

  /**
   * No-args Constructor.
   */
  protected PaymentChannelFundJsonTests() {
    super(PaymentChannelFund.class, ImmutablePaymentChannelFund.class, TransactionType.PAYMENT_CHANNEL_FUND);
  }

  @Override
  protected ImmutablePaymentChannelFund.Builder builder() {
    return ImmutablePaymentChannelFund.builder();
  }

  @Override
  protected PaymentChannelFund fullyPopulatedTransaction() {
    return PaymentChannelFund.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .amount(XrpCurrencyAmount.ofDrops(200000))
      .expiration(UnsignedLong.valueOf(543171558))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected PaymentChannelFund fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected PaymentChannelFund minimallyPopulatedTransaction() {
    return PaymentChannelFund.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .amount(XrpCurrencyAmount.ofDrops(200000))
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testPaymentChannelFundJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Sequence\": 1,\n" +
        "  \"TransactionType\": \"PaymentChannelFund\",\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"Amount\": \"200000\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"Expiration\": 543171558\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testPaymentChannelFundJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    PaymentChannelFund fund = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Amount\": \"200000\",\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"Expiration\": 543171558,\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Flags\": 0,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"Sequence\": 1,\n" +
        "  \"TransactionType\": \"PaymentChannelFund\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fund, json);
  }

  @Test
  public void testPaymentChannelFundJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    PaymentChannelFund fund = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Amount\": \"200000\",\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"Expiration\": 543171558,\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"Sequence\": 1,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"TransactionType\": \"PaymentChannelFund\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fund, json);
  }

  @Test
  public void testPaymentChannelFundJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    PaymentChannelFund fund = PaymentChannelFund.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .amount(XrpCurrencyAmount.ofDrops(200000))
      .expiration(UnsignedLong.valueOf(543171558))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Sequence\": 1,\n" +
        "  \"TransactionType\": \"PaymentChannelFund\",\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"Amount\": \"200000\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"Expiration\": 543171558\n" +
        "}";

    assertCanSerializeAndDeserialize(fund, json);
  }
}
