package org.xrpl.xrpl4j.model.client.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;

/**
 * Unit tests for {@link ServerInfoLedger}.
 */
class ServerInfoLedgerTest extends AbstractJsonTest {

  @Test
  public void testJsonReservesWithOnlyNonDeprecated() throws JsonProcessingException, JSONException {
    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) // "base_fee_xrp":1E-5,
      .reserveBaseAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(5)))
      .reserveIncAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(2)))
      //.reserveBaseXrp(UnsignedInteger.valueOf(5))
      //.reserveIncXrp(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 5,\n" +
      "        \"reserve_inc_xrp\": 2\n" +
      "      }";

    assertCanSerializeAndDeserialize(result, json);
  }

  @Test
  public void testJsonReservesWithOnlyDeprecated() throws JsonProcessingException, JSONException {
    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) //"base_fee_xrp":1E-5,
      //.reserveBaseAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(5)))
      //.reserveIncAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(2)))
      .reserveBaseXrp(UnsignedInteger.valueOf(5))
      .reserveIncXrp(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 5,\n" +
      "        \"reserve_inc_xrp\": 2\n" +
      "      }";

    assertCanSerializeAndDeserialize(result, json);
  }


  @Test
  public void testJsonReservesWithBoth() throws JsonProcessingException, JSONException {
    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) //"base_fee_xrp":1E-5,
      .reserveBaseAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(5)))
      .reserveIncAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(2)))
      .reserveBaseXrp(UnsignedInteger.valueOf(5))
      .reserveIncXrp(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 5,\n" +
      "        \"reserve_inc_xrp\": 2\n" +
      "      }";

    assertCanSerializeAndDeserialize(result, json);
  }

  @Test
  public void testJsonReservesWithZero() throws JsonProcessingException, JSONException {
    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) //"base_fee_xrp":1E-5,
      .reserveBaseAsXrp(XrpCurrencyAmount.of(UnsignedLong.ZERO))
      .reserveIncAsXrp(XrpCurrencyAmount.of(UnsignedLong.ZERO))
      .build();

    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 0,\n" +
      "        \"reserve_inc_xrp\": 0\n" +
      "      }";

    assertCanSerializeAndDeserialize(result, json);
  }

  @Test
  public void testJsonReservesWithOneDrop() throws JsonProcessingException, JSONException {
    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) //"base_fee_xrp":1E-5,
      .reserveBaseAsXrp(XrpCurrencyAmount.of(UnsignedLong.ONE))
      .reserveIncAsXrp(XrpCurrencyAmount.of(UnsignedLong.ONE))
      .build();

    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 0.000001,\n" +
      "        \"reserve_inc_xrp\": 0.000001\n" +
      "      }";

    assertCanSerializeAndDeserialize(result, json);
  }

  @Test
  public void testJsonReservesWithMaxXrpValues() throws JsonProcessingException, JSONException {
    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) //"base_fee_xrp":1E-5,
      .reserveBaseAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(UnsignedInteger.MAX_VALUE.longValue())))
      .reserveIncAsXrp(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(UnsignedInteger.MAX_VALUE.longValue())))
      .build();

    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 4294967295,\n" +
      "        \"reserve_inc_xrp\": 4294967295\n" +
      "      }";

    assertCanSerializeAndDeserialize(result, json);
  }

  @Test
  public void testJsonReservesWithScientificNotation() throws JsonProcessingException, JSONException {
    String json = "{\n" +
      "        \"age\": 2,\n" +
      "        \"seq\": 1,\n" +
      "        \"hash\": \"0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9\",\n" +
      "        \"base_fee_xrp\": 0.00001,\n" +
      "        \"reserve_base_xrp\": 1E1,\n" +
      "        \"reserve_inc_xrp\": 2E0\n" +
      "      }";

    ServerInfoLedger result = ServerInfoLedger.builder()
      .age(UnsignedInteger.valueOf(2))
      .sequence(LedgerIndex.of(UnsignedInteger.ONE))
      .hash(Hash256.of("0D2D30837E05995AAAAA117294BB45AB0699AB1219605FFD23318E050C7166E9"))
      .baseFeeXrp(new BigDecimal("0.00001")) //"base_fee_xrp":1E-5,
      .reserveBaseAsXrp(XrpCurrencyAmount.ofDrops(10000000))
      .reserveIncAsXrp(XrpCurrencyAmount.ofDrops(2000000))
      .build();

    assertCanSerializeAndDeserialize(result, json);
  }
}