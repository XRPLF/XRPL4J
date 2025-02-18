package org.xrpl.xrpl4j.model.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.AmmWithdrawFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class AmmWithdrawTest extends AbstractJsonTest {

  @Test
  void constructLpTokenWithdrawAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.LP_TOKEN)
      .lpTokensIn(lpTokensIn())
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"LPTokenIn\" : " + objectMapper.writeValueAsString(withdraw.lpTokensIn()) + "," +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructLpTokenWithdrawWithXrpCurrencyAmountAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.LP_TOKEN)
      .lpTokensIn(XrpCurrencyAmount.ofDrops(10))
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"LPTokenIn\" : \"10\"," +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructWithdrawAllAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.WITHDRAW_ALL)
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.WITHDRAW_ALL + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructTwoAssetAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.TWO_ASSET)
      .amount(amount())
      .amount2(XrpCurrencyAmount.ofDrops(50000000))
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"5\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"50000000\"," +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.TWO_ASSET + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructSingleAssetAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.SINGLE_ASSET)
      .amount(amount())
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"5\"\n" +
      "    },\n" +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.SINGLE_ASSET + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructOneAssetWithdrawAllAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.ONE_ASSET_WITHDRAW_ALL)
      .amount(amount())
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"5\"\n" +
      "    },\n" +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.ONE_ASSET_WITHDRAW_ALL + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructOneAssetLpTokenAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.ONE_ASSET_LP_TOKEN)
      .amount(amount())
      .lpTokensIn(lpTokensIn())
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"5\"\n" +
      "    },\n" +
      "    \"LPTokenIn\" : " + objectMapper.writeValueAsString(withdraw.lpTokensIn()) + "," +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.ONE_ASSET_LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void constructLimitLpTokenAndTestJson() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.LIMIT_LP_TOKEN)
      .amount(amount())
      .effectivePrice(amount())
      .build();

    String json = "{\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\",\n" +
      "        \"value\" : \"5\"\n" +
      "    },\n" +
      "    \"EPrice\" : " + objectMapper.writeValueAsString(withdraw.effectivePrice()) + "," +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.LIMIT_LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    AmmWithdraw withdraw = baseBuilder()
      .flags(AmmWithdrawFlags.LP_TOKEN)
      .lpTokensIn(lpTokensIn())
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\" : \"rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm\",\n" +
      "    \"LPTokenIn\" : " + objectMapper.writeValueAsString(withdraw.lpTokensIn()) + "," +
      "    \"Asset\" : " + objectMapper.writeValueAsString(withdraw.asset()) + "," +
      "    \"Asset2\" : " + objectMapper.writeValueAsString(withdraw.asset2()) + "," +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmWithdrawFlags.LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMWithdraw\"\n" +
      "}";

    assertCanSerializeAndDeserialize(withdraw, json);
  }

  private ImmutableIssuedCurrencyAmount amount() {
    return IssuedCurrencyAmount.builder()
      .currency("TST")
      .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
      .value("5")
      .build();
  }

  private ImmutableIssuedCurrencyAmount lpTokensIn() {
    return IssuedCurrencyAmount.builder()
      .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
      .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
      .value("100")
      .build();
  }

  private ImmutableAmmWithdraw.Builder baseBuilder() {
    return AmmWithdraw.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .asset(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      ).asset2(Issue.XRP);
  }
}