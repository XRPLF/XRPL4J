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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides an abstract interface for all concrete XRPL transactions.
 */
public interface Transaction {

  /**
   * A bidirectional map of immutable transaction types to their corresponding {@link TransactionType}.
   *
   * <p>This is useful for polymorphic Jackson deserialization.
   */
  BiMap<Class<? extends Transaction>, TransactionType> typeMap =
    new ImmutableBiMap.Builder<Class<? extends Transaction>, TransactionType>()
      .put(ImmutableAccountSet.class, TransactionType.ACCOUNT_SET)
      .put(ImmutableAccountDelete.class, TransactionType.ACCOUNT_DELETE)
      .put(ImmutableCheckCancel.class, TransactionType.CHECK_CANCEL)
      .put(ImmutableCheckCash.class, TransactionType.CHECK_CASH)
      .put(ImmutableCheckCreate.class, TransactionType.CHECK_CREATE)
      .put(ImmutableDepositPreAuth.class, TransactionType.DEPOSIT_PRE_AUTH)
      .put(ImmutableEnableAmendment.class, TransactionType.ENABLE_AMENDMENT)
      .put(ImmutableEscrowCancel.class, TransactionType.ESCROW_CANCEL)
      .put(ImmutableEscrowCreate.class, TransactionType.ESCROW_CREATE)
      .put(ImmutableEscrowFinish.class, TransactionType.ESCROW_FINISH)
      .put(ImmutableNfTokenBurn.class, TransactionType.NFTOKEN_BURN)
      .put(ImmutableNfTokenMint.class, TransactionType.NFTOKEN_MINT)
      .put(ImmutableNfTokenAcceptOffer.class, TransactionType.NFTOKEN_ACCEPT_OFFER)
      .put(ImmutableNfTokenCancelOffer.class, TransactionType.NFTOKEN_CANCEL_OFFER)
      .put(ImmutableNfTokenCreateOffer.class, TransactionType.NFTOKEN_CREATE_OFFER)
      .put(ImmutableOfferCancel.class, TransactionType.OFFER_CANCEL)
      .put(ImmutableOfferCreate.class, TransactionType.OFFER_CREATE)
      .put(ImmutablePayment.class, TransactionType.PAYMENT)
      .put(ImmutablePaymentChannelClaim.class, TransactionType.PAYMENT_CHANNEL_CLAIM)
      .put(ImmutablePaymentChannelCreate.class, TransactionType.PAYMENT_CHANNEL_CREATE)
      .put(ImmutablePaymentChannelFund.class, TransactionType.PAYMENT_CHANNEL_FUND)
      .put(ImmutableSetFee.class, TransactionType.SET_FEE)
      .put(ImmutableSetRegularKey.class, TransactionType.SET_REGULAR_KEY)
      .put(ImmutableSignerListSet.class, TransactionType.SIGNER_LIST_SET)
      .put(ImmutableTrustSet.class, TransactionType.TRUST_SET)
      .put(ImmutableTicketCreate.class, TransactionType.TICKET_CREATE)
      .put(ImmutableUnlModify.class, TransactionType.UNL_MODIFY)
      .put(ImmutableAmmBid.class, TransactionType.AMM_BID)
      .put(ImmutableAmmCreate.class, TransactionType.AMM_CREATE)
      .put(ImmutableAmmDeposit.class, TransactionType.AMM_DEPOSIT)
      .put(ImmutableAmmVote.class, TransactionType.AMM_VOTE)
      .put(ImmutableAmmWithdraw.class, TransactionType.AMM_WITHDRAW)
      .put(ImmutableAmmDelete.class, TransactionType.AMM_DELETE)
      .put(ImmutableClawback.class, TransactionType.CLAWBACK)
      .put(ImmutableXChainAccountCreateCommit.class, TransactionType.XCHAIN_ACCOUNT_CREATE_COMMIT)
      .put(ImmutableXChainAddAccountCreateAttestation.class, TransactionType.XCHAIN_ADD_ACCOUNT_CREATE_ATTESTATION)
      .put(ImmutableXChainAddClaimAttestation.class, TransactionType.XCHAIN_ADD_CLAIM_ATTESTATION)
      .put(ImmutableXChainClaim.class, TransactionType.XCHAIN_CLAIM)
      .put(ImmutableXChainCommit.class, TransactionType.XCHAIN_COMMIT)
      .put(ImmutableXChainCreateBridge.class, TransactionType.XCHAIN_CREATE_BRIDGE)
      .put(ImmutableXChainCreateClaimId.class, TransactionType.XCHAIN_CREATE_CLAIM_ID)
      .put(ImmutableXChainModifyBridge.class, TransactionType.XCHAIN_MODIFY_BRIDGE)
      .put(ImmutableDidSet.class, TransactionType.DID_SET)
      .put(ImmutableDidDelete.class, TransactionType.DID_DELETE)
      .put(ImmutableOracleSet.class, TransactionType.ORACLE_SET)
      .put(ImmutableOracleDelete.class, TransactionType.ORACLE_DELETE)
      .put(ImmutableUnknownTransaction.class, TransactionType.UNKNOWN)
      .build();

