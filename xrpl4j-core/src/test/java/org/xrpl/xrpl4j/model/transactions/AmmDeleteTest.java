package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class AmmDeleteTest extends AbstractJsonTest {

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    AmmDelete ammDelete = AmmDelete.builder()
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "EDD299D60BCE7980F6082945B5597FFFD35223F1950673BFA4D4AED6FDE5097156"
      ))
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"SigningPubKey\" : \"EDD299D60BCE7980F6082945B5597FFFD35223F1950673BFA4D4AED6FDE5097156\",\n" +
      "    \"TransactionType\" : \"AMMDelete\"\n" +
      "}";

    assertCanSerializeAndDeserialize(ammDelete, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    AmmDelete ammDelete = AmmDelete.builder()
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "EDD299D60BCE7980F6082945B5597FFFD35223F1950673BFA4D4AED6FDE5097156"
      ))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"SigningPubKey\" : \"EDD299D60BCE7980F6082945B5597FFFD35223F1950673BFA4D4AED6FDE5097156\",\n" +
      "    \"TransactionType\" : \"AMMDelete\"\n" +
      "}";

    assertCanSerializeAndDeserialize(ammDelete, json);
  }
}