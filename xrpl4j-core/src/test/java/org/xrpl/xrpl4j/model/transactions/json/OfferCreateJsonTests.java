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
import org.xrpl.xrpl4j.model.flags.OfferCreateFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutableOfferCreate;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.OfferCreate;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class OfferCreateJsonTests
  extends AbstractTransactionJsonTest<ImmutableOfferCreate, ImmutableOfferCreate.Builder, OfferCreate> {

  /**
   * No-args Constructor.
   */
  protected OfferCreateJsonTests() {
    super(OfferCreate.class, ImmutableOfferCreate.class, TransactionType.OFFER_CREATE);
  }


  @Override
  protected ImmutableOfferCreate.Builder builder() {
    return ImmutableOfferCreate.builder();
  }

  @Override
  protected OfferCreate fullyPopulatedTransaction() {
    return OfferCreate.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .offerSequence(UnsignedInteger.valueOf(13))
      .takerPays(XrpCurrencyAmount.ofDrops(14))
      .takerGets(XrpCurrencyAmount.ofDrops(15))
      .expiration(UnsignedInteger.valueOf(16))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected OfferCreate fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected OfferCreate minimallyPopulatedTransaction() {
    return OfferCreate.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .takerPays(XrpCurrencyAmount.ofDrops(14))
      .takerGets(XrpCurrencyAmount.ofDrops(15))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testOfferCreateJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"TransactionType\": \"OfferCreate\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"OfferSequence\": 13,\n" +
      "  \"TakerPays\": \"14\",\n" +
      "  \"TakerGets\": \"15\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Expiration\": 16\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testMinimallyPopulatedOfferCreateJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"TransactionType\": \"OfferCreate\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"OfferSequence\": 13,\n" +
      "  \"TakerPays\": \"14\",\n" +
      "  \"TakerGets\": \"15\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Expiration\": 16\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testOfferCreateJsonWithFlags() throws JsonProcessingException, JSONException {
    OfferCreateFlags flags = OfferCreateFlags.builder()
      .tfSell(true)
      .tfImmediateOrCancel(true)
      .tfPassive(true)
      .tfImmediateOrCancel(true)
      .build();
    OfferCreate transaction = builder().from(fullyPopulatedTransaction())
      .flags(flags)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Flags\": " + flags + ",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"OfferSequence\": 13,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TakerPays\": \"14\",\n" +
      "  \"TakerGets\": \"15\",\n" +
      "  \"TransactionType\": \"OfferCreate\",\n" +
      "  \"Expiration\": 16\n" +
      "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testOfferCreateJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"TransactionType\": \"OfferCreate\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"OfferSequence\": 13,\n" +
      "  \"TakerPays\": \"14\",\n" +
      "  \"TakerGets\": \"15\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Expiration\": 16\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

}
