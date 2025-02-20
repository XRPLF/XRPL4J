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
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.NfTokenMintFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutableNfTokenMint;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.NfTokenMint;
import org.xrpl.xrpl4j.model.transactions.NfTokenUri;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.TransferFee;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class NfTokenMintJsonTests extends AbstractTransactionJsonTest<
  ImmutableNfTokenMint, ImmutableNfTokenMint.Builder, NfTokenMint
  > {

  /**
   * No-args Constructor.
   */
  protected NfTokenMintJsonTests() {
    super(NfTokenMint.class, ImmutableNfTokenMint.class, TransactionType.NFTOKEN_MINT);
  }


  @Override
  protected ImmutableNfTokenMint.Builder builder() {
    return ImmutableNfTokenMint.builder();
  }

  @Override
  protected NfTokenMint fullyPopulatedTransaction() {
    return NfTokenMint.builder()
      .fee(XrpCurrencyAmount.ofDrops(12))
      .account(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .tokenTaxon(UnsignedLong.valueOf(146999694L))
      .sequence(UnsignedInteger.valueOf(12))
      .transferFee(TransferFee.of(UnsignedInteger.valueOf(1000)))
      .flags(NfTokenMintFlags.builder()
        .tfTransferable(true)
        .build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected NfTokenMint fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected NfTokenMint minimallyPopulatedTransaction() {
    return NfTokenMint.builder()
      .fee(XrpCurrencyAmount.ofDrops(12))
      .account(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .tokenTaxon(UnsignedLong.valueOf(146999694L))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testMinimalNfTokenMintJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenMint\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Flags\": 2147483656,\n" +
        "  \"Sequence\": 12,\n" +
        "  \"TransferFee\": 1000,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NFTokenTaxon\": 146999694\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testJsonWithoutFlags() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenMint\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Sequence\": 0,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NFTokenTaxon\": 146999694\n" +
        "}";

    assertCanSerializeAndDeserialize(minimallyPopulatedTransaction(), json);
  }

  @Test
  public void testMinimalNfTokenMintWithUriJson() throws JsonProcessingException, JSONException {
    String uri = "ipfs://bafybeigdyrzt5sfp7udm7hu76uh7y26nf4dfuylqabf3oclgtqy55fbzdi";
    NfTokenMint transaction = builder().from(fullyPopulatedTransaction())
      .uri(NfTokenUri.ofPlainText(uri))
      .build();

    String json =
      "{\n" +
        "  \"TransactionType\": \"NFTokenMint\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Flags\": 2147483656,\n" +
        "  \"Sequence\": 12,\n" +
        "  \"TransferFee\": 1000,\n" +
        "  \"URI\": \"697066733A2F2F62616679626569676479727A74357366703775646D3768753736756837" +
        "7932366E6634646675796C71616266336F636C67747179353566627A6469\",\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NFTokenTaxon\": 146999694,\n" +
        "  \"NetworkID\": 1024\n" +
        "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"NFTokenMint\",\n" +
        "  \"Account\": \"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba\",\n" +
        "  \"Fee\": \"12\",\n" +
        "  \"Flags\": 2147483656,\n" +
        "  \"Sequence\": 12,\n" +
        "  \"TransferFee\": 1000,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "  \"NFTokenTaxon\": 146999694\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
