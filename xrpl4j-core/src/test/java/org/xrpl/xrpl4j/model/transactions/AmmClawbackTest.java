package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.AmmClawbackFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

/**
 * Unit tests for {@link AmmClawback}.
 **/
public class AmmClawbackTest extends AbstractJsonTest {

  String usd = "USD";

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    AmmClawback ammClawback = AmmClawback.builder()
        .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
        .amount(
            IssuedCurrencyAmount.builder()
                .currency(usd)
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .value("25")
                .build()
        )
        .asset(Issue.builder().currency(usd).issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd")).build())
        .asset2(Issue.builder().currency(usd).issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd")).build())
        .holder(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(UnsignedInteger.valueOf(6))
        .signingPublicKey(
            PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
        )
        .build();

    String json = "{\n" +
        "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
        "    \"Amount\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
        "        \"value\" : \"25\"\n" +
        "    },\n" +
        "    \"Asset\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
        "    },\n" +
        "    \"Asset2\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
        "    },\n" +
        "    \"Holder\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
        "    \"Fee\" : \"10\",\n" +
        "    \"Sequence\" : 6,\n" +
        "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "    \"TransactionType\" : \"AMMClawback\"\n" +
        "}";

    assertCanSerializeAndDeserialize(ammClawback, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    AmmClawback ammClawback = AmmClawback.builder()
        .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
        .amount(
            IssuedCurrencyAmount.builder()
                .currency(usd)
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .value("25")
                .build()
        )
        .holder(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
        .asset(Issue.builder().currency(usd).issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd")).build())
        .asset2(Issue.builder().currency(usd).issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd")).build())
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(UnsignedInteger.valueOf(6))
        .signingPublicKey(
            PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
        )
        .flags(AmmClawbackFlags.UNSET)
        .build();

    String json = "{\n" +
        "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
        "    \"Amount\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
        "        \"value\" : \"25\"\n" +
        "    },\n" +
        "    \"Asset\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
        "    },\n" +
        "    \"Asset2\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
        "    },\n" +
        "    \"Holder\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
        "    \"Flags\" : 0,\n" +
        "    \"Fee\" : \"10\",\n" +
        "    \"Sequence\" : 6,\n" +
        "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "    \"TransactionType\" : \"AMMClawback\"\n" +
        "}";

    assertCanSerializeAndDeserialize(ammClawback, json);
  }

  @Test
  void testJsonWithNonZeroFlags() throws JSONException, JsonProcessingException {
    AmmClawback ammClawback = AmmClawback.builder()
        .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
        .amount(
            IssuedCurrencyAmount.builder()
                .currency(usd)
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .value("25")
                .build()
        )
        .holder(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
        .asset(Issue.builder().currency(usd).issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd")).build())
        .asset2(Issue.builder().currency(usd).issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd")).build())
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(UnsignedInteger.valueOf(6))
        .signingPublicKey(
            PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
        )
        .flags(AmmClawbackFlags.CLAW_TWO_ASSETS)
        .build();

    String json = "{\n" +
        "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
        "    \"Amount\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
        "        \"value\" : \"25\"\n" +
        "    },\n" +
        "    \"Asset\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
        "    },\n" +
        "    \"Asset2\" : {\n" +
        "        \"currency\" : \"USD\",\n" +
        "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
        "    },\n" +
        "    \"Holder\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
        "    \"Flags\" : 1,\n" +
        "    \"Fee\" : \"10\",\n" +
        "    \"Sequence\" : 6,\n" +
        "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
        "    \"TransactionType\" : \"AMMClawback\"\n" +
        "}";

    assertCanSerializeAndDeserialize(ammClawback, json);
  }
}
