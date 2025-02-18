package org.xrpl.xrpl4j.model.transactions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;
import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY_HEX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

class OracleSetTest extends AbstractJsonTest {

  @Test
  void testDefaultFlags() {
    OracleSet oracleSet = baseBuilder().build();

    assertThat(oracleSet.flags()).isEqualTo(TransactionFlags.EMPTY);
  }

  @Test
  void testJsonEmptyFields() throws JSONException, JsonProcessingException {
    OracleSet oracleSet = baseBuilder().build();
    String json = "\n" +
      "{\n" +
      "  \"TransactionType\": \"OracleSet\",\n" +
      "  \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "  \"OracleDocumentID\": 1,\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"SigningPubKey\": \"" + ED_PUBLIC_KEY_HEX + "\",\n" +
      "  \"LastUpdateTime\": 1\n" +
      "}";

    assertCanSerializeAndDeserialize(oracleSet, json);
  }

  @Test
  void testFullJson() throws JSONException, JsonProcessingException {
    OracleSet oracleSet = baseBuilder()
      .provider(OracleProvider.of("70726F7669646572"))
      .assetClass("63757272656E6379")
      .uri(OracleUri.of("ABCD"))
      .addPriceDataSeries(
        PriceDataWrapper.of(
          PriceData.builder()
            .baseAsset("XRP")
            .quoteAsset("USD")
            .assetPrice(AssetPrice.of(UnsignedLong.ONE))
            .scale(UnsignedInteger.valueOf(3))
            .build()
        )
      )
      .build();
    String json = "\n" +
      "{\n" +
      "  \"TransactionType\": \"OracleSet\",\n" +
      "  \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "  \"Provider\": \"70726F7669646572\"," +
      "  \"AssetClass\": \"63757272656E6379\"," +
      "  \"URI\": \"ABCD\"," +
      "  \"OracleDocumentID\": 1,\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"SigningPubKey\": \"" + ED_PUBLIC_KEY_HEX + "\",\n" +
      "  \"LastUpdateTime\": 1,\n" +
      "  \"PriceDataSeries\": [\n" +
      "    {\n" +
      "      \"PriceData\": {\n" +
      "        \"BaseAsset\": \"XRP\",\n" +
      "        \"QuoteAsset\": \"USD\",\n" +
      "        \"AssetPrice\": \"1\",\n" +
      "        \"Scale\": 3\n" +
      "      }\n" +
      "    }\n" +
      "  ]" +
      "}";

    assertCanSerializeAndDeserialize(oracleSet, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    OracleSet oracleSet = baseBuilder()
      .putUnknownFields("Foo", "Bar")
      .build();
    String json = "\n" +
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"OracleSet\",\n" +
      "  \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "  \"OracleDocumentID\": 1,\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"SigningPubKey\": \"" + ED_PUBLIC_KEY_HEX + "\",\n" +
      "  \"LastUpdateTime\": 1\n" +
      "}";

    assertCanSerializeAndDeserialize(oracleSet, json);
  }

  private static ImmutableOracleSet.Builder baseBuilder() {
    return OracleSet.builder()
      .account(Address.of("rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(ED_PUBLIC_KEY)
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .lastUpdateTime(UnsignedInteger.ONE);
  }
}