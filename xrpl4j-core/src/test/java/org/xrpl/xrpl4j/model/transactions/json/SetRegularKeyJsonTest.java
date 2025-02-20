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
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutableSetRegularKey;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.SetRegularKey;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class SetRegularKeyJsonTest
  extends AbstractTransactionJsonTest<ImmutableSetRegularKey, ImmutableSetRegularKey.Builder, SetRegularKey> {

  /**
   * No-args Constructor.
   */
  protected SetRegularKeyJsonTest() {
    super(SetRegularKey.class, ImmutableSetRegularKey.class, TransactionType.SET_REGULAR_KEY);
  }


  @Override
  protected ImmutableSetRegularKey.Builder builder() {
    return ImmutableSetRegularKey.builder();
  }

  @Override
  protected SetRegularKey fullyPopulatedTransaction() {
    return SetRegularKey.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .regularKey(Address.of("rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected SetRegularKey fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected SetRegularKey minimallyPopulatedTransaction() {
    return SetRegularKey.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testSetRegularKeyJson() throws JsonProcessingException, JSONException {

    String json =
      "{\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"SetRegularKey\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"RegularKey\": \"rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testSetRegularKeyJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    SetRegularKey transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"RegularKey\": \"rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TransactionType\": \"SetRegularKey\"\n" +
      "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testSetRegularKeyJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    SetRegularKey transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"RegularKey\": \"rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD\",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"TransactionType\": \"SetRegularKey\"\n" +
      "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .regularKey(Address.of("rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD"))
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
      "  \"Fee\": \"12\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"RegularKey\": \"rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TransactionType\": \"SetRegularKey\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
