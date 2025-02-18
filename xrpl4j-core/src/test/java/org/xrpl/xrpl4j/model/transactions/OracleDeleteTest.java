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

class OracleDeleteTest extends AbstractJsonTest {

  @Test
  void testDefaultFlags() {
    OracleDelete oracleDelete = baseBuilder().build();

    assertThat(oracleDelete.flags()).isEqualTo(TransactionFlags.EMPTY);
  }

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    OracleDelete oracleDelete = baseBuilder().build();
    String json = "\n" +
      "{\n" +
      "  \"TransactionType\": \"OracleDelete\",\n" +
      "  \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "  \"OracleDocumentID\": 1,\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"SigningPubKey\": \"" + ED_PUBLIC_KEY_HEX + "\"\n" +
      "}";

    assertCanSerializeAndDeserialize(oracleDelete, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    OracleDelete oracleDelete = baseBuilder()
      .putUnknownFields("Foo", "Bar")
      .build();
    String json = "\n" +
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"OracleDelete\",\n" +
      "  \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "  \"OracleDocumentID\": 1,\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"SigningPubKey\": \"" + ED_PUBLIC_KEY_HEX + "\"\n" +
      "}";

    assertCanSerializeAndDeserialize(oracleDelete, json);
  }

  private static ImmutableOracleDelete.Builder baseBuilder() {
    return OracleDelete.builder()
      .account(Address.of("rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(ED_PUBLIC_KEY)
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE));
  }
}