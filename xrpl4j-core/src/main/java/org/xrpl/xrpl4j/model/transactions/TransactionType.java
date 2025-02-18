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

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.annotations.Beta;

/**
 * Enumeration of the types of Transactions on the XRP Ledger.
 */
public enum TransactionType {

  /**
   * The {@link TransactionType} for the {@link AccountSet} transaction.
   */
  ACCOUNT_SET("AccountSet"),

  /**
   * The {@link TransactionType} for the {@link AccountDelete} transaction.
   */
  ACCOUNT_DELETE("AccountDelete"),

  /**
   * The {@link TransactionType} for the {@link CheckCancel} transaction.
   */
  CHECK_CANCEL("CheckCancel"),

  /**
   * The {@link TransactionType} for the {@link CheckCash} transaction.
   */
  CHECK_CASH("CheckCash"),

  /**
   * The {@link TransactionType} for the {@link CheckCreate} transaction.
   */
  CHECK_CREATE("CheckCreate"),

  /**
   * The {@link TransactionType} for the {@link DepositPreAuth} transaction.
   */
  DEPOSIT_PRE_AUTH("DepositPreauth"),

  /**
   * The {@link TransactionType} for the {@link EnableAmendment} transaction.
   */
  ENABLE_AMENDMENT("EnableAmendment"),

  /**
   * The {@link TransactionType} for the {@link EscrowCancel} transaction.
   */
  ESCROW_CANCEL("EscrowCancel"),

  /**
   * The {@link TransactionType} for the {@link EscrowCreate} transaction.
   */
  ESCROW_CREATE("EscrowCreate"),

  /**
   * The {@link TransactionType} for the {@link EscrowFinish} transaction.
   */
  ESCROW_FINISH("EscrowFinish"),

  /**
   * The {@link TransactionType} for the {@link NfTokenMint} transaction.
   */
  NFTOKEN_MINT("NFTokenMint"),

  /**
   * The {@link TransactionType} for the {@link NfTokenBurn} transaction.
   */
  NFTOKEN_BURN("NFTokenBurn"),

  /**
   * The {@link TransactionType} for the {@link NfTokenAcceptOffer} transaction.
   */
  NFTOKEN_ACCEPT_OFFER("NFTokenAcceptOffer"),

  /**
   * The {@link TransactionType} for the {@link NfTokenCancelOffer} transaction.
   */
  NFTOKEN_CANCEL_OFFER("NFTokenCancelOffer"),

  /**
   * The {@link TransactionType} for the {@link NfTokenCreateOffer} transaction.
   */
  NFTOKEN_CREATE_OFFER("NFTokenCreateOffer"),

  /**
   * The {@link TransactionType} for the {@link OfferCancel} transaction.
   */
  OFFER_CANCEL("OfferCancel"),

  /**
   * The {@link TransactionType} for the {@link OfferCreate} transaction.
   */
  OFFER_CREATE("OfferCreate"),

  /**
   * The {@link TransactionType} for the {@link Payment} transaction.
   */
  PAYMENT("Payment"),

  /**
   * The {@link TransactionType} for the {@link PaymentChannelClaim} transaction.
   */
  PAYMENT_CHANNEL_CLAIM("PaymentChannelClaim"),

  /**
   * The {@link TransactionType} for the {@link PaymentChannelCreate} transaction.
   */
  PAYMENT_CHANNEL_CREATE("PaymentChannelCreate"),

  /**
   * The {@link TransactionType} for the {@link PaymentChannelFund} transaction.
   */
  PAYMENT_CHANNEL_FUND("PaymentChannelFund"),

  /**
   * The {@link TransactionType} for the {@link SetFee} transaction.
   */
  SET_FEE("SetFee"),

  /**
   * The {@link TransactionType} for the {@link SetRegularKey} transaction.
   */
  SET_REGULAR_KEY("SetRegularKey"),

  /**
   * The {@link TransactionType} for the {@link SignerListSet} transaction.
   */
  SIGNER_LIST_SET("SignerListSet"),

  /**
   * The {@link TransactionType} for the {@link TrustSet} transaction.
   */
  TRUST_SET("TrustSet"),

  /**
   * The {@link TransactionType} for the {@link TicketCreate} transaction.
   */
  TICKET_CREATE("TicketCreate"),

