package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

class AmmCreateTest extends AbstractJsonTest {

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .value("25")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(250000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(6))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500)))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"25\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"250000000\",\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 6,\n" +
      "    \"TradingFee\" : 500,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMCreate\"\n" +
      "}";

    assertCanSerializeAndDeserialize(ammCreate, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .value("25")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(250000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(6))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500)))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"25\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"250000000\",\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 6,\n" +
      "    \"TradingFee\" : 500,\n" +
      "    \"Flags\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMCreate\"\n" +
      "}";

    assertCanSerializeAndDeserialize(ammCreate, json);
  }

  @Test
  void testJsonWithNonZeroFlags() throws JSONException, JsonProcessingException {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .value("25")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(250000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(6))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500)))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"25\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"250000000\",\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 6,\n" +
      "    \"TradingFee\" : 500,\n" +
      "    \"Flags\" : 2147483648,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMCreate\"\n" +
      "}";

    assertCanSerializeAndDeserialize(ammCreate, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .value("25")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(250000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(6))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500)))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"25\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"250000000\",\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 6,\n" +
      "    \"TradingFee\" : 500,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMCreate\"\n" +
      "}";

    assertCanSerializeAndDeserialize(ammCreate, json);
  }
}