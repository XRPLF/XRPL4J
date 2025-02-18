package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

class ClawbackTest extends AbstractJsonTest {

  @Test
  void testJsonWithoutFlags() throws JSONException, JsonProcessingException {
    Clawback clawback = Clawback.builder()
      .account(Address.of("rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC"
      ))
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("FOO")
          .issuer(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
          .value("314.159")
          .build()
      )
      .build();

    String json = "{\n" +
      "  \"TransactionType\": \"Clawback\",\n" +
      "  \"Account\": \"rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S\",\n" +
      "  \"Amount\": {\n" +
      "      \"currency\": \"FOO\",\n" +
      "      \"issuer\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "      \"value\": \"314.159\"\n" +
      "    },\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
      "}";

    assertCanSerializeAndDeserialize(clawback, json);
  }

  @Test
  void testJsonWithZeroFlags() throws JSONException, JsonProcessingException {
    Clawback clawback = Clawback.builder()
      .account(Address.of("rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC"
      ))
      .flags(TransactionFlags.UNSET)
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("FOO")
          .issuer(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
          .value("314.159")
          .build()
      )
      .build();

    String json = "{\n" +
      "  \"TransactionType\": \"Clawback\",\n" +
      "  \"Account\": \"rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S\",\n" +
      "  \"Amount\": {\n" +
      "      \"currency\": \"FOO\",\n" +
      "      \"issuer\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "      \"value\": \"314.159\"\n" +
      "    },\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
      "}";

    assertCanSerializeAndDeserialize(clawback, json);
  }

  @Test
  void testJsonWithNonZeroFlags() throws JSONException, JsonProcessingException {
    Clawback clawback = Clawback.builder()
      .account(Address.of("rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC"
      ))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("FOO")
          .issuer(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
          .value("314.159")
          .build()
      )
      .build();

    String json = String.format("{\n" +
      "  \"TransactionType\": \"Clawback\",\n" +
      "  \"Account\": \"rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S\",\n" +
      "  \"Amount\": {\n" +
      "      \"currency\": \"FOO\",\n" +
      "      \"issuer\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "      \"value\": \"314.159\"\n" +
      "    },\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Flags\": %s,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG);

    assertCanSerializeAndDeserialize(clawback, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    Clawback clawback = Clawback.builder()
      .account(Address.of("rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC"
      ))
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("FOO")
          .issuer(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
          .value("314.159")
          .build()
      )
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"Clawback\",\n" +
      "  \"Account\": \"rp6abvbTbjoce8ZDJkT6snvxTZSYMBCC9S\",\n" +
      "  \"Amount\": {\n" +
      "      \"currency\": \"FOO\",\n" +
      "      \"issuer\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "      \"value\": \"314.159\"\n" +
      "    },\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
      "}";

    assertCanSerializeAndDeserialize(clawback, json);
  }
}