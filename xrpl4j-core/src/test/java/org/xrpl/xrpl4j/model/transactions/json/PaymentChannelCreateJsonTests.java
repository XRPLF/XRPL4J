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
import org.xrpl.xrpl4j.model.transactions.ImmutablePaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class PaymentChannelCreateJsonTests extends AbstractTransactionJsonTest<
  ImmutablePaymentChannelCreate, ImmutablePaymentChannelCreate.Builder, PaymentChannelCreate
  > {

  /**
   * No-args Constructor.
   */
  protected PaymentChannelCreateJsonTests() {
    super(PaymentChannelCreate.class, ImmutablePaymentChannelCreate.class, TransactionType.PAYMENT_CHANNEL_CREATE);
  }

  @Override
  protected ImmutablePaymentChannelCreate.Builder builder() {
    return ImmutablePaymentChannelCreate.builder();
  }

  @Override
  protected PaymentChannelCreate fullyPopulatedTransaction() {
    return PaymentChannelCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .sourceTag(UnsignedInteger.valueOf(11747))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .settleDelay(UnsignedInteger.valueOf(86400))
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .cancelAfter(UnsignedLong.valueOf(533171558))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected PaymentChannelCreate fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected PaymentChannelCreate minimallyPopulatedTransaction() {
    return PaymentChannelCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .settleDelay(UnsignedInteger.valueOf(86400))
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testPaymentChannelCreateJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"PaymentChannelCreate\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "  \"SettleDelay\": 86400,\n" +
      "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\",\n" +
      "  \"CancelAfter\": 533171558,\n" +
      "  \"DestinationTag\": 23480,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SourceTag\": 11747\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testPaymentChannelCreateJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    PaymentChannelCreate transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"CancelAfter\": 533171558,\n" +
      "  \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "  \"DestinationTag\": 23480,\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\",\n" +
      "  \"SettleDelay\": 86400,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SourceTag\": 11747,\n" +
      "  \"TransactionType\": \"PaymentChannelCreate\"\n" +
      "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testPaymentChannelCreateJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    PaymentChannelCreate create = PaymentChannelCreate.builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"CancelAfter\": 533171558,\n" +
      "  \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "  \"DestinationTag\": 23480,\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\",\n" +
      "  \"SettleDelay\": 86400,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"SourceTag\": 11747,\n" +
      "  \"TransactionType\": \"PaymentChannelCreate\"\n" +
      "}";

    assertCanSerializeAndDeserialize(create, json);
  }

  @Test
  public void testPaymentChannelCreateJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"PaymentChannelCreate\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "  \"SettleDelay\": 86400,\n" +
      "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\",\n" +
      "  \"CancelAfter\": 533171558,\n" +
      "  \"DestinationTag\": 23480,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SourceTag\": 11747\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

}
