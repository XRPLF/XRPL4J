package org.xrpl.xrpl4j.model.transactions.json;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
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
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableNfTokenCancelOffer;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.NfTokenCancelOffer;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.ArrayList;
import java.util.List;

public class NfTokenCancelOfferJsonTests extends AbstractTransactionJsonTest<
  ImmutableNfTokenCancelOffer, ImmutableNfTokenCancelOffer.Builder, NfTokenCancelOffer
  > {

  /**
   * No-args Constructor.
   */
  protected NfTokenCancelOfferJsonTests() {
    super(NfTokenCancelOffer.class, ImmutableNfTokenCancelOffer.class, TransactionType.NFTOKEN_CANCEL_OFFER);
  }

  @Override
  protected ImmutableNfTokenCancelOffer.Builder builder() {
    return ImmutableNfTokenCancelOffer.builder();
  }

  @Override
  protected NfTokenCancelOffer fullyPopulatedTransaction() {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    List<Hash256> offers = new ArrayList<>();
    offers.add(offer);
    return NfTokenCancelOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(12))
      .tokenOffers(offers)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected NfTokenCancelOffer fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected NfTokenCancelOffer minimallyPopulatedTransaction() {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    List<Hash256> offers = new ArrayList<>();
    offers.add(offer);
    return NfTokenCancelOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .tokenOffers(offers)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testMinimalNfTokenCancelOfferJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenCancelOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 0,\n" +
        "  \"NFTokenOffers\": [" +
        "    \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\"" +
        "  ],\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(minimallyPopulatedTransaction(), json);
  }

  @Test
  public void testNfTokenCancelOfferJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    NfTokenCancelOffer transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenCancelOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"Flags\": 0,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenOffers\": [" +
        "    \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\"" +
        "  ],\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testNfTokenCancelOfferJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    NfTokenCancelOffer transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenCancelOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenOffers\": [" +
        "    \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\"" +
        "  ],\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Foo\": \"Bar\",\n" +
        "  \"TransactionType\": \"NFTokenCancelOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenOffers\": [" +
        "    \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\"" +
        "  ],\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