  /**
   * The unique {@link Address} of the account that initiated this transaction.
   *
   * @return The {@link Address} of the account submitting this transaction.
   */
  @JsonProperty("Account")
  Address account();

  /**
   * The type of transaction.
   *
   * @return A {@link TransactionType}.
   */
  @JsonProperty("TransactionType")
  @Value.Default // must be Default rather than Derived, otherwise Jackson treats "TransactionType" as an unknownField
  default TransactionType transactionType() {
    return typeMap.get(this.getClass());
  }

  /**
   * The {@link String} representation of an integer amount of XRP, in drops, to be destroyed as a cost for distributing
   * this Payment transaction to the network.
   *
   * <p>This field is auto-fillable
   *
   * @return An {@link XrpCurrencyAmount} representing the transaction cost.
   *
   * @see "https://xrpl.org/transaction-common-fields.html#auto-fillable-fields"
   */
  @JsonProperty("Fee")
  XrpCurrencyAmount fee();

  /**
   * The sequence number of the account submitting the {@link Transaction}. A {@link Transaction} is only valid if the
   * Sequence number is exactly 1 greater than the previous transaction from the same account.
   *
   * <p>This field is auto-fillable
   *
   * @return An {@link UnsignedInteger} representing the sequence of the transaction.
   *
   * @see "https://xrpl.org/transaction-common-fields.html#auto-fillable-fields"
   */
  @Value.Default
  @JsonProperty("Sequence")
  default UnsignedInteger sequence() {
    return UnsignedInteger.ZERO;
  }

  /**
   * The sequence number of the {@link org.xrpl.xrpl4j.model.ledger.TicketObject} to use in place of a
   * {@link #sequence()} number. If this is provided, {@link #sequence()} must be 0. Cannot be used with
   * {@link #accountTransactionId()}.
   *
   * @return An {@link UnsignedInteger} representing the ticket sequence of the transaction.
   */
  @JsonProperty("TicketSequence")
  Optional<UnsignedInteger> ticketSequence();

  /**
   * Hash value identifying another transaction. If provided, this {@link Transaction} is only valid if the sending
   * account's previously-sent transaction matches the provided hash.
   *
   * @return An {@link Optional} of type {@link Hash256} containing the account transaction ID.
   */
  @JsonProperty("AccountTxnID")
  Optional<Hash256> accountTransactionId();

  /**
   * Highest ledger index this transaction can appear in. Specifying this field places a strict upper limit on how long
   * the transaction can wait to be validated or rejected.
   *
   * @return An {@link Optional} of type {@link UnsignedInteger} representing the last ledger sequence.
   */
  @JsonProperty("LastLedgerSequence")
  Optional<UnsignedInteger> lastLedgerSequence();

  /**
   * Additional arbitrary information used to identify this {@link Transaction}.
   *
   * @return A {@link List} of {@link MemoWrapper}s.
   */
  @JsonProperty("Memos")
  List<MemoWrapper> memos();

  /**
   * Array of {@link SignerWrapper}s that represent a multi-signature which authorizes this {@link Transaction}.
   *
   * @return A {@link List} of {@link SignerWrapper}s.
   */
  @JsonProperty("Signers")
  List<SignerWrapper> signers();

  /**
   * Arbitrary {@link UnsignedInteger} used to identify the reason for this {@link Transaction}, or a sender on whose
   * behalf this {@link Transaction} is made.
   *
   * @return An {@link Optional} {@link UnsignedInteger} representing the source account's tag.
   */
  @JsonProperty("SourceTag")
  Optional<UnsignedInteger> sourceTag();

  /**
   * The {@link PublicKey} that corresponds to the private key used to sign this transaction. If an empty string, ie
   * {@link PublicKey#MULTI_SIGN_PUBLIC_KEY}, indicates a multi-signature is present in the
   * {@link Transaction#signers()} field instead.
   *
   * @return A {@link PublicKey} containing the public key of the account submitting the transaction, or
   *   {@link PublicKey#MULTI_SIGN_PUBLIC_KEY} if the transaction is multi-signed.
   */
  @JsonInclude(JsonInclude.Include.NON_ABSENT)
  @JsonProperty("SigningPubKey")
  @Value.Default
  default PublicKey signingPublicKey() {
    return PublicKey.MULTI_SIGN_PUBLIC_KEY;
  }

  /**
   * The signature that verifies this transaction as originating from the account it says it is from.
   *
   * <p>This field is automatically added when signing this {@link Transaction}.
   *
   * @return An {@link Optional} {@link String} containing the transaction signature.
   */
  @JsonProperty("TxnSignature")
  Optional<Signature> transactionSignature();

  @JsonProperty("NetworkID")
  Optional<NetworkId> networkId();

  @JsonAnyGetter
  @JsonInclude(Include.NON_ABSENT)
  Map<String, Object> unknownFields();

}
