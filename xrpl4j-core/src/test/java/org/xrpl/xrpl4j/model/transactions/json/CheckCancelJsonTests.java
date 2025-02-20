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
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableCheckCancel;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class CheckCancelJsonTests
  extends AbstractTransactionJsonTest<ImmutableCheckCancel, ImmutableCheckCancel.Builder, CheckCancel> {

  /**
   * No-args Constructor.
   */
  protected CheckCancelJsonTests() {
    super(CheckCancel.class, ImmutableCheckCancel.class, TransactionType.CHECK_CANCEL);
  }

  @Override
  protected ImmutableCheckCancel.Builder builder() {
    return ImmutableCheckCancel.builder();
  }

  @Override
  protected CheckCancel fullyPopulatedTransaction() {
    return CheckCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .checkId(Hash256.of("49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"))
      .sequence(UnsignedInteger.valueOf(12))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected CheckCancel fullyPopulatedTransactionWithUnknownFields() {
    return CheckCancel.builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected CheckCancel minimallyPopulatedTransaction() {
    return CheckCancel.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .checkId(Hash256.of("49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"))
      .sequence(UnsignedInteger.valueOf(5))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testFullyPopulatedCheckCancelJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"TransactionType\": \"CheckCancel\",\n" +
      "  \"CheckID\": \"49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0\",\n" +
      "  \"Sequence\": 12,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Flags\": 0,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

  @Test
  public void testCheckCancelJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    CheckCancel fullyPopulatedTransaction = CheckCancel.builder().from(fullyPopulatedTransactionWithUnknownFields())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"TransactionType\": \"CheckCancel\",\n" +
      "  \"CheckID\": \"49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0\",\n" +
      "  \"Sequence\": 12,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Flags\": 0,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction, json);
  }

  @Test
  public void testCheckCancelJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    CheckCancel fullyPopulatedTransaction = CheckCancel.builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"TransactionType\": \"CheckCancel\",\n" +
      "  \"CheckID\": \"49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0\",\n" +
      "  \"Sequence\": 12,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction, json);
  }

  @Test
  public void testCheckCancelJsonWithEmptyFlags() throws JsonProcessingException, JSONException {
    CheckCancel fullyPopulatedTransaction = CheckCancel.builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.EMPTY)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"TransactionType\": \"CheckCancel\",\n" +
      "  \"CheckID\": \"49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0\",\n" +
      "  \"Sequence\": 12,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction, json);
  }

  @Test
  public void testCheckCancelJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"Account\": \"rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo\",\n" +
      "  \"TransactionType\": \"CheckCancel\",\n" +
      "  \"CheckID\": \"49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0\",\n" +
      "  \"Sequence\": 12,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
