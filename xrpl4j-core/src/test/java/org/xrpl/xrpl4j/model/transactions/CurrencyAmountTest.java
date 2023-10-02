package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: model
 * %%
 * Copyright (C) 2020 - 2022 XRPL Foundation and its contributors
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.codec.addresses.ByteUtils;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;

import java.nio.charset.StandardCharsets;

/**
 * Unit tests for {@link CurrencyAmount}.
 */
public class CurrencyAmountTest {

  @Test
  public void handleXrp() {
    XrpCurrencyAmount xrpCurrencyAmount = XrpCurrencyAmount.ofDrops(0L);

    xrpCurrencyAmount.handle(
      ($) -> assertThat($.value()).isEqualTo(UnsignedLong.ZERO),
      ($) -> fail()
    );

    // null xrpCurrencyAmountHandler
    assertThrows(NullPointerException.class, () ->
      xrpCurrencyAmount.handle(null, ($) -> new Object())
    );

    // null issuedCurrencyAmountConsumer
    assertThrows(NullPointerException.class, () ->
      xrpCurrencyAmount.handle(($) -> new Object(), null)
    );

    // Unhandled...
    CurrencyAmount currencyAmount = new CurrencyAmount() {
    };
    assertThrows(IllegalStateException.class, () ->
      currencyAmount.handle(($) -> new Object(), ($) -> new Object())
    );
  }


