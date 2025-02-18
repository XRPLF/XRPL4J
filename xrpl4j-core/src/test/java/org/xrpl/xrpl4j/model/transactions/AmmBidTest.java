package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.AuthAccount;
import org.xrpl.xrpl4j.model.ledger.AuthAccountWrapper;
import org.xrpl.xrpl4j.model.ledger.Issue;

class AmmBidTest extends AbstractJsonTest {

  @Test
  void testJsonWithoutMinAndMax() throws JSONException, JsonProcessingException {
    AmmBid bid = AmmBid.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
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
      "    \"AuthAccounts\" : [\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg\"\n" +
      "          }\n" +
      "        },\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv\"\n" +
      "          }\n" +
      "        }\n" +
      "    ],\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMBid\"\n" +
      "}";

    assertCanSerializeAndDeserialize(bid, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    AmmBid bid = AmmBid.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .flags(TransactionFlags.UNSET)
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
      "    \"AuthAccounts\" : [\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg\"\n" +
      "          }\n" +
      "        },\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv\"\n" +
      "          }\n" +
      "        }\n" +
      "    ],\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : 0,\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMBid\"\n" +
      "}";

    assertCanSerializeAndDeserialize(bid, json);
  }

  @Test
  void testJsonWithNonZeroFlags() throws JSONException, JsonProcessingException {
    AmmBid bid = AmmBid.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
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
      "    \"AuthAccounts\" : [\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg\"\n" +
      "          }\n" +
      "        },\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv\"\n" +
      "          }\n" +
      "        }\n" +
      "    ],\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : 0,\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"Flags\" : 2147483648,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMBid\"\n" +
      "}";

    assertCanSerializeAndDeserialize(bid, json);
  }

  @Test
  void testJsonWithMinAndMax() throws JSONException, JsonProcessingException {
    AmmBid bid = AmmBid.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .bidMax(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .bidMin(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
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
      "    \"AuthAccounts\" : [\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg\"\n" +
      "          }\n" +
      "        },\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv\"\n" +
      "          }\n" +
      "        }\n" +
      "    ],\n" +
      "    \"BidMax\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"BidMin\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMBid\"\n" +
      "}";

    assertCanSerializeAndDeserialize(bid, json);
  }

  /**
   * Test that ensures the problematic transaction found in <a
   * href="https://github.com/XRPLF/xrpl4j/issues/529">#529</a> is deserializable.
   */
  @Test
  void testJsonWithXrpAmountBidMinAndMax() throws JSONException, JsonProcessingException {
    AmmBid ammBid = AmmBid.builder()
      .account(Address.of("rammersz4CroiyvbkzeZN1sBDCK9P8DvxF"))
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rswh1fvyLqHizBS2awu1vs6QcmwTBd9qiv"))
          .currency("XAH")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(
          AuthAccount.of(Address.of("rapido5rxPmP4YkMZZEeXSHqWefxHEkqv6"))
        )
      )
      .bidMax(XrpCurrencyAmount.ofDrops(10))
      .bidMin(XrpCurrencyAmount.ofDrops(10))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .sequence(UnsignedInteger.valueOf(87704195))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED2D15BC6B61D6520011E4C794C5B320E584106154D0865BB095D70DA9A2A57B57")
      )
      .transactionSignature(
        Signature.fromBase16("F652BD5369F6EE9A8A1490BD37B8240CEE2B4B6EF94D22EC2DBB6912AA729B829" +
          "FC3D7E24B30A1E6CC11F868CE229B105398719152B9BEE8992A56D654F79C0A")
      )
      .build();
    String json = "{\n" +
      "        \"Account\": \"rammersz4CroiyvbkzeZN1sBDCK9P8DvxF\",\n" +
      "        \"Asset\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "        },\n" +
      "        \"Asset2\": {\n" +
      "            \"currency\": \"XAH\",\n" +
      "            \"issuer\": \"rswh1fvyLqHizBS2awu1vs6QcmwTBd9qiv\"\n" +
      "        },\n" +
      "        \"AuthAccounts\": [\n" +
      "            {\n" +
      "                \"AuthAccount\": {\n" +
      "                    \"Account\": \"rapido5rxPmP4YkMZZEeXSHqWefxHEkqv6\"\n" +
      "                }\n" +
      "            }\n" +
      "        ],\n" +
      "        \"BidMax\": \"10\",\n" +
      "        \"BidMin\": \"10\",\n" +
      "        \"Fee\": \"10\",\n" +
      "        \"Flags\": 2147483648,\n" +
      "        \"Sequence\": 87704195,\n" +
      "        \"SigningPubKey\": \"ED2D15BC6B61D6520011E4C794C5B320E584106154D0865BB095D70DA9A2A57B57\",\n" +
      "        \"TransactionType\": \"AMMBid\",\n" +
      "        \"TxnSignature\": \"F652BD5369F6EE9A8A1490BD37B8240CEE2B4B6EF94D22EC2DBB6912AA729B829FC3D7E24B30A" +
      "1E6CC11F868CE229B105398719152B9BEE8992A56D654F79C0A\"\n" +
      "}";
    assertCanSerializeAndDeserialize(ammBid, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    AmmBid bid = AmmBid.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
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
      "    \"AuthAccounts\" : [\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg\"\n" +
      "          }\n" +
      "        },\n" +
      "        {\n" +
      "          \"AuthAccount\" : {\n" +
      "              \"Account\" : \"rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv\"\n" +
      "          }\n" +
      "        }\n" +
      "    ],\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Sequence\" : 9,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMBid\"\n" +
      "}";

    assertCanSerializeAndDeserialize(bid, json);
  }
}