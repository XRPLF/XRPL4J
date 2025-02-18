package org.xrpl.xrpl4j.model.transactions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.AmmDepositFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class AmmDepositTest extends AbstractJsonTest {

  @Test
  void constructLpTokenDepositAndTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.LP_TOKEN)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .lpTokenOut(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      ).build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.LP_TOKEN);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"LPTokenOut\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void constructLpTokenDepositWithXrpLpTokenAmountAndTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.LP_TOKEN)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .lpTokenOut(XrpCurrencyAmount.ofDrops(10))
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.LP_TOKEN);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"LPTokenOut\" : \"10\",\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void constructTwoAssetDepositAndTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.TWO_ASSET)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(10))
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.TWO_ASSET);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"10\"," +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.TWO_ASSET + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void constructSingleAssetDepositAndTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(AmmDepositFlags.SINGLE_ASSET)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.SINGLE_ASSET);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.SINGLE_ASSET + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void constructOneAssetLpTokenDepositAndTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.ONE_ASSET_LP_TOKEN)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .lpTokenOut(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.ONE_ASSET_LP_TOKEN);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"LPTokenOut\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.ONE_ASSET_LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void constructLimitLpTokenDepositAndTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.LIMIT_LP_TOKEN)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .effectivePrice(XrpCurrencyAmount.ofDrops(10))
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.LIMIT_LP_TOKEN);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"EPrice\" : \"10\",\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.LIMIT_LP_TOKEN + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void constructTwoAssetIfEmptyDepositTestJson() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.TWO_ASSET_IF_EMPTY)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(10))
      .effectivePrice(XrpCurrencyAmount.ofDrops(10))
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.TWO_ASSET_IF_EMPTY);

    String json = "{\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"10\",\n" +
      "    \"EPrice\" : \"10\",\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.TWO_ASSET_IF_EMPTY + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(AmmDepositFlags.TWO_ASSET_IF_EMPTY)
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("039C99CD9AB0B70B32ECDA51EAAE471625608EA2")
          .issuer(Address.of("rE54zDvgnghAoPopCgvtiqWNq3dU5y836S"))
          .value("100")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(10))
      .effectivePrice(XrpCurrencyAmount.ofDrops(10))
      .putUnknownFields("Foo", "Bar")
      .build();

    assertThat(deposit.flags()).isEqualTo(AmmDepositFlags.TWO_ASSET_IF_EMPTY);

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\" : \"" + deposit.account() + "\",\n" +
      "    \"Amount\" : {\n" +
      "        \"currency\" : \"039C99CD9AB0B70B32ECDA51EAAE471625608EA2\",\n" +
      "        \"issuer\" : \"rE54zDvgnghAoPopCgvtiqWNq3dU5y836S\",\n" +
      "        \"value\" : \"100\"\n" +
      "    },\n" +
      "    \"Amount2\" : \"10\",\n" +
      "    \"EPrice\" : \"10\",\n" +
      "    \"Asset2\" : {\n" +
      "        \"currency\" : \"TST\",\n" +
      "        \"issuer\" : \"rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd\"\n" +
      "    },\n" +
      "    \"Asset\" : {\n" +
      "        \"currency\" : \"XRP\"\n" +
      "    },\n" +
      "    \"Fee\" : \"10\",\n" +
      "    \"Flags\" : " + AmmDepositFlags.TWO_ASSET_IF_EMPTY + ",\n" +
      "    \"Sequence\" : 0,\n" +
      "    \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "    \"TransactionType\" : \"AMMDeposit\"\n" +
      "}";

    assertCanSerializeAndDeserialize(deposit, json);
  }
}