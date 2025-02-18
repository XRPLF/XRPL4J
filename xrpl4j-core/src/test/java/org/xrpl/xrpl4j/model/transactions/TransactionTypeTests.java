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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link TransactionType}.
 */
public class TransactionTypeTests {

  @ParameterizedTest
  @EnumSource
  public void shouldReturnTransactionTypeForValidValues(TransactionType type) {
    String value = type.value();
    TransactionType transactionType = TransactionType.forValue(value);
    assertNotNull(transactionType);
    assertTrue(transactionType instanceof TransactionType);
  }

  @EmptySource
  @NullSource
  @ParameterizedTest
  @ValueSource(strings = {"bla", "blaaa", "123"})
  public void shouldThrowIllegalArgumentExceptionForInvalidValues(String value) {
    assertThat(TransactionType.forValue(value)).isEqualTo(TransactionType.UNKNOWN);
  }

  @Test
  public void testTxTypeCapitalization() {
    assertThat(TransactionType.ACCOUNT_DELETE.value()).isEqualTo("AccountDelete");
    assertThat(TransactionType.ACCOUNT_SET.value()).isEqualTo("AccountSet");
    assertThat(TransactionType.CHECK_CANCEL.value()).isEqualTo("CheckCancel");
    assertThat(TransactionType.CHECK_CASH.value()).isEqualTo("CheckCash");
    assertThat(TransactionType.CHECK_CREATE.value()).isEqualTo("CheckCreate");
    assertThat(TransactionType.DEPOSIT_PRE_AUTH.value()).isEqualTo("DepositPreauth");
    assertThat(TransactionType.ENABLE_AMENDMENT.value()).isEqualTo("EnableAmendment");
    assertThat(TransactionType.ESCROW_CANCEL.value()).isEqualTo("EscrowCancel");
    assertThat(TransactionType.ESCROW_CREATE.value()).isEqualTo("EscrowCreate");
    assertThat(TransactionType.ESCROW_FINISH.value()).isEqualTo("EscrowFinish");
    assertThat(TransactionType.OFFER_CANCEL.value()).isEqualTo("OfferCancel");
    assertThat(TransactionType.OFFER_CREATE.value()).isEqualTo("OfferCreate");
    assertThat(TransactionType.PAYMENT.value()).isEqualTo("Payment");
    assertThat(TransactionType.PAYMENT_CHANNEL_CLAIM.value()).isEqualTo("PaymentChannelClaim");
    assertThat(TransactionType.PAYMENT_CHANNEL_CREATE.value()).isEqualTo("PaymentChannelCreate");
    assertThat(TransactionType.PAYMENT_CHANNEL_FUND.value()).isEqualTo("PaymentChannelFund");
    assertThat(TransactionType.SET_FEE.value()).isEqualTo("SetFee");
    assertThat(TransactionType.SET_REGULAR_KEY.value()).isEqualTo("SetRegularKey");
    assertThat(TransactionType.SIGNER_LIST_SET.value()).isEqualTo("SignerListSet");
    assertThat(TransactionType.TRUST_SET.value()).isEqualTo("TrustSet");
    assertThat(TransactionType.TICKET_CREATE.value()).isEqualTo("TicketCreate");
    assertThat(TransactionType.UNL_MODIFY.value()).isEqualTo("UNLModify");
    assertThat(TransactionType.CLAWBACK.value()).isEqualTo("Clawback");
    assertThat(TransactionType.AMM_BID.value()).isEqualTo("AMMBid");
    assertThat(TransactionType.AMM_CREATE.value()).isEqualTo("AMMCreate");
    assertThat(TransactionType.AMM_DEPOSIT.value()).isEqualTo("AMMDeposit");
    assertThat(TransactionType.AMM_VOTE.value()).isEqualTo("AMMVote");
    assertThat(TransactionType.AMM_WITHDRAW.value()).isEqualTo("AMMWithdraw");
    assertThat(TransactionType.AMM_DELETE.value()).isEqualTo("AMMDelete");
    assertThat(TransactionType.XCHAIN_ACCOUNT_CREATE_COMMIT.value()).isEqualTo("XChainAccountCreateCommit");
    assertThat(TransactionType.XCHAIN_ADD_ACCOUNT_CREATE_ATTESTATION.value())
      .isEqualTo("XChainAddAccountCreateAttestation");
    assertThat(TransactionType.XCHAIN_ADD_CLAIM_ATTESTATION.value()).isEqualTo("XChainAddClaimAttestation");
    assertThat(TransactionType.XCHAIN_CLAIM.value()).isEqualTo("XChainClaim");
    assertThat(TransactionType.XCHAIN_COMMIT.value()).isEqualTo("XChainCommit");
    assertThat(TransactionType.XCHAIN_CREATE_BRIDGE.value()).isEqualTo("XChainCreateBridge");
    assertThat(TransactionType.XCHAIN_CREATE_CLAIM_ID.value()).isEqualTo("XChainCreateClaimID");
    assertThat(TransactionType.XCHAIN_MODIFY_BRIDGE.value()).isEqualTo("XChainModifyBridge");
    assertThat(TransactionType.UNKNOWN.value()).isEqualTo("Unknown");
  }
}