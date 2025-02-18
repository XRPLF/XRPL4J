package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

class DidDeleteTest extends AbstractJsonTest {

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    DidDelete transaction = baseBuilder().build();

    String json = String.format("{\n" +
      "    \"TransactionType\": \"DIDDelete\", \n" +
      "    \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"Sequence\": 391,\n" +
      "    \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    DidDelete transaction = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();

    String json = String.format("{\n" +
      "    \"TransactionType\": \"DIDDelete\", \n" +
      "    \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"Sequence\": 391,\n" +
      "    \"Flags\": 0,\n" +
      "    \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithSetFlags() throws JSONException, JsonProcessingException {
    DidDelete transaction = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "    \"TransactionType\": \"DIDDelete\", \n" +
      "    \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"Sequence\": 391,\n" +
      "    \"Flags\": %s,\n" +
      "    \"SigningPubKey\":\"%s\"\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG, ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    DidDelete transaction = baseBuilder()
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"TransactionType\": \"DIDDelete\", \n" +
      "    \"Account\": \"rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex\",\n" +
      "    \"Fee\": \"12\",\n" +
      "    \"Sequence\": 391,\n" +
      "    \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  private ImmutableDidDelete.Builder baseBuilder() {
    return DidDelete.builder()
      .account(Address.of("rp4pqYgrTAtdPHuZd1ZQWxrzx45jxYcZex"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(ED_PUBLIC_KEY);
  }
}