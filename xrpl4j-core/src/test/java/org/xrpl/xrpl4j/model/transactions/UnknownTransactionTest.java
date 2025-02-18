package org.xrpl.xrpl4j.model.transactions;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

import java.util.HashMap;

class UnknownTransactionTest extends AbstractJsonTest {

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    HashMap<String, Object> unknownFields = Maps.newHashMap();
    unknownFields.put("Domain", "");
    unknownFields.put("SetFlag", 5);
    unknownFields.put("MessageKey", "");
    unknownFields.put("TransferRate", 1000000001);
    unknownFields.put("TickSize", 15);
    unknownFields.put("ClearFlag", 8);
    unknownFields.put("NFTokenMinter", "rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn");
    unknownFields.put("WalletLocator", Strings.repeat("0", 64));
    unknownFields.put("EmailHash", Strings.repeat("0", 32));
    TransactionFlags flags = TransactionFlags.of(2147483648L);
    UnknownTransaction transaction = UnknownTransaction.builder()
      .unknownTransactionType("AccountSet2")
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(5))
      .flags(flags)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC"))
      .unknownFields(
        unknownFields
      )
      .networkId(NetworkId.of(UnsignedInteger.valueOf(1024)))
      .build();

    assertThat(transaction.flags()).isEqualTo(flags);

    // Same properties as AccountSet, but TransactionType is AccountSet2
    String json = "{\n" +
      "    \"TransactionType\":\"AccountSet2\",\n" +
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

    assertCanSerializeAndDeserialize(transaction, json);
  }

}