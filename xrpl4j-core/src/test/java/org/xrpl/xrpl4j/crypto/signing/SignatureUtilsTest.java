package org.xrpl.xrpl4j.crypto.signing;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.AddressConstants;
import org.xrpl.xrpl4j.model.client.channels.UnsignedClaim;
import org.xrpl.xrpl4j.model.flags.AmmDepositFlags;
import org.xrpl.xrpl4j.model.flags.AmmWithdrawFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.AttestationClaim;
import org.xrpl.xrpl4j.model.ledger.AttestationCreateAccount;
import org.xrpl.xrpl4j.model.ledger.AuthAccount;
import org.xrpl.xrpl4j.model.ledger.AuthAccountWrapper;
import org.xrpl.xrpl4j.model.ledger.Issue;
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.AmmBid;
import org.xrpl.xrpl4j.model.transactions.AmmClawback;
import org.xrpl.xrpl4j.model.transactions.AmmCreate;
import org.xrpl.xrpl4j.model.transactions.AmmDelete;
import org.xrpl.xrpl4j.model.transactions.AmmDeposit;
import org.xrpl.xrpl4j.model.transactions.AmmVote;
import org.xrpl.xrpl4j.model.transactions.AmmWithdraw;
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.Clawback;
import org.xrpl.xrpl4j.model.transactions.DepositPreAuth;
import org.xrpl.xrpl4j.model.transactions.DidData;
import org.xrpl.xrpl4j.model.transactions.DidDelete;
import org.xrpl.xrpl4j.model.transactions.DidDocument;
import org.xrpl.xrpl4j.model.transactions.DidSet;
import org.xrpl.xrpl4j.model.transactions.DidUri;
import org.xrpl.xrpl4j.model.transactions.EscrowCancel;
import org.xrpl.xrpl4j.model.transactions.EscrowCreate;
import org.xrpl.xrpl4j.model.transactions.EscrowFinish;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainAddClaimAttestation;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainBridge;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainClaim;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainCreateClaimId;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.NfTokenAcceptOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenBurn;
import org.xrpl.xrpl4j.model.transactions.NfTokenCancelOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenCreateOffer;
import org.xrpl.xrpl4j.model.transactions.NfTokenId;
import org.xrpl.xrpl4j.model.transactions.NfTokenMint;
import org.xrpl.xrpl4j.model.transactions.OfferCancel;
import org.xrpl.xrpl4j.model.transactions.OfferCreate;
import org.xrpl.xrpl4j.model.transactions.OracleDelete;
import org.xrpl.xrpl4j.model.transactions.OracleDocumentId;
import org.xrpl.xrpl4j.model.transactions.OracleSet;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.SetRegularKey;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.SignerWrapper;
import org.xrpl.xrpl4j.model.transactions.TicketCreate;
import org.xrpl.xrpl4j.model.transactions.TradingFee;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
import org.xrpl.xrpl4j.model.transactions.XChainAccountCreateCommit;
import org.xrpl.xrpl4j.model.transactions.XChainAddAccountCreateAttestation;
import org.xrpl.xrpl4j.model.transactions.XChainAddClaimAttestation;
import org.xrpl.xrpl4j.model.transactions.XChainBridge;
import org.xrpl.xrpl4j.model.transactions.XChainClaim;
import org.xrpl.xrpl4j.model.transactions.XChainClaimId;
import org.xrpl.xrpl4j.model.transactions.XChainCommit;
import org.xrpl.xrpl4j.model.transactions.XChainCount;
import org.xrpl.xrpl4j.model.transactions.XChainCreateBridge;
import org.xrpl.xrpl4j.model.transactions.XChainCreateClaimId;
import org.xrpl.xrpl4j.model.transactions.XChainModifyBridge;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Unit tests for {@link SignatureUtils}.
 */
public class SignatureUtilsTest {

