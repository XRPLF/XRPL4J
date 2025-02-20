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
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.PaymentChannelClaimFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutablePaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class PaymentChannelClaimJsonTests extends AbstractTransactionJsonTest<
  ImmutablePaymentChannelClaim, ImmutablePaymentChannelClaim.Builder, PaymentChannelClaim
  > {

  /**
   * No-args Constructor.
   */
  protected PaymentChannelClaimJsonTests() {
    super(PaymentChannelClaim.class, ImmutablePaymentChannelClaim.class, TransactionType.PAYMENT_CHANNEL_CLAIM);
  }

  @Override
  protected ImmutablePaymentChannelClaim.Builder builder() {
    return ImmutablePaymentChannelClaim.builder();
  }

  @Override
  protected PaymentChannelClaim fullyPopulatedTransaction() {
    return PaymentChannelClaim.builder()
      .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .balance(XrpCurrencyAmount.ofDrops(1000000))
      .amount(XrpCurrencyAmount.ofDrops(1000000))
      .signature("30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4779E" +
        "F4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B")
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected PaymentChannelClaim fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected PaymentChannelClaim minimallyPopulatedTransaction() {
    return PaymentChannelClaim.builder()
      .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .signature("30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4779E" +
        "F4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B")
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testPaymentChannelClaimJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Sequence\": 1,\n" +
        "  \"TransactionType\": \"PaymentChannelClaim\",\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"Balance\": \"1000000\",\n" +
        "  \"Amount\": \"1000000\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"Signature\": \"30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4" +
        "779EF4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B\",\n" +
        "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\"\n" +
        "}";
    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testPaymentChannelClaimJsonWithFlags() throws JsonProcessingException, JSONException {

    PaymentChannelClaimFlags flags = PaymentChannelClaimFlags.builder()
      .tfClose(true)
      .tfRenew(true)
      .build();
    PaymentChannelClaim transaction = builder().from(fullyPopulatedTransaction())
      .flags(flags)
      .build();

    String json =
      "{\n" +
        "  \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
        "  \"Amount\": \"1000000\",\n" +
        "  \"Balance\": \"1000000\",\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Flags\": 2147680256,\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\",\n" +
        "  \"Sequence\": 1,\n" +
        "  \"Signature\": \"30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4" +
        "779EF4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B\",\n" +
        "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"TransactionType\": \"PaymentChannelClaim\"\n" +
        "}";
    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testPaymentChannelClaimJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
        "  \"Fee\": \"10\",\n" +
        "  \"Sequence\": 1,\n" +
        "  \"TransactionType\": \"PaymentChannelClaim\",\n" +
        "  \"Channel\": \"C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198\",\n" +
        "  \"Balance\": \"1000000\",\n" +
        "  \"Amount\": \"1000000\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"Signature\": \"30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4" +
        "779EF4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B\",\n" +
        "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"PublicKey\": \"32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A\"\n" +
        "}";
    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

}
