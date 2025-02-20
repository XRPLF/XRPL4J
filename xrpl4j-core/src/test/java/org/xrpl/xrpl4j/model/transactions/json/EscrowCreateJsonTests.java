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
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.ripple.cryptoconditions.CryptoConditionReader;
import com.ripple.cryptoconditions.der.DerEncodingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.EscrowCreate;
import org.xrpl.xrpl4j.model.transactions.ImmutableEscrowCreate;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class EscrowCreateJsonTests extends AbstractTransactionJsonTest<
  ImmutableEscrowCreate, ImmutableEscrowCreate.Builder, EscrowCreate
  > {

  /**
   * No-args Constructor.
   */
  protected EscrowCreateJsonTests() {
    super(EscrowCreate.class, ImmutableEscrowCreate.class, TransactionType.ESCROW_CREATE);
  }

  @Override
  protected ImmutableEscrowCreate.Builder builder() {
    return ImmutableEscrowCreate.builder();
  }

  @Override
  protected EscrowCreate fullyPopulatedTransaction() {
    try {
      return EscrowCreate.builder()
        .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
        .fee(XrpCurrencyAmount.ofDrops(12))
        .sequence(UnsignedInteger.ONE)
        .amount(XrpCurrencyAmount.ofDrops(10000))
        .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
        .destinationTag(UnsignedInteger.valueOf(23480))
        .cancelAfter(UnsignedLong.valueOf(533257958))
        .finishAfter(UnsignedLong.valueOf(533171558))
        .condition(CryptoConditionReader.readCondition(
          BaseEncoding.base16()
            .decode("A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
        )
        .sourceTag(UnsignedInteger.valueOf(11747))
        .signingPublicKey(
          PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
        )
        .networkId(NetworkId.of(1024))
        .build();
    } catch (DerEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected EscrowCreate fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected EscrowCreate minimallyPopulatedTransaction() {
    return EscrowCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testEscrowCreateJson() throws JsonProcessingException, JSONException, DerEncodingException {
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .cancelAfter(UnsignedLong.valueOf(533257958))
      .finishAfter(UnsignedLong.valueOf(533171558))
      .condition(CryptoConditionReader.readCondition(
        BaseEncoding.base16()
          .decode("A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .sourceTag(UnsignedInteger.valueOf(11747))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();

    String json = "{\n" +
      "    \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"TransactionType\": \"EscrowCreate\",\n" +
      "    \"Amount\": \"10000\",\n" +
      "    \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "    \"CancelAfter\": 533257958,\n" +
      "    \"FinishAfter\": 533171558,\n" +
      "    \"Condition\": \"A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100\",\n" +
      "    \"DestinationTag\": 23480,\n" +
      "    \"SourceTag\": 11747,\n" +
      "    \"Sequence\": 1,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(escrowCreate, json);
  }

  @Test
  public void testEscrowCreateJsonWithUnsetFlags() throws JsonProcessingException, JSONException, DerEncodingException {
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .cancelAfter(UnsignedLong.valueOf(533257958))
      .finishAfter(UnsignedLong.valueOf(533171558))
      .condition(CryptoConditionReader.readCondition(
        BaseEncoding.base16()
          .decode("A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .sourceTag(UnsignedInteger.valueOf(11747))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String json = "{\n" +
      "    \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"TransactionType\": \"EscrowCreate\",\n" +
      "    \"Amount\": \"10000\",\n" +
      "    \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "    \"CancelAfter\": 533257958,\n" +
      "    \"FinishAfter\": 533171558,\n" +
      "    \"Condition\": \"A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100\",\n" +
      "    \"DestinationTag\": 23480,\n" +
      "    \"SourceTag\": 11747,\n" +
      "    \"Sequence\": 1,\n" +
      "    \"Flags\": 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(escrowCreate, json);
  }

  @Test
  public void testEscrowCreateJsonWithNonZeroFlags()
    throws JsonProcessingException, JSONException, DerEncodingException {
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .cancelAfter(UnsignedLong.valueOf(533257958))
      .finishAfter(UnsignedLong.valueOf(533171558))
      .condition(CryptoConditionReader.readCondition(
        BaseEncoding.base16()
          .decode("A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .sourceTag(UnsignedInteger.valueOf(11747))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "    \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"TransactionType\": \"EscrowCreate\",\n" +
      "    \"Amount\": \"10000\",\n" +
      "    \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "    \"CancelAfter\": 533257958,\n" +
      "    \"FinishAfter\": 533171558,\n" +
      "    \"Condition\": \"A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100\",\n" +
      "    \"DestinationTag\": 23480,\n" +
      "    \"SourceTag\": 11747,\n" +
      "    \"Sequence\": 1,\n" +
      "    \"Flags\": %s,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"Fee\": \"12\"\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG.getValue());

    assertCanSerializeAndDeserialize(escrowCreate, json);
  }

  @Test
  public void testEscrowCreateJsonWithUnknownFields()
    throws JsonProcessingException, JSONException, DerEncodingException {
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .cancelAfter(UnsignedLong.valueOf(533257958))
      .finishAfter(UnsignedLong.valueOf(533171558))
      .condition(CryptoConditionReader.readCondition(
        BaseEncoding.base16()
          .decode("A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .sourceTag(UnsignedInteger.valueOf(11747))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"TransactionType\": \"EscrowCreate\",\n" +
      "    \"Amount\": \"10000\",\n" +
      "    \"Destination\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "    \"CancelAfter\": 533257958,\n" +
      "    \"FinishAfter\": 533171558,\n" +
      "    \"Condition\": \"A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100\",\n" +
      "    \"DestinationTag\": 23480,\n" +
      "    \"SourceTag\": 11747,\n" +
      "    \"Sequence\": 1,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"Fee\": \"12\"\n" +
      "}";

    assertCanSerializeAndDeserialize(escrowCreate, json);
  }
}