  private static final String HEX_PUBLIC_KEY = "027535A4E90B2189CF9885563F45C4F454B3BFAB21930089C3878A9427B4D648D9";
  public static final ImmutableXChainBridge XCHAIN_BRIDGE = XChainBridge.builder()
    .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
    .lockingChainIssue(Issue.XRP)
    .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
    .issuingChainIssue(
      Issue.builder()
        .currency("TST")
        .issuer(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
        .build()
    )
    .build();

  PublicKey sourcePublicKey;

  @Mock
  Transaction transactionMock;

  @Mock
  Signature signatureMock;

  @Mock
  SignerWrapper signer1;

  @Mock
  SignerWrapper signer2;

  @Mock
  ObjectMapper objectMapperMock;

  @Mock
  XrplBinaryCodec xrplBinaryCodecMock;

  SignatureUtils signatureUtils;

  @BeforeEach
  public void setUp() throws JsonProcessingException {
    openMocks(this);

    this.sourcePublicKey = PublicKey.fromBase16EncodedPublicKey(HEX_PUBLIC_KEY);

    when(objectMapperMock.writeValueAsString(any())).thenReturn("{foo}"); // <-- Unused JSON value.
    when(xrplBinaryCodecMock.encodeForSigning(anyString())).thenReturn("ED");
    when(xrplBinaryCodecMock.encodeForMultiSigning(any(), anyString())).thenReturn("ED");
    when(xrplBinaryCodecMock.encode(anyString())).thenReturn("0123456789"); // <-- Unused HEX value.
    this.signatureUtils = new SignatureUtils(objectMapperMock, xrplBinaryCodecMock);
  }

  //////////////////
  // toSignableBytes (Transaction)
  //////////////////

  @Test
  public void toSignableBytesWithNullTransaction() {
    assertThrows(NullPointerException.class, () -> signatureUtils.toSignableBytes((Transaction) null));
  }

  @Test
  public void toMultiSignableBytesWithNullTransaction() {
    assertThrows(NullPointerException.class,
      () -> signatureUtils.toMultiSignableBytes(null, sourcePublicKey.deriveAddress()));
  }

  @Test
  public void toMultiSignableBytesWithNullSignerAddress() {
    assertThrows(
      NullPointerException.class,
      () -> signatureUtils.toMultiSignableBytes(transactionMock, null)
    );
  }

  @Test
  public void toSignableBytesWithJsonException() throws JsonProcessingException {
    doThrow(new JsonParseException(mock(JsonParser.class), "", mock(JsonLocation.class)))
      .when(objectMapperMock).writeValueAsString(any());
    assertThrows(RuntimeException.class, () -> signatureUtils.toSignableBytes(transactionMock));
  }

  @Test
  public void toMutliSignableBytesWithJsonException() throws JsonProcessingException {
    doThrow(new JsonParseException(mock(JsonParser.class), "", mock(JsonLocation.class)))
      .when(objectMapperMock).writeValueAsString(any());
    assertThrows(RuntimeException.class,
      () -> signatureUtils.toMultiSignableBytes(transactionMock, mock(Address.class)));
  }

  @Test
  public void toSignableBytes() throws JsonProcessingException {
    UnsignedByteArray actual = signatureUtils.toSignableBytes(transactionMock);
    assertThat(actual.length()).isEqualTo(1);

    verify(objectMapperMock).writeValueAsString(transactionMock);
    verifyNoMoreInteractions(objectMapperMock);
    verify(xrplBinaryCodecMock).encodeForSigning(anyString());
    verifyNoMoreInteractions(xrplBinaryCodecMock);
  }

  //////////////////
  // toSignableBytes (UnsignedClaim)
  //////////////////

  @Test
  void unsignedClaimToSignableBytesWhenNull() {
    assertThrows(NullPointerException.class, () -> signatureUtils.toSignableBytes((UnsignedClaim) null));
  }

  @Test
  void unsignedClaimToSignableBytes() throws JsonProcessingException {
    when(xrplBinaryCodecMock.encodeForSigningClaim(any())).thenReturn("ABCD1234");
    UnsignedClaim unsignedClaim = UnsignedClaim.builder()
      .amount(XrpCurrencyAmount.of(UnsignedLong.ONE))
      .channel(Hash256.of("ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD"))
      .build();
    assertThat(signatureUtils.toSignableBytes(unsignedClaim).hexValue()).isEqualTo("ABCD1234");

    verify(objectMapperMock).writeValueAsString(any());
    verifyNoMoreInteractions(objectMapperMock);
    verify(xrplBinaryCodecMock).encodeForSigningClaim(any());
    verifyNoMoreInteractions(xrplBinaryCodecMock);
  }

  @Test
  public void unsignedClaimToSignableBytesWithJsonException() throws JsonProcessingException {
    UnsignedClaim unsignedClaim = UnsignedClaim.builder()
      .amount(XrpCurrencyAmount.of(UnsignedLong.ONE))
      .channel(Hash256.of("ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD"))
      .build();
    doThrow(new JsonParseException(mock(JsonParser.class), "", mock(JsonLocation.class)))
      .when(objectMapperMock).writeValueAsString(unsignedClaim);
    assertThrows(RuntimeException.class, () -> signatureUtils.toSignableBytes(unsignedClaim));
  }

  @Test
  void unsignedClaimToSignableBytesActual() {
    UnsignedClaim unsignedClaim = UnsignedClaim.builder()
      .amount(XrpCurrencyAmount.of(UnsignedLong.ONE))
      .channel(Hash256.of("ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD"))
      .build();
    assertThat(SignatureUtils.getInstance().toSignableBytes(unsignedClaim).hexValue())
      .isEqualTo("434C4D00ABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCDABCD0000000000000001");
  }

  //////////////////
  // toSignableBytes (AttestationClaim)
  //////////////////

  @Test
  void attestationClaimToSignableBytesWhenNull() {
    assertThrows(NullPointerException.class, () -> signatureUtils.toSignableBytes((AttestationClaim) null));
  }

  @Test
  void attestationClaimToSignableBytes() throws JsonProcessingException {
    when(xrplBinaryCodecMock.encode(any())).thenReturn("ABCD1234");
    final AttestationClaim unsignedAttestation = AttestationClaim.builder()
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .otherChainSource(AddressConstants.GENESIS_ACCOUNT)
      .amount(XrpCurrencyAmount.ofDrops(10))
      .attestationRewardAccount(AddressConstants.GENESIS_ACCOUNT)
      .wasLockingChainSend(true)
      .xChainClaimId(XChainClaimId.of(UnsignedLong.ONE))
      .destination(AddressConstants.GENESIS_ACCOUNT)
      .build();
    assertThat(signatureUtils.toSignableBytes(unsignedAttestation).hexValue()).isEqualTo("ABCD1234");

    verify(objectMapperMock).writeValueAsString(any());
    verifyNoMoreInteractions(objectMapperMock);
    verify(xrplBinaryCodecMock).encode(any());
    verifyNoMoreInteractions(xrplBinaryCodecMock);
  }

  @Test
  public void attestationClaimToSignableBytesWithJsonException() throws JsonProcessingException {
    final AttestationClaim unsignedAttestation = AttestationClaim.builder()
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .otherChainSource(AddressConstants.GENESIS_ACCOUNT)
      .amount(XrpCurrencyAmount.ofDrops(10))
      .attestationRewardAccount(AddressConstants.GENESIS_ACCOUNT)
      .wasLockingChainSend(true)
      .xChainClaimId(XChainClaimId.of(UnsignedLong.ONE))
      .destination(AddressConstants.GENESIS_ACCOUNT)
      .build();
    doThrow(new JsonParseException(mock(JsonParser.class), "", mock(JsonLocation.class)))
      .when(objectMapperMock).writeValueAsString(unsignedAttestation);
    assertThrows(RuntimeException.class, () -> signatureUtils.toSignableBytes(unsignedAttestation));
  }

  @Test
  void attestationClaimToSignableBytesActual() {
    final AttestationClaim unsignedAttestation = AttestationClaim.builder()
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .otherChainSource(AddressConstants.GENESIS_ACCOUNT)
      .amount(XrpCurrencyAmount.ofDrops(10))
      .attestationRewardAccount(AddressConstants.GENESIS_ACCOUNT)
      .wasLockingChainSend(true)
      .xChainClaimId(XChainClaimId.of(UnsignedLong.ONE))
      .destination(AddressConstants.GENESIS_ACCOUNT)
      .build();
    assertThat(SignatureUtils.getInstance().toSignableBytes(unsignedAttestation).hexValue())
      .isEqualTo("3014000000000000000161400000000000000A8314B5F762798A53D543A014CAF8B297CFF8F2F937E" +
        "8801214B5F762798A53D543A014CAF8B297CFF8F2F937E8801514B5F762798A53D543A014CAF8B297CFF8F2F937E8001013" +
        "01011914B5F762798A53D543A014CAF8B297CFF8F2F937E8000000000000000000000000000000000000000014B5F762798" +
        "A53D543A014CAF8B297CFF8F2F937E80000000000000000000000000000000000000000");
  }

  //////////////////
  // toSignableBytes (AttestationCreateAccount)
  //////////////////

  @Test
  void attestationCreateAccountToSignableBytesWhenNull() {
    assertThrows(NullPointerException.class, () -> signatureUtils.toSignableBytes((AttestationCreateAccount) null));
  }

  @Test
  void attestationCreateToSignableBytes() throws JsonProcessingException {
    when(xrplBinaryCodecMock.encode(any())).thenReturn("ABCD1234");
    final AttestationCreateAccount unsignedAttestation = AttestationCreateAccount.builder()
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .otherChainSource(AddressConstants.GENESIS_ACCOUNT)
      .amount(XrpCurrencyAmount.ofDrops(10))
      .attestationRewardAccount(AddressConstants.GENESIS_ACCOUNT)
      .wasLockingChainSend(true)
      .destination(AddressConstants.GENESIS_ACCOUNT)
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.ONE))
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .build();