  @Test
  public void handleIssuance() {
    final IssuedCurrencyAmount issuedCurrencyAmount = IssuedCurrencyAmount.builder()
      .issuer(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .currency("USD")
      .value("100")
      .build();

    issuedCurrencyAmount.handle(
      ($) -> fail(),
      ($) -> assertThat($.value()).isEqualTo("100")
    );

    // null xrpCurrencyAmountHandler
    assertThrows(NullPointerException.class, () ->
      issuedCurrencyAmount.handle(null, ($) -> new Object())
    );
    // null issuedCurrencyAmountConsumer
    assertThrows(NullPointerException.class, () ->
      issuedCurrencyAmount.handle(($) -> new Object(), null)
    );
  }

  @Test
  public void mapXrp() {
    XrpCurrencyAmount xrpCurrencyAmount = XrpCurrencyAmount.ofDrops(0L);

    String actual = xrpCurrencyAmount.map(
      ($) -> "success",
      ($) -> "fail"
    );
    assertThat(actual).isEqualTo("success");

    // null xrpCurrencyAmountHandler
    assertThrows(NullPointerException.class, () ->
      xrpCurrencyAmount.map(null, ($) -> new Object())
    );
    // null issuedCurrencyAmountConsumer
    assertThrows(NullPointerException.class, () ->
      xrpCurrencyAmount.map(($) -> new Object(), null)
    );

    // Unhandled...
    CurrencyAmount currencyAmount = new CurrencyAmount() {
    };
    assertThrows(IllegalStateException.class, () ->
      currencyAmount.map(($) -> new Object(), ($) -> new Object())
    );
  }

  @Test
  public void mapIssuance() {
    final IssuedCurrencyAmount issuedCurrencyAmount = IssuedCurrencyAmount.builder()
      .issuer(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .currency("USD")
      .value("100")
      .build();

    String actual = issuedCurrencyAmount.map(
      ($) -> "fail",
      ($) -> "success"
    );
    assertThat(actual).isEqualTo("success");

    // null xrpCurrencyAmountHandler
    assertThrows(NullPointerException.class, () ->
      issuedCurrencyAmount.map(null, ($) -> new Object())
    );
    // null issuedCurrencyAmountConsumer
    assertThrows(NullPointerException.class, () ->
      issuedCurrencyAmount.map(($) -> new Object(), null)
    );
  }

  /**
   * Verify that xrpl4j is unaffected by the bug reported in rippled issue #4112. When rippled APIs are provided
   * 3-character currency codes, those APIs will upper-case the supplied currency values. Only after that normalization
   * will those APIs then convert to binary. For example, if a request is made to rippled to sign a payload with a
   * currency code of `UsD`, the API layer will normalize this value to `USD` (i.e., all-caps) before signing. However,
   * tooling (like xrpl4j) is not forced to do this kind of upper-case normalization. So, it's possible for any tooling
   * (like xrpl4j) to unintentionally allow issuers to issue mixed-case, 3-character currency codes. However, there's
   * debate in the GH issue linked below about whether this is a bug in tooling (like xrpl4j), or if this is actually a
   * bug in the rippled code base (in which case, the normalization functionality should be removed from rippled, and
   * tooling should do nothing). Contributors to the issue assert the latter -- i.e., it's a bug in rippled and should
   * be removed from rippled. There is also PR to this effect. Thus, this test ensures that xrpl4j tooling does the
   * correct thing (i.e., no currency code normalization, either in our Transaction layer or in the binary codec).
   *
   * @see "https://github.com/XRPLF/rippled/issues/4112"
   */
  @Test
  public void buildIssuanceWithMixedCaseThreeCharacterCode() {
    final IssuedCurrencyAmount issuedCurrencyAmount = IssuedCurrencyAmount.builder()
      .issuer(Address.of("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59Ba"))
      .currency("UsD")
      .value("100")
      .build();

    assertThat(issuedCurrencyAmount.currency()).isEqualTo("UsD");
  }

  @Test
  public void encodeDecodeMixedCaseCurrencyCode() throws JsonProcessingException {
    currencyTestHelper("Usd");
    currencyTestHelper("UsD");
    currencyTestHelper("USD");
    currencyTestHelper("$GHOST");
    currencyTestHelper("$ghost");
    currencyTestHelper("$ghosT");
  }

  /**
   * Helper method to test various currencies codes for capitalization.
   *
   * @param currencyCode A {@link String} representing a currency code.
   */
  private void currencyTestHelper(String currencyCode) throws JsonProcessingException {
    if (currencyCode.length() > 3) {
      currencyCode = ByteUtils.padded(
        BaseEncoding.base16().encode(currencyCode.getBytes(StandardCharsets.US_ASCII)),
        40 // <-- Non-standard currency codes must be 40 bytes.
      );
    }

    final CurrencyAmount issuedCurrencyAmountMixed = IssuedCurrencyAmount.builder()
      .issuer(Address.of("rPx8CtHbTkjYbQzrwfDxXfPfLHV9nbjYBz"))
      .currency(currencyCode)
      .value("100")
      .build();

    Payment payment = Payment.builder()
      .account(Address.of("rPx8CtHbTkjYbQzrwfDxXfPfLHV9nbjYBz"))
      .destination(Address.of("rPx8CtHbTkjYbQzrwfDxXfPfLHV9nbjYBz"))
      .amount(issuedCurrencyAmountMixed)
      .fee(XrpCurrencyAmount.of(UnsignedLong.ONE))
      .build();

    String transactionJson = ObjectMapperFactory.create().writeValueAsString(payment);
    String transactionBinary = XrplBinaryCodec.getInstance().encode(transactionJson);
    String decodedTransactionJson = XrplBinaryCodec.getInstance().decode(transactionBinary);
    Payment decodedPayment = ObjectMapperFactory.create().readValue(decodedTransactionJson, Payment.class);

    final String finalCurrencyCode = currencyCode;
    decodedPayment.amount().handle(
      xrpCurrencyAmount -> fail(),
      issuedCurrencyAmount -> assertThat(issuedCurrencyAmount.currency()).isEqualTo(finalCurrencyCode)
    );
  }

  @Test
  void testConstants() {
    assertThat(CurrencyAmount.ONE_XRP_IN_DROPS).isEqualTo(1_000_000L);
    assertThat(CurrencyAmount.MAX_XRP).isEqualTo(100_000_000_000L);
    assertThat(CurrencyAmount.MAX_XRP_IN_DROPS).isEqualTo(100_000_000_000_000_000L);
  }
}
