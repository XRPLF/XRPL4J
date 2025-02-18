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
import com.google.common.base.Strings;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.AccountSetTransactionFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.AccountSet.AccountSetFlag;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class AccountSetJsonTests extends AbstractJsonTest {

  @Test
  public void fullyPopulatedAccountSet() throws JSONException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .domain("6578616D706C652E636F6D")
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .emailHash("f9879d71855b5ff21e4963273a886bfc")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AccountSetTransactionFlags.of(TransactionFlags.FULLY_CANONICAL_SIG.getValue()))
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .networkId(NetworkId.of(1024))
      .walletLocator("ABCD")
      .walletSize(UnsignedInteger.ONE)
      .build();

    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Flags\":2147483648,\n" +
      "    \"Domain\":\"6578616D706C652E636F6D\",\n" +
      "    \"SetFlag\":5,\n" +
      "    \"MessageKey\":\"03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB\",\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":8,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"WalletSize\": 1,\n" +
      "    \"WalletLocator\": \"ABCD\",\n" +
      "    \"EmailHash\":\"f9879d71855b5ff21e4963273a886bfc\"\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  public void accountSetWithEmptyOptionalStringFields() throws JSONException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AccountSetTransactionFlags.of(TransactionFlags.FULLY_CANONICAL_SIG.getValue()))
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .networkId(NetworkId.of(1024))
      .build();

    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Flags\":2147483648,\n" +
      "    \"SetFlag\":5,\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":8,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"NetworkID\": 1024\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  public void accountSetWithEmptyStringFields() throws JSONException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .domain("")
      .messageKey("")
      .emailHash(Strings.repeat("0", 32))
      .walletLocator(Strings.repeat("0", 64))
      .sequence(UnsignedInteger.valueOf(5))
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AccountSetTransactionFlags.of(TransactionFlags.FULLY_CANONICAL_SIG.getValue()))
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .networkId(NetworkId.of(1024))
      .build();

    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Flags\":2147483648,\n" +
      "    \"Domain\":\"\",\n" +
      "    \"SetFlag\":5,\n" +
      "    \"MessageKey\":\"\",\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":8,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"WalletLocator\" : \"" + Strings.repeat("0", 64) + "\",\n" +
      "    \"EmailHash\" : \"" + Strings.repeat("0", 32) + "\",\n" +
      "    \"NetworkID\": 1024\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  public void testJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .domain("6578616D706C652E636F6D")
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .emailHash(Strings.repeat("0", 32))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AccountSetTransactionFlags.of(0))
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .build();

    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Flags\":0,\n" +
      "    \"Domain\":\"6578616D706C652E636F6D\",\n" +
      "    \"SetFlag\":5,\n" +
      "    \"MessageKey\":\"03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB\",\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":8,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"EmailHash\":\"" + Strings.repeat("0", 32) + "\"\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  public void testJsonWithEmptyFlags() throws JsonProcessingException, JSONException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .domain("6578616D706C652E636F6D")
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .emailHash("f9879d71855b5ff21e4963273a886bfc")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .build();

    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Domain\":\"6578616D706C652E636F6D\",\n" +
      "    \"SetFlag\":5,\n" +
      "    \"MessageKey\":\"03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB\",\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":8,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"EmailHash\":\"f9879d71855b5ff21e4963273a886bfc\"\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  void testJsonWithZeroClearFlagAndSetFlag() throws JSONException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .domain("6578616D706C652E636F6D")
      .setFlag(AccountSetFlag.NONE)
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.NONE)
      .emailHash("f9879d71855b5ff21e4963273a886bfc")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .build();

    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Domain\":\"6578616D706C652E636F6D\",\n" +
      "    \"SetFlag\":0,\n" +
      "    \"MessageKey\":\"03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB\",\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"EmailHash\":\"f9879d71855b5ff21e4963273a886bfc\"\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  void testJsonWithUnrecognizedClearAndSetFlag() throws JSONException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rhyg7sn3ZQj9aja9CBuLETFKdkG9Fte7ck"))
      .fee(XrpCurrencyAmount.ofDrops(15))
      .sequence(UnsignedInteger.valueOf(40232131))
      .setFlagRawValue(UnsignedInteger.valueOf(4294967295L))
      .clearFlagRawValue(UnsignedInteger.valueOf(4294967295L))
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "ED03FCED79BB3699089BC3F0F57BBD9F9ABA4772C20EC14BFAE908B4299344194A"
      ))
      .transactionSignature(Signature.fromBase16("8D0915449FB617234DD54E1BA79136B50C4439696BB4287F" +
        "CA25717F3FD4875D5A1FEE6269B91D71D9306B48DECAAE1F1C91CAD1AAD0E0D683DC11E68212740E"))
      .build();

    String json = "{\n" +
      "    \"Account\": \"rhyg7sn3ZQj9aja9CBuLETFKdkG9Fte7ck\",\n" +
      "    \"Fee\": \"15\",\n" +
      "    \"Sequence\": 40232131,\n" +
      "    \"SetFlag\": 4294967295,\n" +
      "    \"ClearFlag\": 4294967295,\n" +
      "    \"SigningPubKey\": \"ED03FCED79BB3699089BC3F0F57BBD9F9ABA4772C20EC14BFAE908B4299344194A\",\n" +
      "    \"TransactionType\": \"AccountSet\",\n" +
      "    \"TxnSignature\": \"8D0915449FB617234DD54E1BA79136B50C4439696BB4287FCA25717F3FD4875D5A1FEE6269B" +
      "91D71D9306B48DECAAE1F1C91CAD1AAD0E0D683DC11E68212740E\"" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .domain("6578616D706C652E636F6D")
      .setFlag(AccountSetFlag.ACCOUNT_TXN_ID)
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .transferRate(UnsignedInteger.valueOf(1000000001))
      .tickSize(UnsignedInteger.valueOf(15))
      .clearFlag(AccountSetFlag.DEFAULT_RIPPLE)
      .emailHash("f9879d71855b5ff21e4963273a886bfc")
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AccountSetTransactionFlags.of(TransactionFlags.FULLY_CANONICAL_SIG.getValue()))
      .mintAccount(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .networkId(NetworkId.of(1024))
      .walletLocator("ABCD")
      .walletSize(UnsignedInteger.ONE)
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"TransactionType\":\"AccountSet\",\n" +
      "    \"Account\":\"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"Fee\":\"12\",\n" +
      "    \"Sequence\":5,\n" +
      "    \"Flags\":2147483648,\n" +
      "    \"Domain\":\"6578616D706C652E636F6D\",\n" +
      "    \"SetFlag\":5,\n" +
      "    \"MessageKey\":\"03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB\",\n" +
      "    \"TransferRate\":1000000001,\n" +
      "    \"TickSize\":15,\n" +
      "    \"ClearFlag\":8,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"NFTokenMinter\" : \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "    \"NetworkID\": 1024,\n" +
      "    \"WalletSize\": 1,\n" +
      "    \"WalletLocator\": \"ABCD\",\n" +
      "    \"EmailHash\":\"f9879d71855b5ff21e4963273a886bfc\"\n" +
      "}";

    assertCanSerializeAndDeserialize(accountSet, json);
  }
}