    assertThat(signatureUtils.toSignableBytes(unsignedAttestation).hexValue()).isEqualTo("ABCD1234");

    verify(objectMapperMock).writeValueAsString(any());
    verifyNoMoreInteractions(objectMapperMock);
    verify(xrplBinaryCodecMock).encode(any());
    verifyNoMoreInteractions(xrplBinaryCodecMock);
  }

  @Test
  public void attestationCreateToSignableBytesWithJsonException() throws JsonProcessingException {
    final AttestationCreateAccount unsignedAttestation = AttestationCreateAccount.builder()
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .otherChainSource(AddressConstants.GENESIS_ACCOUNT)
      .amount(XrpCurrencyAmount.ofDrops(10))
      .attestationRewardAccount(AddressConstants.GENESIS_ACCOUNT)
      .wasLockingChainSend(true)
      .destination(AddressConstants.GENESIS_ACCOUNT)
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.ONE))
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .build();

    doThrow(new JsonParseException(mock(JsonParser.class), "", mock(JsonLocation.class)))
      .when(objectMapperMock).writeValueAsString(unsignedAttestation);
    assertThrows(RuntimeException.class, () -> signatureUtils.toSignableBytes(unsignedAttestation));
  }

  @Test
  void attestationCreateToSignableBytesActual() {
    final AttestationCreateAccount unsignedAttestation = AttestationCreateAccount.builder()
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(AddressConstants.GENESIS_ACCOUNT)
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .otherChainSource(AddressConstants.GENESIS_ACCOUNT)
      .amount(XrpCurrencyAmount.ofDrops(10))
      .attestationRewardAccount(AddressConstants.GENESIS_ACCOUNT)
      .wasLockingChainSend(true)
      .destination(AddressConstants.GENESIS_ACCOUNT)
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.ONE))
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .build();

    assertThat(SignatureUtils.getInstance().toSignableBytes(unsignedAttestation).hexValue())
      .isEqualTo("3015000000000000000161400000000000000A601D40000000000000C88314B5F762798A53D543A014C" +
        "AF8B297CFF8F2F937E8801214B5F762798A53D543A014CAF8B297CFF8F2F937E8801514B5F762798A53D543A014CAF8B297CF" +
        "F8F2F937E800101301011914B5F762798A53D543A014CAF8B297CFF8F2F937E80000000000000000000000000000000000000" +
        "00014B5F762798A53D543A014CAF8B297CFF8F2F937E80000000000000000000000000000000000000000");
  }

  //////////////////
  // toMultiSignableBytes
  //////////////////


  @Test
  public void toMultiSignableBytes() throws JsonProcessingException {
    UnsignedByteArray actual = signatureUtils.toMultiSignableBytes(transactionMock, sourcePublicKey.deriveAddress());
    assertThat(actual.length()).isEqualTo(1);

    verify(objectMapperMock).writeValueAsString(transactionMock);
    verifyNoMoreInteractions(objectMapperMock);
    verify(xrplBinaryCodecMock).encodeForMultiSigning(anyString(), anyString());
    verifyNoMoreInteractions(xrplBinaryCodecMock);
  }

  @Test
  public void toMultiSignableBytesWithJsonException() throws JsonProcessingException {
    doThrow(new JsonParseException(mock(JsonParser.class), "", mock(JsonLocation.class)))
      .when(objectMapperMock).writeValueAsString(any());
    assertThrows(
      RuntimeException.class,
      () -> signatureUtils.toMultiSignableBytes(transactionMock, sourcePublicKey.deriveAddress())
    );
  }

  ////////////////////////////
  // addSignatureToTransaction
  ////////////////////////////

  @Test
  public void addSignatureToTransactionWithNullTransaction() {
    assertThrows(NullPointerException.class, () -> signatureUtils.addSignatureToTransaction(null, signatureMock));
  }

  @Test
  public void addSignatureToTransactionWithNullSignature() {
    assertThrows(NullPointerException.class, () -> signatureUtils.addSignatureToTransaction(transactionMock, null));
  }

  @Test
  public void addSignatureToTransactionWithMissingSignature() {
    when(transactionMock.transactionSignature()).thenReturn(Optional.empty());
    when(transactionMock.signingPublicKey()).thenReturn(PublicKey.MULTI_SIGN_PUBLIC_KEY);
    assertThrows(IllegalArgumentException.class,
      () -> signatureUtils.addSignatureToTransaction(transactionMock, signatureMock));
  }

  @Test
  public void addSignatureToTransactionPayment() {
    Payment payment = Payment.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .destination(sourcePublicKey.deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .signingPublicKey(sourcePublicKey)
      .build();
    addSignatureToTransactionHelper(payment);
  }

  @Test
  public void addSignatureToTransactionAccountSet() {
    AccountSet accountSet = AccountSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .build();
    addSignatureToTransactionHelper(accountSet);
  }

  @Test
  public void addSignatureToTransactionAccountDelete() {
    AccountDelete accountDelete = AccountDelete.builder()
      .account(sourcePublicKey.deriveAddress())
      .destination(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .build();
    addSignatureToTransactionHelper(accountDelete);
  }

  @Test
  public void addSignatureToTransactionCheckCancel() {
    CheckCancel checkCancel = CheckCancel.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .checkId(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .build();
    addSignatureToTransactionHelper(checkCancel);
  }

  @Test
  public void addSignatureToTransactionCheckCash() {
    CheckCash checkCash = CheckCash.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .checkId(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .build();
    addSignatureToTransactionHelper(checkCash);
  }

  @Test
  public void addSignatureToTransactionCheckCreate() {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .destination(sourcePublicKey.deriveAddress())
      .sendMax(XrpCurrencyAmount.ofDrops(100))
      .build();
    addSignatureToTransactionHelper(checkCreate);
  }

  @Test
  public void addSignatureToTransactionDepositPreAuth() {
    DepositPreAuth depositPreAuth = DepositPreAuth.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .authorize(sourcePublicKey.deriveAddress())
      .build();
    addSignatureToTransactionHelper(depositPreAuth);
  }

  @Test
  public void addSignatureToTransactionEscrowCancel() {
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .offerSequence(UnsignedInteger.ONE)
      .owner(sourcePublicKey.deriveAddress())
      .build();
    addSignatureToTransactionHelper(escrowCancel);
  }

  @Test
  public void addSignatureToTransactionEscrowFinish() {
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .offerSequence(UnsignedInteger.ONE)
      .owner(sourcePublicKey.deriveAddress())
      .build();
    addSignatureToTransactionHelper(escrowFinish);
  }

  @Test
  public void addSignatureToTransactionEscrowCreate() {
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .amount(XrpCurrencyAmount.ofDrops(100))
      .destination(sourcePublicKey.deriveAddress())
      .build();
    addSignatureToTransactionHelper(escrowCreate);
  }

  @Test
  public void addSignatureToTransactionTrustSet() {
    TrustSet trustSet = TrustSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .limitAmount(IssuedCurrencyAmount.builder()
        .issuer(sourcePublicKey.deriveAddress())
        .currency("USD")
        .value("10")
        .build())
      .build();
    addSignatureToTransactionHelper(trustSet);
  }

  @Test
  public void addSignatureToTransactionOfferOfferCreate() {
    OfferCreate offerCreate = OfferCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .takerPays(XrpCurrencyAmount.ofDrops(100))
      .takerGets(XrpCurrencyAmount.ofDrops(100))
      .build();
    addSignatureToTransactionHelper(offerCreate);
  }

  @Test
  public void addSignatureToTransactionOfferCancel() {
    OfferCancel offerCancel = OfferCancel.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .build();
    addSignatureToTransactionHelper(offerCancel);
  }

  @Test
  public void addSignatureToTransactionPaymentChannelCreate() {
    PaymentChannelCreate paymentChannelCreate = PaymentChannelCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .amount(XrpCurrencyAmount.ofDrops(100))
      .destination(sourcePublicKey.deriveAddress())
      .settleDelay(UnsignedInteger.ONE)
      .publicKey("123")
      .build();
    addSignatureToTransactionHelper(paymentChannelCreate);
  }

  @Test
  public void addSignatureToTransactionPaymentChannelClaim() {
    PaymentChannelClaim paymentChannelClaim = PaymentChannelClaim.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .channel(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .build();
    addSignatureToTransactionHelper(paymentChannelClaim);
  }

  @Test
  public void addSignatureToTransactionPaymentChannelFund() {
    PaymentChannelFund paymentChannelFund = PaymentChannelFund.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .channel(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .amount(XrpCurrencyAmount.ofDrops(100L))
      .build();
    addSignatureToTransactionHelper(paymentChannelFund);
  }

  @Test
  public void addSignatureToTransactionSetRegularKey() {
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .build();
    addSignatureToTransactionHelper(setRegularKey);
  }

  @Test
  public void addSignatureToTransactionSignerListSet() {
    SignerListSet signerListSet = SignerListSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .signerQuorum(UnsignedInteger.ONE)
      .build();
    addSignatureToTransactionHelper(signerListSet);
  }

  @Test
  public void addSignatureToTransactionNfTokenAcceptOffer() {

    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenAcceptOffer nfTokenAcceptOffer = NfTokenAcceptOffer.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(1))
      .signingPublicKey(sourcePublicKey)
      .buyOffer(offer)
      .build();
    addSignatureToTransactionHelper(nfTokenAcceptOffer);
  }

  @Test
  public void addSignatureToTransactionNfTokenBurn() {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenBurn nfTokenBurn = NfTokenBurn.builder()
      .fee(XrpCurrencyAmount.ofDrops(1))
      .account(sourcePublicKey.deriveAddress())
      .signingPublicKey(sourcePublicKey)
      .nfTokenId(id)
      .build();
    addSignatureToTransactionHelper(nfTokenBurn);
  }

  @Test
  public void addSignatureToTransactionNfTokenCancelOffer() {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    List<Hash256> offers = new ArrayList<>();
    offers.add(offer);
    NfTokenCancelOffer nfTokenCancelOffer = NfTokenCancelOffer.builder()
      .account(sourcePublicKey.deriveAddress())
      .signingPublicKey(sourcePublicKey)
      .fee(XrpCurrencyAmount.ofDrops(1))
      .tokenOffers(offers)
      .build();
    addSignatureToTransactionHelper(nfTokenCancelOffer);
  }

  @Test
  public void addSignatureToTransactionNfTokenCreateOffer() {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenCreateOffer nfTokenCreateOffer = NfTokenCreateOffer.builder()
      .account(sourcePublicKey.deriveAddress())
      .signingPublicKey(sourcePublicKey)
      .fee(XrpCurrencyAmount.ofDrops(1))
      .nfTokenId(id)
      .amount(XrpCurrencyAmount.ofDrops(2000L))
      .build();
    addSignatureToTransactionHelper(nfTokenCreateOffer);
  }

  @Test
  public void addSignatureToTransactionNfTokenMint() {
    UnsignedLong taxon = UnsignedLong.valueOf(146999694L);
    NfTokenMint nfTokenMint = NfTokenMint.builder()
      .fee(XrpCurrencyAmount.ofDrops(1))
      .account(sourcePublicKey.deriveAddress())
      .signingPublicKey(sourcePublicKey)
      .tokenTaxon(taxon)
      .build();
    addSignatureToTransactionHelper(nfTokenMint);
  }

  @Test
  void addSignatureToTicketCreate() {
    TicketCreate ticketCreate = TicketCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .ticketCount(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(ticketCreate);
  }

  @Test
  void addSignatureToAmmBid() {
    AmmBid bid = AmmBid.builder()
      .account(sourcePublicKey.deriveAddress())
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(bid);
  }

  @Test
  void addSignatureToAmmCreate() {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .value("25")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(250000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(6))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500)))
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(ammCreate);
  }

  @Test
  void addSignatureToAmmClawback() {
    AmmClawback ammClawback = AmmClawback.builder()
        .account(sourcePublicKey.deriveAddress())
        .holder(sourcePublicKey.deriveAddress())
        .amount(
            IssuedCurrencyAmount.builder()
                .currency("TST")
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .value("25")
                .build()
        )
        .asset(Issue.XRP)
        .asset2(
            Issue.builder()
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .currency("TST")
                .build()
        )
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(UnsignedInteger.valueOf(6))
        .signingPublicKey(sourcePublicKey)
        .build();

    addSignatureToTransactionHelper(ammClawback);
  }

  @Test
  void addMultiSignaturesToAmmClawback() {
    AmmClawback ammClawback = AmmClawback.builder()
        .account(sourcePublicKey.deriveAddress())
        .holder(sourcePublicKey.deriveAddress())
        .amount(
            IssuedCurrencyAmount.builder()
                .currency("TST")
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .value("25")
                .build()
        )
        .asset(Issue.XRP)
        .asset2(
            Issue.builder()
                .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
                .currency("TST")
                .build()
        )
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(UnsignedInteger.valueOf(6))
        .build();

    addMultiSignatureToTransactionHelper(ammClawback);
  }

  @Test
  void addSignatureToAmmDeposit() {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(AmmDepositFlags.LIMIT_LP_TOKEN)
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
      )
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(deposit);
  }

  @Test
  void addSignatureToAmmVote() {
    AmmVote vote = AmmVote.builder()
      .account(sourcePublicKey.deriveAddress())
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .build()
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(8))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(600)))
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(vote);
  }

  @Test
  void addSignatureToAmmWithdraw() {
    AmmWithdraw withdraw = AmmWithdraw.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .asset(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .asset2(Issue.XRP)
      .flags(AmmWithdrawFlags.WITHDRAW_ALL)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(withdraw);
  }

  @Test
  void addSignatureToAmmDelete() {
    AmmDelete ammDelete = AmmDelete.builder()
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .flags(TransactionFlags.UNSET)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(ammDelete);
  }

  @Test
  void addSignatureToClawback() {
    Clawback clawback = Clawback.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("FOO")
          .issuer(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
          .value("314.159")
          .build()
      )
      .build();

    addSignatureToTransactionHelper(clawback);
  }

  @Test
  void addSignatureToXChainAccountCreateCommit() {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .signingPublicKey(sourcePublicKey)
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addSignatureToTransactionHelper(commit);
  }

  @Test
  void addSignatureToXChainAddAccountCreateAttestation() {
    XChainAddAccountCreateAttestation transaction = XChainAddAccountCreateAttestation.builder()
      .account(sourcePublicKey.deriveAddress())
      .otherChainSource(Address.of("rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U"))
      .destination(Address.of("rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd"))
      .amount(XrpCurrencyAmount.ofDrops(2000000000))
      .publicKey(sourcePublicKey)
      .wasLockingChainSend(true)
      .attestationRewardAccount(Address.of("rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es"))
      .attestationSignerAccount(Address.of("rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw"))
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.valueOf(2)))
      .signatureReward(XrpCurrencyAmount.ofDrops(204))
      .xChainBridge(XCHAIN_BRIDGE)
      .fee(XrpCurrencyAmount.ofDrops(20))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .signature(
        Signature.fromBase16("F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E" +
          "9AFF11A4AA46F09ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500")
      )
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToXChainAddClaimAttestation() {
    XChainAddClaimAttestation transaction = XChainAddClaimAttestation.builder()
      .account(sourcePublicKey.deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(10000000))
      .attestationRewardAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .attestationSignerAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .destination(Address.of("rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi"))
      .fee(XrpCurrencyAmount.ofDrops(20))
      .lastLedgerSequence(UnsignedInteger.valueOf(19))
      .otherChainSource(Address.of("raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym"))
      .sequence(UnsignedInteger.valueOf(9))
      .publicKey(sourcePublicKey)
      .signingPublicKey(sourcePublicKey)
      .signature(
        Signature.fromBase16("F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E" +
          "9AFF11A4AA46F09ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500")
      )
      .wasLockingChainSend(true)
      .xChainBridge(XCHAIN_BRIDGE)
      .xChainClaimId(XChainClaimId.of(UnsignedLong.ONE))
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToXChainClaim() {
    XChainClaim transaction = XChainClaim.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(sourcePublicKey)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.valueOf(0x13F)))
      .destination(Address.of("rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw"))
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToXChainCommit() {
    XChainCommit transaction = XChainCommit.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.valueOf(0x13f)))
      .xChainBridge(XCHAIN_BRIDGE)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToXChainCreateBridge() {
    XChainCreateBridge transaction = XChainCreateBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .xChainBridge(XCHAIN_BRIDGE)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToXChainCreateClaimId() {
    XChainCreateClaimId transaction = XChainCreateClaimId.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .otherChainSource(Address.of("rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo"))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .xChainBridge(XCHAIN_BRIDGE)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToXChainModifyBridge() {
    XChainModifyBridge transaction = XChainModifyBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .xChainBridge(XCHAIN_BRIDGE)
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToDidSet() {
    DidSet transaction = DidSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToDidDelete() {
    DidDelete transaction = DidDelete.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(sourcePublicKey)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToOracleSet() {
    OracleSet transaction = OracleSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(sourcePublicKey)
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .lastUpdateTime(UnsignedInteger.ONE)
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  void addSignatureToOracleDelete() {
    OracleDelete transaction = OracleDelete.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .signingPublicKey(sourcePublicKey)
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .build();

    addSignatureToTransactionHelper(transaction);
  }

  @Test
  public void addSignatureToTransactionUnsupported() {
    assertThrows(IllegalArgumentException.class, () -> addSignatureToTransactionHelper(transactionMock));
  }

  @Test
  void addMultiSignaturesWithNulls() {
    assertThatThrownBy(
      () -> signatureUtils.addMultiSignaturesToTransaction(null, Lists.newArrayList(signer1))
    ).isInstanceOf(NullPointerException.class);

    assertThatThrownBy(
      () -> signatureUtils.addMultiSignaturesToTransaction(mock(Transaction.class), null)
    ).isInstanceOf(NullPointerException.class);
  }

  @Test
  void addMultiSignaturesWithTransactionSignaturePresent() {
    when(transactionMock.transactionSignature()).thenReturn(Optional.of(Signature.fromBase16("00")));
    assertThatThrownBy(
      () -> signatureUtils.addMultiSignaturesToTransaction(transactionMock, Lists.newArrayList(signer1))
    ).isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Transactions to be signed must not already include a signature.");
  }

  @Test
  void addMultiSignaturesWithSigningPublicKeyNonBlank() {
    when(transactionMock.transactionSignature()).thenReturn(Optional.empty());
    when(transactionMock.signingPublicKey())
      .thenReturn(
        PublicKey.fromBase16EncodedPublicKey("ED5F5AC8B98974A3CA843326D9B88CEBD0560177B973EE0B149F782CFAA06DC66A")
      );
    assertThatThrownBy(
      () -> signatureUtils.addMultiSignaturesToTransaction(transactionMock, Lists.newArrayList(signer1))
    ).isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Transactions to be multisigned must set signingPublicKey to an empty String.");
  }

  @Test
  public void addMultiSignaturesToTransactionPayment() {
    Payment payment = Payment.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .destination(sourcePublicKey.deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .build();
    addMultiSignatureToTransactionHelper(payment);
  }

  @Test
  public void addMultiSignaturesToTransactionAccountSet() {
    AccountSet accountSet = AccountSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .build();
    addMultiSignatureToTransactionHelper(accountSet);
  }

  @Test
  public void addMultiSignaturesToTransactionAccountDelete() {
    AccountDelete accountDelete = AccountDelete.builder()
      .account(sourcePublicKey.deriveAddress())
      .destination(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .build();
    addMultiSignatureToTransactionHelper(accountDelete);
  }

  @Test
  public void addMultiSignaturesToTransactionCheckCancel() {
    CheckCancel checkCancel = CheckCancel.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .checkId(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .build();
    addMultiSignatureToTransactionHelper(checkCancel);
  }

  @Test
  public void addMultiSignaturesToTransactionCheckCash() {
    CheckCash checkCash = CheckCash.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .checkId(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .build();
    addMultiSignatureToTransactionHelper(checkCash);
  }

  @Test
  public void addMultiSignaturesToTransactionCheckCreate() {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .destination(sourcePublicKey.deriveAddress())
      .sendMax(XrpCurrencyAmount.ofDrops(100))
      .build();
    addMultiSignatureToTransactionHelper(checkCreate);
  }

  @Test
  public void addMultiSignaturesToTransactionDepositPreAuth() {
    DepositPreAuth depositPreAuth = DepositPreAuth.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .authorize(sourcePublicKey.deriveAddress())
      .build();
    addMultiSignatureToTransactionHelper(depositPreAuth);
  }

  @Test
  public void addMultiSignaturesToTransactionEscrowCancel() {
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .offerSequence(UnsignedInteger.ONE)
      .owner(sourcePublicKey.deriveAddress())
      .build();
    addMultiSignatureToTransactionHelper(escrowCancel);
  }

  @Test
  public void addMultiSignaturesToTransactionEscrowFinish() {
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .offerSequence(UnsignedInteger.ONE)
      .owner(sourcePublicKey.deriveAddress())
      .build();
    addMultiSignatureToTransactionHelper(escrowFinish);
  }

  @Test
  public void addMultiSignaturesToTransactionEscrowCreate() {
    EscrowCreate escrowCreate = EscrowCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(100))
      .destination(sourcePublicKey.deriveAddress())
      .build();
    addMultiSignatureToTransactionHelper(escrowCreate);
  }

  @Test
  public void addMultiSignaturesToTransactionTrustSet() {
    TrustSet trustSet = TrustSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .limitAmount(IssuedCurrencyAmount.builder()
        .issuer(sourcePublicKey.deriveAddress())
        .currency("USD")
        .value("10")
        .build())
      .build();
    addMultiSignatureToTransactionHelper(trustSet);
  }

  @Test
  public void addMultiSignaturesToTransactionOfferOfferCreate() {
    OfferCreate offerCreate = OfferCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .takerPays(XrpCurrencyAmount.ofDrops(100))
      .takerGets(XrpCurrencyAmount.ofDrops(100))
      .build();
    addMultiSignatureToTransactionHelper(offerCreate);
  }

  @Test
  public void addMultiSignaturesToTransactionOfferCancel() {
    OfferCancel offerCancel = OfferCancel.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .build();
    addMultiSignatureToTransactionHelper(offerCancel);
  }

  @Test
  public void addMultiSignaturesToTransactionPaymentChannelCreate() {
    PaymentChannelCreate paymentChannelCreate = PaymentChannelCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(100))
      .destination(sourcePublicKey.deriveAddress())
      .settleDelay(UnsignedInteger.ONE)
      .publicKey("123")
      .build();
    addMultiSignatureToTransactionHelper(paymentChannelCreate);
  }

  @Test
  public void addMultiSignaturesToTransactionPaymentChannelClaim() {
    PaymentChannelClaim paymentChannelClaim = PaymentChannelClaim.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .build();
    addMultiSignatureToTransactionHelper(paymentChannelClaim);
  }

  @Test
  public void addMultiSignaturesToTransactionPaymentChannelFund() {
    PaymentChannelFund paymentChannelFund = PaymentChannelFund.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("0123456789012345678901234567890123456789012345678901234567891234"))
      .amount(XrpCurrencyAmount.ofDrops(100L))
      .build();
    addMultiSignatureToTransactionHelper(paymentChannelFund);
  }

  @Test
  public void addMultiSignaturesToTransactionSetRegularKey() {
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .build();
    addMultiSignatureToTransactionHelper(setRegularKey);
  }

  @Test
  public void addMultiSignaturesToTransactionSignerListSet() {
    SignerListSet signerListSet = SignerListSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .signerQuorum(UnsignedInteger.ONE)
      .build();
    addMultiSignatureToTransactionHelper(signerListSet);
  }

  @Test
  public void addMultiSignaturesToTransactionNfTokenAcceptOffer() {

    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenAcceptOffer nfTokenAcceptOffer = NfTokenAcceptOffer.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(1))
      .buyOffer(offer)
      .build();
    addMultiSignatureToTransactionHelper(nfTokenAcceptOffer);
  }

  @Test
  public void addMultiSignaturesToTransactionNfTokenBurn() {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenBurn nfTokenBurn = NfTokenBurn.builder()
      .fee(XrpCurrencyAmount.ofDrops(1))
      .account(sourcePublicKey.deriveAddress())
      .nfTokenId(id)
      .build();
    addMultiSignatureToTransactionHelper(nfTokenBurn);
  }

  @Test
  public void addMultiSignaturesToTransactionNfTokenCancelOffer() {
    Hash256 offer = Hash256.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    List<Hash256> offers = new ArrayList<>();
    offers.add(offer);
    NfTokenCancelOffer nfTokenCancelOffer = NfTokenCancelOffer.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(1))
      .tokenOffers(offers)
      .build();
    addMultiSignatureToTransactionHelper(nfTokenCancelOffer);
  }

  @Test
  public void addMultiSignaturesToTransactionNfTokenCreateOffer() {
    NfTokenId id = NfTokenId.of("000B013A95F14B0044F78A264E41713C64B5F89242540EE208C3098E00000D65");
    NfTokenCreateOffer nfTokenCreateOffer = NfTokenCreateOffer.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(1))
      .nfTokenId(id)
      .amount(XrpCurrencyAmount.ofDrops(2000L))
      .build();
    addMultiSignatureToTransactionHelper(nfTokenCreateOffer);
  }

  @Test
  public void addMultiSignaturesToTransactionNfTokenMint() {
    UnsignedLong taxon = UnsignedLong.valueOf(146999694L);
    NfTokenMint nfTokenMint = NfTokenMint.builder()
      .fee(XrpCurrencyAmount.ofDrops(1))
      .account(sourcePublicKey.deriveAddress())
      .tokenTaxon(taxon)
      .build();
    addMultiSignatureToTransactionHelper(nfTokenMint);
  }

  @Test
  void addMultiSignaturesToTicketCreate() {
    TicketCreate ticketCreate = TicketCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(UnsignedLong.ONE))
      .sequence(UnsignedInteger.ONE)
      .ticketCount(UnsignedInteger.ONE)
      .build();

    addMultiSignatureToTransactionHelper(ticketCreate);
  }

  @Test
  void addMultiSignaturesToClawback() {
    Clawback clawback = Clawback.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("FOO")
          .issuer(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
          .value("314.159")
          .build()
      )
      .build();

    addMultiSignatureToTransactionHelper(clawback);
  }

  @Test
  void addMultiSignatureToAmmBid() {
    AmmBid bid = AmmBid.builder()
      .account(sourcePublicKey.deriveAddress())
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rMKXGCbJ5d8LbrqthdG46q3f969MVK2Qeg"))),
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rBepJuTLFJt3WmtLXYAxSjtBWAeQxVbncv")))
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .build();

    addMultiSignatureToTransactionHelper(bid);
  }

  @Test
  void addMultiSignatureToAmmCreate() {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(sourcePublicKey.deriveAddress())
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .value("25")
          .build()
      )
      .amount2(XrpCurrencyAmount.ofDrops(250000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(6))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(500)))
      .build();

    addMultiSignatureToTransactionHelper(ammCreate);
  }

  @Test
  void addMultiSignatureToAmmDeposit() {
    AmmDeposit deposit = AmmDeposit.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(AmmDepositFlags.LIMIT_LP_TOKEN)
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
      )
      .build();

    addMultiSignatureToTransactionHelper(deposit);
  }

  @Test
  void addMultiSignatureToAmmVote() {
    AmmVote vote = AmmVote.builder()
      .account(sourcePublicKey.deriveAddress())
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .currency("TST")
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .build()
      )
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(8))
      .tradingFee(TradingFee.of(UnsignedInteger.valueOf(600)))
      .build();

    addMultiSignatureToTransactionHelper(vote);
  }

  @Test
  void addMultiSignatureToAmmWithdraw() {
    AmmWithdraw withdraw = AmmWithdraw.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .asset(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .asset2(Issue.XRP)
      .flags(AmmWithdrawFlags.WITHDRAW_ALL)
      .build();

    addMultiSignatureToTransactionHelper(withdraw);
  }

  @Test
  void addMultiSignatureToAmmDelete() {
    AmmDelete ammDelete = AmmDelete.builder()
      .asset(Issue.XRP)
      .asset2(
        Issue.builder()
          .issuer(Address.of("rP9jPyP5kyvFRb6ZiRghAGw5u8SGAmU4bd"))
          .currency("TST")
          .build()
      )
      .account(Address.of("rJVUeRqDFNs2xqA7ncVE6ZoAhPUoaJJSQm"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(9))
      .flags(TransactionFlags.UNSET)
      .build();

    addMultiSignatureToTransactionHelper(ammDelete);
  }

  @Test
  void addMultiSignatureToXChainAccountCreateCommit() {
    XChainAccountCreateCommit transaction = XChainAccountCreateCommit.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainAddAccountCreateAttestation() {
    XChainAddAccountCreateAttestation transaction = XChainAddAccountCreateAttestation.builder()
      .account(sourcePublicKey.deriveAddress())
      .otherChainSource(Address.of("rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U"))
      .destination(Address.of("rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd"))
      .amount(XrpCurrencyAmount.ofDrops(2000000000))
      .publicKey(sourcePublicKey)
      .wasLockingChainSend(true)
      .attestationRewardAccount(Address.of("rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es"))
      .attestationSignerAccount(Address.of("rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw"))
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.valueOf(2)))
      .signatureReward(XrpCurrencyAmount.ofDrops(204))
      .xChainBridge(XCHAIN_BRIDGE)
      .fee(XrpCurrencyAmount.ofDrops(20))
      .sequence(UnsignedInteger.ONE)
      .signature(
        Signature.fromBase16("F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E" +
          "9AFF11A4AA46F09ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500")
      )
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainAddClaimAttestation() {
    XChainAddClaimAttestation transaction = XChainAddClaimAttestation.builder()
      .account(sourcePublicKey.deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(10000000))
      .attestationRewardAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .attestationSignerAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .destination(Address.of("rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi"))
      .fee(XrpCurrencyAmount.ofDrops(20))
      .lastLedgerSequence(UnsignedInteger.valueOf(19))
      .otherChainSource(Address.of("raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym"))
      .sequence(UnsignedInteger.valueOf(9))
      .publicKey(sourcePublicKey)
      .signature(
        Signature.fromBase16("F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E" +
          "9AFF11A4AA46F09ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500")
      )
      .wasLockingChainSend(true)
      .xChainBridge(XCHAIN_BRIDGE)
      .xChainClaimId(XChainClaimId.of(UnsignedLong.ONE))
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainClaim() {
    XChainClaim transaction = XChainClaim.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.valueOf(0x13F)))
      .destination(Address.of("rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw"))
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainCommit() {
    XChainCommit transaction = XChainCommit.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.valueOf(0x13f)))
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainCreateBridge() {
    XChainCreateBridge transaction = XChainCreateBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainCreateClaimId() {
    XChainCreateClaimId transaction = XChainCreateClaimId.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .otherChainSource(Address.of("rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo"))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToXChainModifyBridge() {
    XChainModifyBridge transaction = XChainModifyBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(sourcePublicKey.deriveAddress())
      .xChainBridge(XCHAIN_BRIDGE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToDidSet() {
    DidSet transaction = DidSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToDidDelete() {
    DidDelete transaction = DidDelete.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToOracleSet() {
    OracleSet transaction = OracleSet.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .lastUpdateTime(UnsignedInteger.ONE)
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  void addMultiSignatureToOracleDelete() {
    OracleDelete transaction = OracleDelete.builder()
      .account(sourcePublicKey.deriveAddress())
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .build();

    addMultiSignatureToTransactionHelper(transaction);
  }

  @Test
  public void addMultiSignaturesToTransactionUnsupported() {
    when(transactionMock.transactionSignature()).thenReturn(Optional.empty());
    when(transactionMock.signingPublicKey()).thenReturn(PublicKey.MULTI_SIGN_PUBLIC_KEY);
    assertThatThrownBy(
      () -> addMultiSignatureToTransactionHelper(transactionMock)
    )
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Signing fields could not be added to the transaction.");
  }


  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private void addSignatureToTransactionHelper(final Transaction transaction) {
    Objects.requireNonNull(transaction);
    when(signatureMock.base16Value()).thenReturn("ED");
    SingleSignedTransaction<?> result = signatureUtils.addSignatureToTransaction(transaction, signatureMock);
    assertThat(result.unsignedTransaction()).isEqualTo(transaction);
    assertThat(result.signature().base16Value()).isEqualTo("ED");
    assertThat(result.signedTransaction().transactionSignature()).isPresent();
    assertThat(result.signedTransaction().transactionSignature().get().base16Value()).isEqualTo("ED");
  }

  private void addMultiSignatureToTransactionHelper(final Transaction transaction) {
    Objects.requireNonNull(transaction);

    Transaction signedTransaction = signatureUtils.addMultiSignaturesToTransaction(
      transaction,
      Lists.newArrayList(signer1, signer2)
    );

    assertThat(signedTransaction).usingRecursiveComparison().ignoringFields("signers").isEqualTo(transaction);
    assertThat(signedTransaction.signers()).asList().containsExactly(signer1, signer2);
  }
}