  /**
   * The {@link TransactionType} for the {@link UnlModify} transaction.
   */
  UNL_MODIFY("UNLModify"),

  /**
   * The {@link TransactionType} for the {@link Clawback} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the Clawback amendment is enabled on mainnet. Its API is
   * subject
   * to change.</p>
   */
  @Beta
  CLAWBACK("Clawback"),

  /**
   * The {@link TransactionType} for the {@link AmmBid} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
   * change.</p>
   */
  @Beta
  AMM_BID("AMMBid"),

  /**
   * The {@link TransactionType} for the {@link AmmCreate} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
   * change.</p>
   */
  @Beta
  AMM_CREATE("AMMCreate"),

  /**
   * The {@link TransactionType} for the {@link AmmDeposit} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
   * change.</p>
   */
  @Beta
  AMM_DEPOSIT("AMMDeposit"),

  /**
   * The {@link TransactionType} for the {@link AmmVote} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
   * change.</p>
   */
  @Beta
  AMM_VOTE("AMMVote"),

  /**
   * The {@link TransactionType} for the {@link AmmWithdraw} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
   * change.</p>
   */
  @Beta
  AMM_WITHDRAW("AMMWithdraw"),

  /**
   * The {@link TransactionType} for the {@link AmmDelete} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the AMM amendment is enabled on mainnet. Its API is subject to
   * change.</p>
   */
  @Beta
  AMM_DELETE("AMMDelete"),

  /**
   * The {@link TransactionType} for the {@link XChainAccountCreateCommit} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to
   * change.</p>
   */
  @Beta
  XCHAIN_ACCOUNT_CREATE_COMMIT("XChainAccountCreateCommit"),

  /**
   * The {@link TransactionType} for the {@link XChainAddAccountCreateAttestation} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_ADD_ACCOUNT_CREATE_ATTESTATION("XChainAddAccountCreateAttestation"),

  /**
   * The {@link TransactionType} for the {@link XChainAddClaimAttestation} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_ADD_CLAIM_ATTESTATION("XChainAddClaimAttestation"),


  /**
   * The {@link TransactionType} for the {@link XChainClaim} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_CLAIM("XChainClaim"),


  /**
   * The {@link TransactionType} for the {@link XChainCommit} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_COMMIT("XChainCommit"),

  /**
   * The {@link TransactionType} for the {@link XChainCreateBridge} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_CREATE_BRIDGE("XChainCreateBridge"),

  /**
   * The {@link TransactionType} for the {@link XChainCreateClaimId} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_CREATE_CLAIM_ID("XChainCreateClaimID"),

  /**
   * The {@link TransactionType} for the {@link XChainModifyBridge} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureXChainBridge amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  XCHAIN_MODIFY_BRIDGE("XChainModifyBridge"),

  /**
   * The {@link TransactionType} for the {@link DidSet} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureDID amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  DID_SET("DIDSet"),

  /**
   * The {@link TransactionType} for the {@link DidDelete} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featureDID amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  DID_DELETE("DIDDelete"),

  /**
   * The {@link TransactionType} for the {@link OracleSet} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featurePriceOracle amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  ORACLE_SET("OracleSet"),

  /**
   * The {@link TransactionType} for the {@link OracleDelete} transaction.
   *
   * <p>This constant will be marked {@link Beta} until the featurePriceOracle amendment is enabled on mainnet. Its API
   * is subject to change.</p>
   */
  @Beta
  ORACLE_DELETE("OracleDelete"),

  /**
   * The {@link TransactionType} for any transaction that is unrecognized/unsupported by xrpl4j.
   */
  UNKNOWN("Unknown");

  private final String value;

  TransactionType(String value) {
    this.value = value;
  }

  /**
   * Gets an instance of {@link TransactionType} for the given string value.
   *
   * @param value The {@link String} value corresponding to a {@link TransactionType}.
   *
   * @return The {@link TransactionType} with the corresponding value.
   */
  public static TransactionType forValue(String value) {
    for (TransactionType transactionType : TransactionType.values()) {
      if (transactionType.value.equals(value)) {
        return transactionType;
      }
    }

    return UNKNOWN;
  }

  /**
   * Get the underlying value of this {@link TransactionType}.
   *
   * @return The {@link String} value.
   */
  @JsonValue
  public String value() {
    return value;
  }
}
