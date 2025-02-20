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
import org.xrpl.xrpl4j.model.flags.NfTokenCreateOfferFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutableNfTokenCreateOffer;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.NfTokenCreateOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class NfTokenCreateOfferJsonTests extends AbstractTransactionJsonTest<
  ImmutableNfTokenCreateOffer, ImmutableNfTokenCreateOffer.Builder, NfTokenCreateOffer
  > {

  /**
   * No-args Constructor.
   */
  protected NfTokenCreateOfferJsonTests() {
    super(NfTokenCreateOffer.class, ImmutableNfTokenCreateOffer.class, TransactionType.NFTOKEN_CREATE_OFFER);
  }


  @Override
  protected ImmutableNfTokenCreateOffer.Builder builder() {
    return ImmutableNfTokenCreateOffer.builder();
  }

  @Override
  protected NfTokenCreateOffer fullyPopulatedTransaction() {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    return NfTokenCreateOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .nfTokenId(id)
      .sequence(UnsignedInteger.valueOf(12))
      .amount(XrpCurrencyAmount.ofDrops(2000L))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected NfTokenCreateOffer fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected NfTokenCreateOffer minimallyPopulatedTransaction() {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    return NfTokenCreateOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .nfTokenId(id)
      .amount(XrpCurrencyAmount.ofDrops(2000L))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testMinimalNfTokenCreateOfferJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenCreateOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 0,\n" +
        "  \"Amount\": \"2000\",\n" +
        "  \"NFTokenID\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(minimallyPopulatedTransaction(), json);
  }

  @Test
  public void testJsonWithFlags() throws JsonProcessingException, JSONException {
    NfTokenCreateOffer transaction = builder().from(fullyPopulatedTransaction())
      .flags(NfTokenCreateOfferFlags.SELL_NFTOKEN)
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenCreateOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"Amount\": \"2000\",\n" +
        "  \"Flags\": " + NfTokenCreateOfferFlags.SELL_NFTOKEN + ",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenID\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenCreateOffer nfTokenCreateOffer = NfTokenCreateOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .nfTokenId(id)
      .sequence(UnsignedInteger.valueOf(12))
      .amount(XrpCurrencyAmount.ofDrops(2000L))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"NFTokenCreateOffer\",\n" +
        "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 12,\n" +
        "  \"Amount\": \"2000\",\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"NFTokenID\": \"000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(nfTokenCreateOffer, json);
  }
}
