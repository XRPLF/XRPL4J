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
import org.xrpl.xrpl4j.model.transactions.ImmutableTicketCreate;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TicketCreate;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

class TicketCreateJsonTest
  extends AbstractTransactionJsonTest<ImmutableTicketCreate, ImmutableTicketCreate.Builder, TicketCreate> {

  /**
   * No-args Constructor.
   */
  protected TicketCreateJsonTest() {
    super(TicketCreate.class, ImmutableTicketCreate.class, TransactionType.TICKET_CREATE);
  }


  @Override
  protected ImmutableTicketCreate.Builder builder() {
    return ImmutableTicketCreate.builder();
  }

  @Override
  protected TicketCreate fullyPopulatedTransaction() {
    return TicketCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .ticketCount(UnsignedInteger.valueOf(200))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected TicketCreate fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected TicketCreate minimallyPopulatedTransaction() {
    return TicketCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .ticketCount(UnsignedInteger.valueOf(200))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    String json =
      "{\n" +
      "  \"TransactionType\": \"TicketCreate\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TicketCount\": 200\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    TicketCreate ticketCreate = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"TransactionType\": \"TicketCreate\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"TicketCount\": 200\n" +
      "}";

    assertCanSerializeAndDeserialize(ticketCreate, json);
  }

  @Test
  void testJsonWithNonZeroFlags() throws JSONException, JsonProcessingException {
    TicketCreate ticketCreate = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"TransactionType\": \"TicketCreate\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TicketCount\": 200\n" +
      "}";

    assertCanSerializeAndDeserialize(ticketCreate, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {

    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"TicketCreate\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TicketCount\": 200\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
