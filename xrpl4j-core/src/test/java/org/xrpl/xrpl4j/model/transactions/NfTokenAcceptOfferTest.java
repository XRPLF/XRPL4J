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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class NfTokenAcceptOfferTest {

  private final Address address = Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn");
  private final Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");

  @Test
  public void buildTx() {
    NfTokenAcceptOffer nfTokenAcceptOffer = NfTokenAcceptOffer.builder()
      .account(address)
      .fee(XrpCurrencyAmount.ofDrops(1))
      .buyOffer(offer)
      .build();

    Assertions.assertThat(offer.equals(nfTokenAcceptOffer.buyOffer()));
    assertThat(nfTokenAcceptOffer.buyOffer()).isEqualTo(Optional.of(offer));
    assertThat(nfTokenAcceptOffer.account().equals(address));
  }

  @Test
  public void throwWhenNoOfferIsPresent() {
    assertThatThrownBy(() -> NfTokenAcceptOffer.builder()
      .account(address)
      .brokerFee(XrpCurrencyAmount.ofDrops(1000))
      .fee(XrpCurrencyAmount.ofDrops(100))
      .build())
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Please specify one offer for direct mode and both offers for brokered mode.");
  }

  @Test
  public void directModeWithBrokerFee_throws() {
    assertThatThrownBy(() -> NfTokenAcceptOffer.builder()
      .account(address)
      .sellOffer(offer)
      .brokerFee(XrpCurrencyAmount.ofDrops(1000))
      .fee(XrpCurrencyAmount.ofDrops(100))
      .build())
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("No BrokerFee needed in direct mode.");
  }

  @Test
  public void brokeredModeWithAndWithoutFee() {
    NfTokenAcceptOffer nfTokenAcceptOffer = NfTokenAcceptOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .buyOffer(offer)
      .sellOffer(offer)
      .build();

    Assertions.assertThat(offer.equals(nfTokenAcceptOffer.buyOffer()));
    assertThat(nfTokenAcceptOffer.buyOffer()).isEqualTo(Optional.of(offer));

    nfTokenAcceptOffer = NfTokenAcceptOffer.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .buyOffer(offer)
      .sellOffer(offer)
      .brokerFee(XrpCurrencyAmount.ofDrops(10000))
      .build();
    Assertions.assertThat(offer.equals(nfTokenAcceptOffer.buyOffer()));
    assertThat(nfTokenAcceptOffer.buyOffer()).isEqualTo(Optional.of(offer));
  }

  @Test
  public void offerValueTooShort() {

    assertThrows(
      IllegalArgumentException.class,
      () -> NfTokenAcceptOffer.builder()
        .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
        .fee(XrpCurrencyAmount.ofDrops(1))
        .buyOffer(Hash256.of("offer"))
        .build(),
      "Hash256 Strings must be 64 characters long."
    );
  }

}
