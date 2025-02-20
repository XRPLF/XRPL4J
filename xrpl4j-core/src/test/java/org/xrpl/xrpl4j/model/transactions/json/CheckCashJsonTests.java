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
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableCheckCash;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class CheckCashJsonTests
  extends AbstractTransactionJsonTest<ImmutableCheckCash, ImmutableCheckCash.Builder, CheckCash> {

  /**
   * No-args Constructor.
   */
  protected CheckCashJsonTests() {
    super(CheckCash.class, ImmutableCheckCash.class, TransactionType.CHECK_CASH);
  }

  @Override
  protected ImmutableCheckCash.Builder builder() {
    return ImmutableCheckCash.builder();
  }

  @Override
  protected CheckCash fullyPopulatedTransaction() {
    return builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .checkId(Hash256.of("838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334"))
      .sequence(UnsignedInteger.valueOf(1))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected CheckCash fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected CheckCash minimallyPopulatedTransaction() {
    return builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .checkId(Hash256.of("49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"))
      .deliverMin(IssuedCurrencyAmount.builder()
        .currency("USD")
        .value("10")
        .issuer(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo")
        ).build())
      .fee(XrpCurrencyAmount.ofDrops(12))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testCheckCashJsonWithDeliverMin() throws JsonProcessingException, JSONException {
    CheckCash checkCash = builder().from(minimallyPopulatedTransaction())
      .deliverMin(XrpCurrencyAmount.ofDrops(100))
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"DeliverMin\": \"100\",\n" +
      "  \"TransactionType\": \"CheckCash\",\n" +
      "  \"CheckID\": \"49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0\",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Sequence\": 0,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCash, json);
  }

  @Test
  public void testCheckCashJsonWithAmount() throws JsonProcessingException, JSONException {
    CheckCash checkCash = builder().from(fullyPopulatedTransactionWithUnknownFields())
      .amount(XrpCurrencyAmount.ofDrops(100))
      .build();

    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"Amount\": \"100\",\n" +
      "  \"CheckID\": \"838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TransactionType\": \"CheckCash\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCash, json);
  }

  @Test
  public void testCheckCashJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    CheckCash checkCash = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"TransactionType\": \"CheckCash\",\n" +
      "  \"Amount\": \"100\",\n" +
      "  \"CheckID\": \"838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": 0,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCash, json);
  }

  @Test
  public void testCheckCashJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    CheckCash checkCash = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"TransactionType\": \"CheckCash\",\n" +
      "  \"Amount\": \"100\",\n" +
      "  \"CheckID\": \"838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(checkCash, json);
  }

  @Test
  public void testCheckCashJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Amount\": \"100\",\n" +
      "  \"Account\": \"rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy\",\n" +
      "  \"CheckID\": \"838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TransactionType\": \"CheckCash\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
