package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;

import java.util.Optional;

/**
 * Unit tests for {@link SetFee}.
 */
public class SetFeeTest extends AbstractJsonTest {

  @Test
  public void testConstructWithNoFeeUnits() {
    SetFee setFee = SetFee.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(2470665))
      .baseFeeDrops(XrpCurrencyAmount.ofDrops(10))
      .reserveBaseDrops(XrpCurrencyAmount.ofDrops(20000000))
      .reserveIncrementDrops(XrpCurrencyAmount.ofDrops(5000000))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(67850752))))
      .build();

    assertThat(setFee.transactionType()).isEqualTo(TransactionType.SET_FEE);
    assertThat(setFee.account()).isEqualTo(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"));
    assertThat(setFee.fee().value()).isEqualTo(UnsignedLong.valueOf(12));
    assertThat(setFee.sequence()).isEqualTo(UnsignedInteger.valueOf(2470665));
    assertThat(setFee.ledgerSequence()).isNotEmpty().get().isEqualTo(LedgerIndex.of(UnsignedInteger.valueOf(67850752)));
    assertThat(setFee.baseFee()).isEqualTo("a");
    assertThat(setFee.baseFeeDrops()).isEqualTo(XrpCurrencyAmount.ofDrops(10));
    assertThat(setFee.referenceFeeUnits()).isEmpty();
    assertThat(setFee.reserveIncrement()).isEqualTo(UnsignedInteger.valueOf(5000000));
    assertThat(setFee.reserveIncrementDrops()).isEqualTo(XrpCurrencyAmount.ofDrops(5000000));
    assertThat(setFee.reserveBase()).isEqualTo(UnsignedInteger.valueOf(20000000));
    assertThat(setFee.reserveBaseDrops()).isEqualTo(XrpCurrencyAmount.ofDrops(20000000));
  }

  @Test
  public void testConstructWithFeeUnits() {
    SetFee setFee = SetFee.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(2470665))
      .baseFeeDrops(XrpCurrencyAmount.ofDrops(10))
      .reserveBaseDrops(XrpCurrencyAmount.ofDrops(20000000))
      .reserveIncrementDrops(XrpCurrencyAmount.ofDrops(5000000))
      .referenceFeeUnits(UnsignedInteger.valueOf(10))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(67850752))))
      .build();

    assertThat(setFee.transactionType()).isEqualTo(TransactionType.SET_FEE);
    assertThat(setFee.account()).isEqualTo(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"));
    assertThat(setFee.fee().value()).isEqualTo(UnsignedLong.valueOf(12));
    assertThat(setFee.sequence()).isEqualTo(UnsignedInteger.valueOf(2470665));
    assertThat(setFee.ledgerSequence()).isNotEmpty().get().isEqualTo(LedgerIndex.of(UnsignedInteger.valueOf(67850752)));
    assertThat(setFee.baseFee()).isEqualTo("a");
    assertThat(setFee.baseFeeDrops()).isEqualTo(XrpCurrencyAmount.ofDrops(10));
    assertThat(setFee.referenceFeeUnits()).isNotEmpty().get().isEqualTo(UnsignedInteger.valueOf(10));
    assertThat(setFee.reserveIncrement()).isEqualTo(UnsignedInteger.valueOf(5000000));
    assertThat(setFee.reserveIncrementDrops()).isEqualTo(XrpCurrencyAmount.ofDrops(5000000));
    assertThat(setFee.reserveBase()).isEqualTo(UnsignedInteger.valueOf(20000000));
    assertThat(setFee.reserveBaseDrops()).isEqualTo(XrpCurrencyAmount.ofDrops(20000000));
  }

  @Test
  public void testDeserializePreXrpFeesTransaction() throws JsonProcessingException {
    SetFee expected = SetFee.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(2470665))
      .baseFeeDrops(XrpCurrencyAmount.ofDrops(10))
      .referenceFeeUnits(UnsignedInteger.valueOf(10))
      .reserveBaseDrops(XrpCurrencyAmount.ofDrops(20000000))
      .reserveIncrementDrops(XrpCurrencyAmount.ofDrops(5000000))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(67850752))))
      .build();

    String json = "{" +
      "\"Account\":\"rrrrrrrrrrrrrrrrrrrrrhoLvTp\"," +
      "\"Fee\":\"12\"," +
      "\"LedgerSequence\":67850752," +
      "\"Sequence\":2470665," +
      "\"SigningPubKey\":\"\"," +
      "\"TransactionType\":\"SetFee\"," +
      "\"ReserveIncrement\":5000000," +
      "\"ReserveBase\":20000000," +
      "\"ReferenceFeeUnits\":10," +
      "\"BaseFee\":\"a\"}";

    Transaction actual = objectMapper.readValue(json, Transaction.class);
    assertThat(actual).isEqualTo(expected);

    String reserialized = objectMapper.writeValueAsString(actual);
    Transaction redeserialized = objectMapper.readValue(reserialized, Transaction.class);

    assertThat(redeserialized).isEqualTo(expected);
  }

  @Test
  public void testDeserializePostXrpFeesTransaction() throws JsonProcessingException {
    SetFee expected = SetFee.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(0))
      .sequence(UnsignedInteger.valueOf(0))
      .baseFeeDrops(XrpCurrencyAmount.ofDrops(10))
      .reserveBaseDrops(XrpCurrencyAmount.ofDrops(10000000))
      .reserveIncrementDrops(XrpCurrencyAmount.ofDrops(2000000))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(66462465))))
      .build();

    String json = "{\n" +
      "    \"Account\": \"rrrrrrrrrrrrrrrrrrrrrhoLvTp\",\n" +
      "    \"BaseFeeDrops\": \"10\",\n" +
      "    \"Fee\": \"0\",\n" +
      "    \"LedgerSequence\": 66462465,\n" +
      "    \"ReserveBaseDrops\": \"10000000\",\n" +
      "    \"ReserveIncrementDrops\": \"2000000\",\n" +
      "    \"Sequence\": 0,\n" +
      "    \"SigningPubKey\": \"\",\n" +
      "    \"TransactionType\": \"SetFee\"}";

    Transaction actual = objectMapper.readValue(json, Transaction.class);
    assertThat(actual).isEqualTo(expected);

    String reserialized = objectMapper.writeValueAsString(actual);
    Transaction redeserialized = objectMapper.readValue(reserialized, Transaction.class);

    assertThat(redeserialized).isEqualTo(expected);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException {
    SetFee expected = SetFee.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(0))
      .sequence(UnsignedInteger.valueOf(0))
      .baseFeeDrops(XrpCurrencyAmount.ofDrops(10))
      .reserveBaseDrops(XrpCurrencyAmount.ofDrops(10000000))
      .reserveIncrementDrops(XrpCurrencyAmount.ofDrops(2000000))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(66462465))))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "    \"Foo\" : \"Bar\",\n" +
      "    \"Account\": \"rrrrrrrrrrrrrrrrrrrrrhoLvTp\",\n" +
      "    \"BaseFeeDrops\": \"10\",\n" +
      "    \"Fee\": \"0\",\n" +
      "    \"LedgerSequence\": 66462465,\n" +
      "    \"ReserveBaseDrops\": \"10000000\",\n" +
      "    \"ReserveIncrementDrops\": \"2000000\",\n" +
      "    \"Sequence\": 0,\n" +
      "    \"SigningPubKey\": \"\",\n" +
      "    \"TransactionType\": \"SetFee\"}";

    Transaction actual = objectMapper.readValue(json, Transaction.class);
    assertThat(actual).isEqualTo(expected);

    String reserialized = objectMapper.writeValueAsString(actual);
    Transaction redeserialized = objectMapper.readValue(reserialized, Transaction.class);

    assertThat(redeserialized).isEqualTo(expected);
  }
}
