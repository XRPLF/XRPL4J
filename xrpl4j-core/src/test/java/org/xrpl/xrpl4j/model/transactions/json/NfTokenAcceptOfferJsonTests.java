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
import org.xrpl.xrpl4j.model.transactions.ImmutableNfTokenAcceptOffer;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.NfTokenAcceptOffer;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class NfTokenAcceptOfferJsonTests extends AbstractTransactionJsonTest<
  ImmutableNfTokenAcceptOffer, ImmutableNfTokenAcceptOffer.Builder, NfTokenAcceptOffer
  > {

  /**
   * No-args Constructor.
   */
  protected NfTokenAcceptOfferJsonTests() {
    super(NfTokenAcceptOffer.class, ImmutableNfTokenAcceptOffer.class, TransactionType.NFTOKEN_ACCEPT_OFFER);
  }

  @Override
  protected ImmutableNfTokenAcceptOffer.Builder builder() {
    return ImmutableNfTokenAcceptOffer.builder();
  }

  @Override
  protected NfTokenAcceptOffer fullyPopulatedTransaction() {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    return NfTokenAcceptOffer.builder()
      .fee(XrpCurrencyAmount.ofDrops(12))
      .account(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .sequence(UnsignedInteger.valueOf(12))
      .brokerFee(XrpCurrencyAmount.ofDrops(10))
      .buyOffer(offer)
      .sellOffer(offer)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected NfTokenAcceptOffer fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected NfTokenAcceptOffer minimallyPopulatedTransaction() {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    return NfTokenAcceptOffer.builder()
      .fee(XrpCurrencyAmount.ofDrops(12))
      .account(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .buyOffer(offer)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testMinimalNfTokenAcceptOfferJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenAcceptOffer\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"NFTokenBuyOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"NFTokenSellOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenBrokerFee\": \"10\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testNfTokenAcceptOfferJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    NfTokenAcceptOffer transactionWithUnsetFlags = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenAcceptOffer\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Flags\": 0,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenBrokerFee\": \"10\",\n" +
        "  \"NFTokenBuyOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"NFTokenSellOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(transactionWithUnsetFlags, json);
  }

  @Test
  public void testNfTokenAcceptOfferJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    NfTokenAcceptOffer transactionWithNonZeroFlags = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenAcceptOffer\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
        "  \"NFTokenBuyOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"NFTokenSellOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenBrokerFee\": \"10\"\n" +
        "}";

    assertCanSerializeAndDeserialize(transactionWithNonZeroFlags, json);
  }

  @Test
  public void testNfTokenAcceptOfferWithOnlyBuyOffer() throws JsonProcessingException, JSONException {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenAcceptOffer nfTokenAcceptOffer = NfTokenAcceptOffer.builder()
      .fee(XrpCurrencyAmount.ofDrops(12))
      .account(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .sequence(UnsignedInteger.valueOf(12))
      .buyOffer(offer)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenAcceptOffer\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"NFTokenBuyOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(nfTokenAcceptOffer, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"NFTokenAcceptOffer\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"NFTokenBuyOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"NFTokenSellOffer\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenBrokerFee\": \"10\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
