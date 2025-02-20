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
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableCheckCreate;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class CheckCreateJsonTests
  extends AbstractTransactionJsonTest<ImmutableCheckCreate, ImmutableCheckCreate.Builder, CheckCreate> {

  /**
   * No-args Constructor.
   */
  protected CheckCreateJsonTests() {
    super(CheckCreate.class, ImmutableCheckCreate.class, TransactionType.CHECK_CREATE);
  }

  @Override
  protected ImmutableCheckCreate.Builder builder() {
    return CheckCreate.builder();
  }

  @Override
  protected CheckCreate fullyPopulatedTransaction() {
    return builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .accountTransactionId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .destinationTag(UnsignedInteger.ONE)
      .expiration(UnsignedInteger.valueOf(570113521))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .invoiceId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .networkId(NetworkId.of(1024))
      .sequence(UnsignedInteger.ONE)
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Override
  protected CheckCreate fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected CheckCreate minimallyPopulatedTransaction() {
    return builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testCheckCreateJson() throws JsonProcessingException, JSONException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .destinationTag(UnsignedInteger.ONE)
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .expiration(UnsignedInteger.valueOf(570113521))
      .invoiceId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();

    String json =
      "{\n" +
      "  \"TransactionType\": \"CheckCreate\",\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"Destination\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"SendMax\": \"100000000\",\n" +
      "  \"Expiration\": 570113521,\n" +
      "  \"InvoiceID\": \"6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B\",\n" +
      "  \"DestinationTag\": 1,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCreate, json);
  }

  @Test
  public void testCheckCreateJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"TransactionType\": \"CheckCreate\",\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"Destination\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"SendMax\": \"100000000\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCreate, json);
  }

  @Test
  public void testCheckCreateJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"TransactionType\": \"CheckCreate\",\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"Destination\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"SendMax\": \"100000000\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG + ",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCreate, json);
  }

  @Test
  public void testCheckCreateJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .destinationTag(UnsignedInteger.ONE)
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .expiration(UnsignedInteger.valueOf(570113521))
      .invoiceId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"CheckCreate\",\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"Destination\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"SendMax\": \"100000000\",\n" +
      "  \"Expiration\": 570113521,\n" +
      "  \"InvoiceID\": \"6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B\",\n" +
      "  \"DestinationTag\": 1,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCreate, json);
  }
}
