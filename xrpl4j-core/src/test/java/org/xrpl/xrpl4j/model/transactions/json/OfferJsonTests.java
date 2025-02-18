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
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.OfferCreateFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.OfferCancel;
import org.xrpl.xrpl4j.model.transactions.OfferCreate;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class OfferJsonTests extends AbstractJsonTest {

  @Test
  public void testOfferCancelJson() throws JsonProcessingException, JSONException {
    OfferCancel offerCancel = OfferCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.valueOf(12))
      .offerSequence(UnsignedInteger.valueOf(13))
      .fee(XrpCurrencyAmount.ofDrops(14))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();

    String json = "{\n" +
      "    \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "    \"TransactionType\": \"OfferCancel\",\n" +
      "    \"Sequence\": 12,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"Fee\": \"14\"\n" +
      "}";

    assertCanSerializeAndDeserialize(offerCancel, json);
  }

  @Test
  public void testOfferCancelJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    OfferCancel offerCancel = OfferCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.valueOf(12))
      .offerSequence(UnsignedInteger.valueOf(13))
      .fee(XrpCurrencyAmount.ofDrops(14))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String json = "{\n" +
      "    \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "    \"TransactionType\": \"OfferCancel\",\n" +
      "    \"Sequence\": 12,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"Flags\": 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"Fee\": \"14\"\n" +
      "}";

    assertCanSerializeAndDeserialize(offerCancel, json);
  }

  @Test
  public void testOfferCancelJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    OfferCancel offerCancel = OfferCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.valueOf(12))
      .offerSequence(UnsignedInteger.valueOf(13))
      .fee(XrpCurrencyAmount.ofDrops(14))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "    \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "    \"TransactionType\": \"OfferCancel\",\n" +
      "    \"Sequence\": 12,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"Flags\": %s,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"Fee\": \"14\"\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG.getValue());

    assertCanSerializeAndDeserialize(offerCancel, json);
  }

  @Test
  public void testOfferCancelJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    OfferCancel offerCancel = OfferCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.valueOf(12))
      .offerSequence(UnsignedInteger.valueOf(13))
      .fee(XrpCurrencyAmount.ofDrops(14))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "    \"TransactionType\": \"OfferCancel\",\n" +
      "    \"Sequence\": 12,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"Fee\": \"14\"\n" +
      "}";

    assertCanSerializeAndDeserialize(offerCancel, json);
  }

  @Test
  public void testOfferCreateJson() throws JsonProcessingException, JSONException {
    OfferCreate offerCreate = OfferCreate.builder()
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

    String json = "{\n" +
      "    \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "    \"TransactionType\": \"OfferCreate\",\n" +
      "    \"Sequence\": 1,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"TakerPays\": \"14\",\n" +
      "    \"TakerGets\": \"15\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"Expiration\": 16\n" +
      "}";

    assertCanSerializeAndDeserialize(offerCreate, json);
  }

  @Test
  public void testOfferCreateJsonWithFlags() throws JsonProcessingException, JSONException {
    OfferCreate offerCreate = OfferCreate.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .offerSequence(UnsignedInteger.valueOf(13))
      .takerPays(XrpCurrencyAmount.ofDrops(14))
      .takerGets(XrpCurrencyAmount.ofDrops(15))
      .expiration(UnsignedInteger.valueOf(16))
      .flags(OfferCreateFlags.builder().tfSell(true).build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();

    String json = String.format("{\n" +
      "    \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "    \"TransactionType\": \"OfferCreate\",\n" +
      "    \"Sequence\": 1,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"TakerPays\": \"14\",\n" +
      "    \"TakerGets\": \"15\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"Flags\": %s,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"Expiration\": 16\n" +
      "}", offerCreate.flags());

    assertCanSerializeAndDeserialize(offerCreate, json);
  }

  @Test
  public void testOfferCreateJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    OfferCreate offerCreate = OfferCreate.builder()
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
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "    \"TransactionType\": \"OfferCreate\",\n" +
      "    \"Sequence\": 1,\n" +
      "    \"OfferSequence\": 13,\n" +
      "    \"TakerPays\": \"14\",\n" +
      "    \"TakerGets\": \"15\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"Expiration\": 16\n" +
      "}";

    assertCanSerializeAndDeserialize(offerCreate, json);
  }

}
