package org.xrpl.xrpl4j.codec.binary;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: integration-tests
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
import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.ripple.cryptoconditions.CryptoConditionReader;
import com.ripple.cryptoconditions.der.DerEncodingException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.codec.binary.XrplBinaryCodec;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.model.flags.AccountSetTransactionFlags;
import org.xrpl.xrpl4j.model.flags.AmmDepositFlags;
import org.xrpl.xrpl4j.model.flags.AmmWithdrawFlags;
import org.xrpl.xrpl4j.model.flags.OfferCreateFlags;
import org.xrpl.xrpl4j.model.flags.PaymentChannelClaimFlags;
import org.xrpl.xrpl4j.model.flags.PaymentFlags;
import org.xrpl.xrpl4j.model.flags.RippleStateFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.flags.TrustSetFlags;
import org.xrpl.xrpl4j.model.flags.XChainModifyBridgeFlags;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;
import org.xrpl.xrpl4j.model.ledger.AuthAccount;
import org.xrpl.xrpl4j.model.ledger.AuthAccountWrapper;
import org.xrpl.xrpl4j.model.ledger.Issue;
import org.xrpl.xrpl4j.model.ledger.RippleStateObject;
import org.xrpl.xrpl4j.model.ledger.SignerEntry;
import org.xrpl.xrpl4j.model.ledger.SignerEntryWrapper;
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.AmmBid;
import org.xrpl.xrpl4j.model.transactions.AmmCreate;
import org.xrpl.xrpl4j.model.transactions.AmmDelete;
import org.xrpl.xrpl4j.model.transactions.AmmDeposit;
import org.xrpl.xrpl4j.model.transactions.AmmVote;
import org.xrpl.xrpl4j.model.transactions.AmmWithdraw;
import org.xrpl.xrpl4j.model.transactions.AssetPrice;
import org.xrpl.xrpl4j.model.transactions.CheckCancel;
import org.xrpl.xrpl4j.model.transactions.CheckCash;
import org.xrpl.xrpl4j.model.transactions.CheckCreate;
import org.xrpl.xrpl4j.model.transactions.CurrencyAmount;
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
import org.xrpl.xrpl4j.model.transactions.ImmutableAmmBid;
import org.xrpl.xrpl4j.model.transactions.ImmutableAmmCreate;
import org.xrpl.xrpl4j.model.transactions.ImmutableAmmDelete;
import org.xrpl.xrpl4j.model.transactions.ImmutableAmmDeposit;
import org.xrpl.xrpl4j.model.transactions.ImmutableDidDelete;
import org.xrpl.xrpl4j.model.transactions.ImmutableDidSet;
import org.xrpl.xrpl4j.model.transactions.ImmutableOracleDelete;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainAccountCreateCommit;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainAddClaimAttestation;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainClaim;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainCreateBridge;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainCreateClaimId;
import org.xrpl.xrpl4j.model.transactions.ImmutableXChainModifyBridge;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.OfferCancel;
import org.xrpl.xrpl4j.model.transactions.OfferCreate;
import org.xrpl.xrpl4j.model.transactions.OracleDelete;
import org.xrpl.xrpl4j.model.transactions.OracleDocumentId;
import org.xrpl.xrpl4j.model.transactions.OracleProvider;
import org.xrpl.xrpl4j.model.transactions.OracleSet;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.PriceData;
import org.xrpl.xrpl4j.model.transactions.PriceDataWrapper;
import org.xrpl.xrpl4j.model.transactions.SetRegularKey;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
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

import java.math.BigDecimal;

public class BinarySerializationTests {

  ObjectMapper objectMapper = ObjectMapperFactory.create();
  XrplBinaryCodec binaryCodec = XrplBinaryCodec.getInstance();

  private static IssuedCurrencyAmount currencyAmount(int amount) {
    return IssuedCurrencyAmount.builder()
      .currency("WCG")
      .issuer(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .value(amount + "")
      .build();
  }

  @Test
  public void serializeAccountSetTransactionWithNetworkId() throws JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rpP2GdsQwenNnFPefbXFgiTvEgJWQpq8Rw"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(10598))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000321FFFFFFFF240000296668400000000000000A730081140F3D0C7D2CFAB2EC8295451F0B3CA03" +
      "8E8E9CDCD";
    assertSerializesAndDeserializes(accountSet, expectedBinary);
  }

  @Test
  public void serializeAccountSetTransactionWithPresentOptionalStringFields() throws JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rpP2GdsQwenNnFPefbXFgiTvEgJWQpq8Rw"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(10598))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .domain("ABCD")
      .messageKey("03AB40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB")
      .emailHash("F9879D71855B5FF21E4963273A886BFC")
      .walletLocator("F9879D71855B5FF21E4963273A886BFCF9879D71855B5FF21E4963273A886BFC")
      .build();

    String expectedBinary = "12000321FFFFFFFF240000296641F9879D71855B5FF21E4963273A886BFC57F98" +
      "79D71855B5FF21E4963273A886BFCF9879D71855B5FF21E4963273A886BFC68400000000000000A722103AB" +
      "40A0490F9B7ED8DF29D246BF2D6269820A0EE7742ACDD457BEA7C7D0931EDB73007702ABCD81140F3D0C7D2" +
      "CFAB2EC8295451F0B3CA038E8E9CDCD";
    assertSerializesAndDeserializes(accountSet, expectedBinary);
  }

  @Test
  public void serializeAccountSetTransactionWithEmptyStrings() throws JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rpP2GdsQwenNnFPefbXFgiTvEgJWQpq8Rw"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(10598))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .domain("")
      .messageKey("")
      .build();

    String expectedBinary = "12000321FFFFFFFF240000296668400000000000000A72007300" +
      "770081140F3D0C7D2CFAB2EC8295451F0B3CA038E8E9CDCD";
    assertSerializesAndDeserializes(accountSet, expectedBinary);
  }

  @Test
  public void serializeAccountSetTransactionWithEmptyFlags() throws JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rpP2GdsQwenNnFPefbXFgiTvEgJWQpq8Rw"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(10598))
      .build();

    String expectedBinary = "120003240000296668400000000000000A730081140F3D0C7D2CFAB2EC8295451F0B3CA03" +
      "8E8E9CDCD";
    assertSerializesAndDeserializes(accountSet, expectedBinary);
  }

  @Test
  public void serializeAccountSetTransactionWithZeroFlags() throws JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rpP2GdsQwenNnFPefbXFgiTvEgJWQpq8Rw"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(10598))
      .flags(AccountSetTransactionFlags.of(0L))
      .build();

    String expectedBinary = "1200032200000000240000296668400000000000000A730081140F3D0C7D2CFAB2EC8295451F0B" +
      "3CA038E8E9CDCD";
    assertSerializesAndDeserializes(accountSet, expectedBinary);
  }

  @Test
  public void serializeAccountSetTransactionWithNonZeroFlags() throws JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
      .account(Address.of("rpP2GdsQwenNnFPefbXFgiTvEgJWQpq8Rw"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(10598))
      .flags(AccountSetTransactionFlags.builder().tfDisallowXrp().build())
      .build();

    String expectedBinary = "1200032280100000240000296668400000000000000A730081140F3D0C7D2CFAB2EC8295451F0B3CA" +
      "038E8E9CDCD";
    assertSerializesAndDeserializes(accountSet, expectedBinary);
  }

  @Test
  public void serializeAccountDelete() throws JsonProcessingException {
    AccountDelete accountDelete = AccountDelete.builder()
      .account(Address.of("rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm"))
      .fee(XrpCurrencyAmount.ofDrops(5000000))
      .sequence(UnsignedInteger.valueOf(2470665))
      .destination(Address.of("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe"))
      .destinationTag(UnsignedInteger.valueOf(13))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12001521FFFFFFFF240025B3092E0000000D6840000000004C4B40730081140596915CFDEEE" +
      "3A695B3EFD6BDA9AC788A368B7B8314F667B0CA50CC7709A220B0561B85E53A48461FA8";
    assertSerializesAndDeserializes(accountDelete, expectedBinary);
  }

  @Test
  public void serializeAccountDeleteWithZeroFlags() throws JsonProcessingException {
    AccountDelete accountDelete = AccountDelete.builder()
      .account(Address.of("rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm"))
      .fee(XrpCurrencyAmount.ofDrops(5000000))
      .sequence(UnsignedInteger.valueOf(2470665))
      .destination(Address.of("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe"))
      .destinationTag(UnsignedInteger.valueOf(13))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "1200152200000000240025B3092E0000000D6840000000004C4B40730081140596915CFDEEE" +
      "3A695B3EFD6BDA9AC788A368B7B8314F667B0CA50CC7709A220B0561B85E53A48461FA8";
    assertSerializesAndDeserializes(accountDelete, expectedBinary);
  }

  @Test
  public void serializeAccountDeleteWithNonZeroFlags() throws JsonProcessingException {
    AccountDelete accountDelete = AccountDelete.builder()
      .account(Address.of("rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm"))
      .fee(XrpCurrencyAmount.ofDrops(5000000))
      .sequence(UnsignedInteger.valueOf(2470665))
      .destination(Address.of("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe"))
      .destinationTag(UnsignedInteger.valueOf(13))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "1200152280000000240025B3092E0000000D6840000000004C4B40730081140596915CFDEEE" +
      "3A695B3EFD6BDA9AC788A368B7B8314F667B0CA50CC7709A220B0561B85E53A48461FA8";
    assertSerializesAndDeserializes(accountDelete, expectedBinary);
  }

  @Test
  public void serializeCheckCancel() throws JsonProcessingException {
    CheckCancel checkCancel = CheckCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .checkId(Hash256.of("49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"))
      .sequence(UnsignedInteger.valueOf(12))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12001221FFFFFFFF240000000C501849647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97" +
      "F67E9977FB068400000000000000C730081147990EC5D1D8DF69E070A968D4B186986FDF06ED0";
    assertSerializesAndDeserializes(checkCancel, expectedBinary);
  }

  @Test
  public void serializeCheckCancelWithZeroFlags() throws JsonProcessingException {
    CheckCancel checkCancel = CheckCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .checkId(Hash256.of("49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"))
      .sequence(UnsignedInteger.valueOf(12))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "1200122200000000240000000C501849647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97" +
      "F67E9977FB068400000000000000C730081147990EC5D1D8DF69E070A968D4B186986FDF06ED0";
    assertSerializesAndDeserializes(checkCancel, expectedBinary);
  }

  @Test
  public void serializeCheckCancelWithNonZeroFlags() throws JsonProcessingException {
    CheckCancel checkCancel = CheckCancel.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .checkId(Hash256.of("49647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97F67E9977FB0"))
      .sequence(UnsignedInteger.valueOf(12))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "1200122280000000240000000C501849647F0D748DC3FE26BDACBC57F251AADEFFF391403EC9BF87C97" +
      "F67E9977FB068400000000000000C730081147990EC5D1D8DF69E070A968D4B186986FDF06ED0";
    assertSerializesAndDeserializes(checkCancel, expectedBinary);
  }

  @Test
  public void serializeCheckCashWithZeroFlags() throws JsonProcessingException {
    CheckCash checkCash = CheckCash.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .checkId(Hash256.of("838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "120011220000000024000000015018838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4" +
      "056FCBD657F5733461400000000000006468400000000000000C7300811449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCash, expectedBinary);
  }

  @Test
  public void serializeCheckCashWithNonZeroFlags() throws JsonProcessingException {
    CheckCash checkCash = CheckCash.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .checkId(Hash256.of("838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "120011228000000024000000015018838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4" +
      "056FCBD657F5733461400000000000006468400000000000000C7300811449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCash, expectedBinary);
  }

  @Test
  public void serializeCheckCashWithXrpAmount() throws JsonProcessingException {
    CheckCash checkCash = CheckCash.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .checkId(Hash256.of("838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12001121FFFFFFFF24000000015018838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4" +
      "056FCBD657F5733461400000000000006468400000000000000C7300811449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCash, expectedBinary);
  }

  @Test
  public void serializeCheckCashWithXrpDeliverMin() throws JsonProcessingException {
    CheckCash checkCash = CheckCash.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .checkId(Hash256.of("838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .deliverMin(XrpCurrencyAmount.ofDrops(100))
      .build();

    String expectedBinary = "12001124000000015018838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056" +
      "FCBD657F5733468400000000000000C6A40000000000000647300811449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCash, expectedBinary);
  }

  @Test
  public void serializeCheckCashWithIssuedCurrencyDeliverMin() throws JsonProcessingException {
    CheckCash checkCash = CheckCash.builder()
      .account(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .checkId(Hash256.of("838766BA2B995C00744175F69A1B11E32C3DBC40E64801A4056FCBD657F57334"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .deliverMin(IssuedCurrencyAmount.builder()
        .currency("USD")
        .issuer(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
        .value("100")
        .build())
      .build();

    String expectedBinary = "12001124000000015018838766BA2B995C00744175F69A1B11E32C3DBC40E64801A40" +
      "56FCBD657F5733468400000000000000C6AD5038D7EA4C68000000000000000000000000000555344000000000049FF0C73CA" +
      "6AF9733DA805F76CA2C37776B7C46B7300811449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCash, expectedBinary);
  }

  @Test
  void serializeCheckCreate() throws JsonProcessingException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .destinationTag(UnsignedInteger.ONE)
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .expiration(UnsignedInteger.valueOf(570113521))
      .invoiceId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12001021FFFFFFFF24000000012A21FB3DF12E0000000150116F1DFD1D0FE8A32E40E1F2C05CF1C" +
      "15545BAB56B617F9C6C2D63A6B704BEF59B68400000000000000C694000000005F5E100730081147990EC5D1D8DF69E070A96" +
      "8D4B186986FDF06ED0831449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCreate, expectedBinary);
  }

  @Test
  void serializeCheckCreateWithZeroFlags() throws JsonProcessingException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .destinationTag(UnsignedInteger.ONE)
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .expiration(UnsignedInteger.valueOf(570113521))
      .invoiceId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "120010220000000024000000012A21FB3DF12E0000000150116F1DFD1D0FE8A32E40E1F2C05CF1C" +
      "15545BAB56B617F9C6C2D63A6B704BEF59B68400000000000000C694000000005F5E100730081147990EC5D1D8DF69E070A96" +
      "8D4B186986FDF06ED0831449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCreate, expectedBinary);
  }

  @Test
  void serializeCheckCreateWithNonZeroFlags() throws JsonProcessingException {
    CheckCreate checkCreate = CheckCreate.builder()
      .account(Address.of("rUn84CUYbNjRoTQ6mSW7BVJPSVJNLb1QLo"))
      .sequence(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(12))
      .destination(Address.of("rfkE1aSy9G8Upk4JssnwBxhEv5p4mn2KTy"))
      .destinationTag(UnsignedInteger.ONE)
      .sendMax(XrpCurrencyAmount.ofDrops(100000000))
      .expiration(UnsignedInteger.valueOf(570113521))
      .invoiceId(Hash256.of("6F1DFD1D0FE8A32E40E1F2C05CF1C15545BAB56B617F9C6C2D63A6B704BEF59B"))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "120010228000000024000000012A21FB3DF12E0000000150116F1DFD1D0FE8A32E40E1F2C05CF1C" +
      "15545BAB56B617F9C6C2D63A6B704BEF59B68400000000000000C694000000005F5E100730081147990EC5D1D8DF69E070A96" +
      "8D4B186986FDF06ED0831449FF0C73CA6AF9733DA805F76CA2C37776B7C46B";
    assertSerializesAndDeserializes(checkCreate, expectedBinary);
  }

  @Test
  void serializeDepositPreAuth() throws JsonProcessingException {
    DepositPreAuth preAuth = DepositPreAuth.builder()
      .account(Address.of("rDd6FpNbeY2CrQajSmP178BmNGusmQiYMM"))
      .authorize(Address.of("rDJFnv5sEfp42LMFiX3mVQKczpFTdxYDzM"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(65))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12001321FFFFFFFF240000004168400000000000000A730081148A928D14A643F388AC0D26B" +
      "AF9755B07EB0A2B44851486FFE2A17E861BA0FE9A3ED8352F895D80E789E0";
    assertSerializesAndDeserializes(preAuth, expectedBinary);
  }

  @Test
  void serializeDepositPreAuthWithZeroFlags() throws JsonProcessingException {
    DepositPreAuth preAuth = DepositPreAuth.builder()
      .account(Address.of("rDd6FpNbeY2CrQajSmP178BmNGusmQiYMM"))
      .authorize(Address.of("rDJFnv5sEfp42LMFiX3mVQKczpFTdxYDzM"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(65))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "1200132200000000240000004168400000000000000A730081148A928D14A643F388AC0D26B" +
      "AF9755B07EB0A2B44851486FFE2A17E861BA0FE9A3ED8352F895D80E789E0";
    assertSerializesAndDeserializes(preAuth, expectedBinary);
  }

  @Test
  void serializeDepositPreAuthWithNonZeroFlags() throws JsonProcessingException {
    DepositPreAuth preAuth = DepositPreAuth.builder()
      .account(Address.of("rDd6FpNbeY2CrQajSmP178BmNGusmQiYMM"))
      .authorize(Address.of("rDJFnv5sEfp42LMFiX3mVQKczpFTdxYDzM"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(65))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "1200132280000000240000004168400000000000000A730081148A928D14A643F388AC0D26B" +
      "AF9755B07EB0A2B44851486FFE2A17E861BA0FE9A3ED8352F895D80E789E0";
    assertSerializesAndDeserializes(preAuth, expectedBinary);
  }

  @Test
  void serializeEscrowCreate() throws JsonProcessingException, DerEncodingException {
    EscrowCreate checkCreate = EscrowCreate.builder()
      .account(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .cancelAfter(UnsignedLong.valueOf(630000001))
      .finishAfter(UnsignedLong.valueOf(630000000))
      .destination(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .condition(CryptoConditionReader.readCondition(BaseEncoding.base16().decode(
        "A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000121FFFFFFFF24000000012E00005BB82024258D09812025258D0980614000000000000064684" +
      "00000000000000C7300701127A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100" +
      "8114EE5F7CF61504C7CF7E0C22562EB19CC7ACB0FCBA8314B5F762798A53D543A014CAF8B297CFF8F2F937E8";
    assertSerializesAndDeserializes(checkCreate, expectedBinary);
  }

  @Test
  void serializeEscrowCreateWithZeroFlags() throws JsonProcessingException, DerEncodingException {
    EscrowCreate checkCreate = EscrowCreate.builder()
      .account(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .cancelAfter(UnsignedLong.valueOf(630000001))
      .finishAfter(UnsignedLong.valueOf(630000000))
      .destination(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .condition(CryptoConditionReader.readCondition(BaseEncoding.base16().decode(
        "A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "120001220000000024000000012E00005BB82024258D09812025258D0980614000000000000064684" +
      "00000000000000C7300701127A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100" +
      "8114EE5F7CF61504C7CF7E0C22562EB19CC7ACB0FCBA8314B5F762798A53D543A014CAF8B297CFF8F2F937E8";
    assertSerializesAndDeserializes(checkCreate, expectedBinary);
  }

  @Test
  void serializeEscrowCreateWithNonZeroFlags() throws JsonProcessingException, DerEncodingException {
    EscrowCreate checkCreate = EscrowCreate.builder()
      .account(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .amount(XrpCurrencyAmount.ofDrops(100))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .cancelAfter(UnsignedLong.valueOf(630000001))
      .finishAfter(UnsignedLong.valueOf(630000000))
      .destination(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .destinationTag(UnsignedInteger.valueOf(23480))
      .condition(CryptoConditionReader.readCondition(BaseEncoding.base16().decode(
        "A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100"))
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "120001228000000024000000012E00005BB82024258D09812025258D0980614000000000000064684" +
      "00000000000000C7300701127A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100" +
      "8114EE5F7CF61504C7CF7E0C22562EB19CC7ACB0FCBA8314B5F762798A53D543A014CAF8B297CFF8F2F937E8";
    assertSerializesAndDeserializes(checkCreate, expectedBinary);
  }

  @Test
  void serializeEscrowCancel() throws JsonProcessingException {
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .owner(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .offerSequence(UnsignedInteger.valueOf(25))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000421FFFFFFFF240000000120190000001968400000000000000C73008114EE5F7CF61504C7" +
      "CF7E0C22562EB19CC7ACB0FCBA8214EE5F7CF61504C7CF7E0C22562EB19CC7ACB0FCBA";
    assertSerializesAndDeserializes(escrowCancel, expectedBinary);
  }

  @Test
  void serializeEscrowCancelWithZeroFlags() throws JsonProcessingException {
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .owner(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .offerSequence(UnsignedInteger.valueOf(25))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "1200042200000000240000000120190000001968400000000000000C73008114EE5F7CF61504C7" +
      "CF7E0C22562EB19CC7ACB0FCBA8214EE5F7CF61504C7CF7E0C22562EB19CC7ACB0FCBA";
    assertSerializesAndDeserializes(escrowCancel, expectedBinary);
  }

  @Test
  void serializeEscrowCancelWithNonZeroFlags() throws JsonProcessingException {
    EscrowCancel escrowCancel = EscrowCancel.builder()
      .account(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .owner(Address.of("r4jQDHCUvgcBAa5EzcB1D8BHGcjYP9eBC2"))
      .offerSequence(UnsignedInteger.valueOf(25))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "1200042280000000240000000120190000001968400000000000000C73008114EE5F7CF61504C7" +
      "CF7E0C22562EB19CC7ACB0FCBA8214EE5F7CF61504C7CF7E0C22562EB19CC7ACB0FCBA";
    assertSerializesAndDeserializes(escrowCancel, expectedBinary);
  }

  @Test
  void serializeEscrowFinish() throws JsonProcessingException, DerEncodingException {
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(Address.of("rMYPppnVNQ7crMizv8D6wF45kYuSupygyr"))
      .fee(XrpCurrencyAmount.ofDrops(330))
      .sequence(UnsignedInteger.valueOf(3))
      .owner(Address.of("rMYPppnVNQ7crMizv8D6wF45kYuSupygyr"))
      .offerSequence(UnsignedInteger.valueOf(25))
      .condition(CryptoConditionReader.readCondition(BaseEncoding.base16().decode(
        "A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100")
      ))
      .fulfillment(CryptoConditionReader.readFulfillment(BaseEncoding.base16().decode("A0028000")))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000221FFFFFFFF240000000320190000001968400000000000014A7300701004A0028000701127A02" +
      "58020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B8558101008114E151CA3207BAB5B91D2F0E4D35" +
      "ECDFD4551C69A18214E151CA3207BAB5B91D2F0E4D35ECDFD4551C69A1";
    assertSerializesAndDeserializes(escrowFinish, expectedBinary);
  }

  @Test
  void serializeEscrowFinishWithZeroFlags() throws JsonProcessingException, DerEncodingException {
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(Address.of("rMYPppnVNQ7crMizv8D6wF45kYuSupygyr"))
      .fee(XrpCurrencyAmount.ofDrops(330))
      .sequence(UnsignedInteger.valueOf(3))
      .owner(Address.of("rMYPppnVNQ7crMizv8D6wF45kYuSupygyr"))
      .offerSequence(UnsignedInteger.valueOf(25))
      .condition(CryptoConditionReader.readCondition(BaseEncoding.base16().decode(
        "A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100")
      ))
      .fulfillment(CryptoConditionReader.readFulfillment(BaseEncoding.base16().decode("A0028000")))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "1200022200000000240000000320190000001968400000000000014A7300701004A0028000701127A02" +
      "58020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B8558101008114E151CA3207BAB5B91D2F0E4D35" +
      "ECDFD4551C69A18214E151CA3207BAB5B91D2F0E4D35ECDFD4551C69A1";
    assertSerializesAndDeserializes(escrowFinish, expectedBinary);
  }

  @Test
  void serializeEscrowFinishWithNonZeroFlags() throws JsonProcessingException, DerEncodingException {
    EscrowFinish escrowFinish = EscrowFinish.builder()
      .account(Address.of("rMYPppnVNQ7crMizv8D6wF45kYuSupygyr"))
      .fee(XrpCurrencyAmount.ofDrops(330))
      .sequence(UnsignedInteger.valueOf(3))
      .owner(Address.of("rMYPppnVNQ7crMizv8D6wF45kYuSupygyr"))
      .offerSequence(UnsignedInteger.valueOf(25))
      .condition(CryptoConditionReader.readCondition(BaseEncoding.base16().decode(
        "A0258020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855810100")
      ))
      .fulfillment(CryptoConditionReader.readFulfillment(BaseEncoding.base16().decode("A0028000")))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "1200022280000000240000000320190000001968400000000000014A7300701004A0028000701127A02" +
      "58020E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B8558101008114E151CA3207BAB5B91D2F0E4D35" +
      "ECDFD4551C69A18214E151CA3207BAB5B91D2F0E4D35ECDFD4551C69A1";
    assertSerializesAndDeserializes(escrowFinish, expectedBinary);
  }

  @Test
  void serializeXrpPaymentWithEmptyFlags() throws JsonProcessingException {
    Address source = Address.builder()
      .value("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK")
      .build();

    Address destination = Address.builder()
      .value("rrrrrrrrrrrrrrrrrrrrBZbvji")
      .build();

    Payment payment = Payment.builder()
      .account(source)
      .destination(destination)
      .sourceTag(UnsignedInteger.valueOf(1))
      .destinationTag(UnsignedInteger.valueOf(2))
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .fee(XrpCurrencyAmount.ofDrops(789))
      .sequence(UnsignedInteger.valueOf(56565656))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000021FFFFFFFF230000000124035F1F982E0000000261400000000000303968" +
      "400000000000031573008114EE39E6D05CFD6A90DAB700A1D70149ECEE29DFEC83140000000000000000000000000000000000000001";
    assertSerializesAndDeserializes(payment, expectedBinary);
  }

  @Test
  void serializeXrpPaymentWithZeroFlags() throws JsonProcessingException {
    Address source = Address.builder()
      .value("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK")
      .build();

    Address destination = Address.builder()
      .value("rrrrrrrrrrrrrrrrrrrrBZbvji")
      .build();

    Payment payment = Payment.builder()
      .account(source)
      .destination(destination)
      .sourceTag(UnsignedInteger.valueOf(1))
      .destinationTag(UnsignedInteger.valueOf(2))
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .fee(XrpCurrencyAmount.ofDrops(789))
      .sequence(UnsignedInteger.valueOf(56565656))
      .flags(PaymentFlags.UNSET)
      .build();

    String expectedBinary = "1200002200000000230000000124035F1F982E0000000261400000000000303968" +
      "400000000000031573008114EE39E6D05CFD6A90DAB700A1D70149ECEE29DFEC83140000000000000000000000000000000000000001";
    assertSerializesAndDeserializes(payment, expectedBinary);
  }

  @Test
  void serializeXrpPaymentWithNonZeroFlags() throws JsonProcessingException {
    Address source = Address.builder()
      .value("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK")
      .build();

    Address destination = Address.builder()
      .value("rrrrrrrrrrrrrrrrrrrrBZbvji")
      .build();

    Payment payment = Payment.builder()
      .account(source)
      .destination(destination)
      .sourceTag(UnsignedInteger.valueOf(1))
      .destinationTag(UnsignedInteger.valueOf(2))
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .fee(XrpCurrencyAmount.ofDrops(789))
      .sequence(UnsignedInteger.valueOf(56565656))
      .flags(PaymentFlags.builder().tfPartialPayment(true).build())
      .build();

    String expectedBinary = "1200002280020000230000000124035F1F982E0000000261400000000000303968" +
      "400000000000031573008114EE39E6D05CFD6A90DAB700A1D70149ECEE29DFEC83140000000000000000000000000000000000000001";
    assertSerializesAndDeserializes(payment, expectedBinary);
  }

  @Test
  void serializeIssuedCurrencyPayment() throws JsonProcessingException {
    Address source = Address.builder()
      .value("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK")
      .build();

    Address destination = Address.builder()
      .value("rrrrrrrrrrrrrrrrrrrrBZbvji")
      .build();

    Address issuer = Address.builder()
      .value("rDgZZ3wyprx4ZqrGQUkquE9Fs2Xs8XBcdw")
      .build();

    CurrencyAmount amount = IssuedCurrencyAmount.builder()
      .currency("USD")
      .issuer(issuer)
      .value("1234567890123456")
      .build();

    Payment payment = Payment.builder()
      .flags(PaymentFlags.builder()
        .tfPartialPayment(true)
        .build())
      .account(source)
      .destination(destination)
      .sourceTag(UnsignedInteger.valueOf(1))
      .destinationTag(UnsignedInteger.valueOf(2))
      .amount(amount)
      .fee(XrpCurrencyAmount.ofDrops(789))
      .sequence(UnsignedInteger.valueOf(1))
      .build();

    String expectedBinary = "1200002280020000230000000124000000012E0000000261D84462D53C8ABAC000000000000000" +
      "000000000055534400000000008B1CE810C13D6F337DAC85863B3D70265A24DF4468400000000000031573008114EE39E6D" +
      "05CFD6A90DAB700A1D70149ECEE29DFEC83140000000000000000000000000000000000000001";
    assertSerializesAndDeserializes(payment, expectedBinary);
  }

  @Test
  void serializePaymentChannelCreateWithEmptyFlags() throws JsonProcessingException {
    PaymentChannelCreate create = PaymentChannelCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .sourceTag(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(100))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .destinationTag(UnsignedInteger.valueOf(2))
      .settleDelay(UnsignedInteger.ONE)
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .cancelAfter(UnsignedLong.valueOf(533171558))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000D21FFFFFFFF230000000124000000012E0000000220241FC78D66202700000001614000" +
      "000000002710684000000000000064712132D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0" +
      "F0A730081144B4E9C06F24296074F7BC48F92A97916C6DC5EA983144B4E9C06F24296074F7BC48F92A97916C6DC5EA9";
    assertSerializesAndDeserializes(create, expectedBinary);
  }

  @Test
  void serializePaymentChannelCreateWithZeroFlags() throws JsonProcessingException {
    PaymentChannelCreate create = PaymentChannelCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .sourceTag(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(100))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .destinationTag(UnsignedInteger.valueOf(2))
      .settleDelay(UnsignedInteger.ONE)
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .cancelAfter(UnsignedLong.valueOf(533171558))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "12000D2200000000230000000124000000012E0000000220241FC78D66202700000001614000" +
      "000000002710684000000000000064712132D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0" +
      "F0A730081144B4E9C06F24296074F7BC48F92A97916C6DC5EA983144B4E9C06F24296074F7BC48F92A97916C6DC5EA9";
    assertSerializesAndDeserializes(create, expectedBinary);
  }

  @Test
  void serializePaymentChannelCreateWithNonZeroFlags() throws JsonProcessingException {
    PaymentChannelCreate create = PaymentChannelCreate.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .sourceTag(UnsignedInteger.ONE)
      .fee(XrpCurrencyAmount.ofDrops(100))
      .sequence(UnsignedInteger.ONE)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .destinationTag(UnsignedInteger.valueOf(2))
      .settleDelay(UnsignedInteger.ONE)
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .cancelAfter(UnsignedLong.valueOf(533171558))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "12000D2280000000230000000124000000012E0000000220241FC78D66202700000001614000" +
      "000000002710684000000000000064712132D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0" +
      "F0A730081144B4E9C06F24296074F7BC48F92A97916C6DC5EA983144B4E9C06F24296074F7BC48F92A97916C6DC5EA9";
    assertSerializesAndDeserializes(create, expectedBinary);
  }

  @Test
  void serializePaymentChannelClaimWithEmptyFlags() throws JsonProcessingException {
    PaymentChannelClaim claim = PaymentChannelClaim.builder()
      .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .balance(XrpCurrencyAmount.ofDrops(1000000))
      .amount(XrpCurrencyAmount.ofDrops(1000000))
      .signature("30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4779E" +
        "F4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B")
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000F21FFFFFFFF24000000015016C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749D" +
      "CDD3B9E5992CA61986140000000000F42406240000000000F424068400000000000000A712132D2471DB72B27E3310F355B" +
      "B33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A7300764630440220718D264EF05CAED7C781FF6DE298DCAC68D002562" +
      "C9BF3A07C1E721B420C0DAB02203A5A4779EF4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B81142042" +
      "88D2E47F8EF6C99BCC457966320D12409711";

    assertSerializesAndDeserializes(claim, expectedBinary);
  }

  @Test
  void serializePaymentChannelClaimWithZeroFlags() throws JsonProcessingException {
    PaymentChannelClaim claim = PaymentChannelClaim.builder()
      .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .balance(XrpCurrencyAmount.ofDrops(1000000))
      .amount(XrpCurrencyAmount.ofDrops(1000000))
      .signature("30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4779E" +
        "F4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B")
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .flags(PaymentChannelClaimFlags.of(0))
      .build();

    String expectedBinary = "12000F220000000024000000015016C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749D" +
      "CDD3B9E5992CA61986140000000000F42406240000000000F424068400000000000000A712132D2471DB72B27E3310F355B" +
      "B33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A7300764630440220718D264EF05CAED7C781FF6DE298DCAC68D002562" +
      "C9BF3A07C1E721B420C0DAB02203A5A4779EF4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B81142042" +
      "88D2E47F8EF6C99BCC457966320D12409711";

    assertSerializesAndDeserializes(claim, expectedBinary);
  }

  @Test
  void serializePaymentChannelClaimWithNonZeroFlags() throws JsonProcessingException {
    PaymentChannelClaim claim = PaymentChannelClaim.builder()
      .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .flags(PaymentChannelClaimFlags.builder().tfClose(true).build())
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .balance(XrpCurrencyAmount.ofDrops(1000000))
      .amount(XrpCurrencyAmount.ofDrops(1000000))
      .signature("30440220718D264EF05CAED7C781FF6DE298DCAC68D002562C9BF3A07C1E721B420C0DAB02203A5A4779E" +
        "F4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B")
      .publicKey("32D2471DB72B27E3310F355BB33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A")
      .build();

    String expectedBinary = "12000F228002000024000000015016C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749D" +
      "CDD3B9E5992CA61986140000000000F42406240000000000F424068400000000000000A712132D2471DB72B27E3310F355B" +
      "B33E339BF26F8392D5A93D3BC0FC3B566612DA0F0A7300764630440220718D264EF05CAED7C781FF6DE298DCAC68D002562" +
      "C9BF3A07C1E721B420C0DAB02203A5A4779EF4D2CCC7BC3EF886676D803A9981B928D3B8ACA483B80ECA3CD7B9B81142042" +
      "88D2E47F8EF6C99BCC457966320D12409711";

    assertSerializesAndDeserializes(claim, expectedBinary);
  }

  @Test
  void serializePaymentChannelFund() throws JsonProcessingException {
    PaymentChannelFund fund = PaymentChannelFund.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .amount(XrpCurrencyAmount.ofDrops(200000))
      .expiration(UnsignedLong.valueOf(543171558))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedJson = "12000E21FFFFFFFF24000000012A206023E65016C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC7" +
      "49DCDD3B9E5992CA6198614000000000030D4068400000000000000A730081144B4E9C06F24296074F7BC48F92A97916C6DC5EA9";

    assertSerializesAndDeserializes(fund, expectedJson);
  }

  @Test
  void serializePaymentChannelFundWithZeroFlags() throws JsonProcessingException {
    PaymentChannelFund fund = PaymentChannelFund.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .amount(XrpCurrencyAmount.ofDrops(200000))
      .expiration(UnsignedLong.valueOf(543171558))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedJson = "12000E220000000024000000012A206023E65016C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC7" +
      "49DCDD3B9E5992CA6198614000000000030D4068400000000000000A730081144B4E9C06F24296074F7BC48F92A97916C6DC5EA9";

    assertSerializesAndDeserializes(fund, expectedJson);
  }

  @Test
  void serializePaymentChannelFundWithNonZeroFlags() throws JsonProcessingException {
    PaymentChannelFund fund = PaymentChannelFund.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .channel(Hash256.of("C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC749DCDD3B9E5992CA6198"))
      .amount(XrpCurrencyAmount.ofDrops(200000))
      .expiration(UnsignedLong.valueOf(543171558))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedJson = "12000E228000000024000000012A206023E65016C1AE6DDDEEC05CF2978C0BAD6FE302948E9533691DC7" +
      "49DCDD3B9E5992CA6198614000000000030D4068400000000000000A730081144B4E9C06F24296074F7BC48F92A97916C6DC5EA9";

    assertSerializesAndDeserializes(fund, expectedJson);
  }

  @Test
  void serializeTrustSetWithEmptyFlags() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(44))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("WCG")
        .issuer(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
        .value("10000000")
        .build())
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12001421FFFFFFFF240000002C63D6438D7EA4C68000000000000000000000000000574347000000" +
      "0000832297BEF589D59F9C03A84F920F8D9128CC1CE468400000000000000C73008114BE6C30732AE33CF2AF3344CE8172A6B9" +
      "300183E3";
    assertSerializesAndDeserializes(trustSet, expectedBinary);
  }

  @Test
  void serializeTrustSetWithZeroFlags() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TrustSetFlags.of(0))
      .sequence(UnsignedInteger.valueOf(44))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("WCG")
        .issuer(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
        .value("10000000")
        .build())
      .build();

    String expectedBinary = "1200142200000000240000002C63D6438D7EA4C68000000000000000000000000000574347000000" +
      "0000832297BEF589D59F9C03A84F920F8D9128CC1CE468400000000000000C73008114BE6C30732AE33CF2AF3344CE8172A6B9" +
      "300183E3";
    assertSerializesAndDeserializes(trustSet, expectedBinary);
  }

  @Test
  void serializeTrustSetWithNonZeroFlags() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TrustSetFlags.builder()
        .tfSetNoRipple()
        .build())
      .sequence(UnsignedInteger.valueOf(44))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("WCG")
        .issuer(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
        .value("10000000")
        .build())
      .build();

    String expectedBinary = "1200142280020000240000002C63D6438D7EA4C68000000000000000000000000000574347000000" +
      "0000832297BEF589D59F9C03A84F920F8D9128CC1CE468400000000000000C73008114BE6C30732AE33CF2AF3344CE8172A6B9" +
      "300183E3";
    assertSerializesAndDeserializes(trustSet, expectedBinary);
  }

  @Test
  void serializeTrustSetWithMaxValue() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rBZwgHHifSeFmf2btuubr6YUhZ3FfW47XE"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(5))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("7872706C346A436F696E00000000000000000000")
        .issuer(Address.of("rwk7evAoUG1YoH1k3KVps3Zm1MtHuwHadj"))
        .value(IssuedCurrencyAmount.MAX_VALUE)
        .build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("EDD299D60BCE7980F6082945B5597FFFD35223F1950673BFA4D4AED6FDE5097156")
      )
      .build();

    String expectedBinary = "120014240000000563EC6386F26FC0FFFF7872706C346A436F696E00000000000000000000" +
      "6AF21A4FCA7AF4F62AA88E72CA923CDAE72FBC1668400000000000000A7321EDD299D60BCE7980F6082945B5597FFFD35223F19506" +
      "73BFA4D4AED6FDE5097156811473C6A5937EB779F5CF33600DB0ADB8556A497318";
    String transactionJson = objectMapper.writeValueAsString(trustSet);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);

    String decodedBinary = binaryCodec.decode(transactionBinary);
    TrustSet deserialized = objectMapper.readValue(
      decodedBinary,
      objectMapper.getTypeFactory().constructType(TrustSet.class)
    );

    assertThat(deserialized).usingRecursiveComparison().ignoringFields("limitAmount").isEqualTo(trustSet);
    BigDecimal deserializedValue = new BigDecimal(deserialized.limitAmount().value());
    BigDecimal originalValue = new BigDecimal(trustSet.limitAmount().value());
    assertThat(deserializedValue.toPlainString()).isEqualTo(originalValue.toPlainString());
  }

  @Test
  void serializeTrustSetWithMaxValueMinusOnePowerOfTen() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("r4V5fhT5KPCdhUanXCMpPUHiM4s6bXDVDH"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(5))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("7872706C346A436F696E00000000000000000000")
        .issuer(Address.of("rUcccYgErfgMifUHYDHed4ibTArjqfkU5R"))
        .value(new BigDecimal(IssuedCurrencyAmount.MAX_VALUE).scaleByPowerOfTen(-1).toString())
        .build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED930B5CBBBCA68DE43CC928E4F3860151E1E5F10D52478B38512651363F4DE8D0")
      )
      .build();

    String expectedBinary = "120014240000000563EC2386F26FC0FFFF7872706C346A436F696E00000000000000000000" +
      "7F65AB9FC20D6D3140698670A12869D6A7D9EE9568400000000000000A7321ED930B5CBBBCA68DE43CC928E4F3860151E1E5F10D52" +
      "478B38512651363F4DE8D08114EBC899C616EE2D4765C3CBBEB51E6131F701537C";
    String transactionJson = objectMapper.writeValueAsString(trustSet);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);

    String decodedBinary = binaryCodec.decode(transactionBinary);
    TrustSet deserialized = objectMapper.readValue(
      decodedBinary,
      objectMapper.getTypeFactory().constructType(TrustSet.class)
    );

    assertThat(deserialized).usingRecursiveComparison().ignoringFields("limitAmount").isEqualTo(trustSet);
    BigDecimal deserializedValue = new BigDecimal(deserialized.limitAmount().value());
    BigDecimal originalValue = new BigDecimal(trustSet.limitAmount().value());
    assertThat(deserializedValue.toPlainString()).isEqualTo(originalValue.toPlainString());
  }

  @Test
  void serializeTrustSetWithMaxValuePlusOnePowerOfTenThrows() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TrustSetFlags.builder()
        .tfSetNoRipple()
        .build())
      .sequence(UnsignedInteger.valueOf(44))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("WCG")
        .issuer(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
        .value(new BigDecimal(IssuedCurrencyAmount.MAX_VALUE).scaleByPowerOfTen(1).toEngineeringString())
        .build())
      .build();

    String transactionJson = objectMapper.writeValueAsString(trustSet);
    assertThatThrownBy(() -> binaryCodec.encode(transactionJson))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("exponent out of range");
  }

  @Test
  void serializeTrustSetWithMaxValuePlusOneThrows() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .flags(TrustSetFlags.builder()
        .tfSetNoRipple()
        .build())
      .sequence(UnsignedInteger.valueOf(44))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("WCG")
        .issuer(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
        .value(new BigDecimal(IssuedCurrencyAmount.MAX_VALUE).add(BigDecimal.ONE).toEngineeringString())
        .build())
      .build();

    String transactionJson = objectMapper.writeValueAsString(trustSet);
    assertThatThrownBy(() -> binaryCodec.encode(transactionJson))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageEndingWith("has more than 16 digits");
  }

  @Test
  void serializeTrustSetWithMinPositiveValue() throws JsonProcessingException {
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rLmnUtinq987W2LWcqz5Ty5BXgCUFG1PX4"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(5))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("7872706C346A436F696E00000000000000000000")
        .issuer(Address.of("rEPgyyv4xdWkKuavTmHLYPV2Ryqt7NNmH"))
        .value(IssuedCurrencyAmount.MIN_POSITIVE_VALUE)
        .build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED55D61E72F14EC798ABC10BC96381573717658B46F58E416EC0C74A467835AB4D")
      )
      .build();

    String expectedBinary = "120014240000000563C0438D7EA4C680007872706C346A436F696E00000000000" +
      "00000000002B8C37A261B75FC294AC84B40D249456715277D68400000000000000A7321ED55D61E72F14EC798ABC10BC9" +
      "6381573717658B46F58E416EC0C74A467835AB4D8114D8C033D20D216FD8939778C229F8BD9CF9585EAC";
    String transactionJson = objectMapper.writeValueAsString(trustSet);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);

    String decodedBinary = binaryCodec.decode(transactionBinary);
    TrustSet deserialized = objectMapper.readValue(
      decodedBinary,
      TrustSet.class
    );

    assertThat(deserialized).usingRecursiveComparison().ignoringFields("limitAmount").isEqualTo(trustSet);
    BigDecimal deserializedValue = new BigDecimal(deserialized.limitAmount().value());
    BigDecimal originalValue = new BigDecimal(trustSet.limitAmount().value());
    assertThat(deserializedValue.setScale(96).toString()).isEqualTo(originalValue.toString());
  }

  @Test
  void serializeTrustSetWithMinPositiveValuePlusOne() throws JsonProcessingException {
    BigDecimal value = new BigDecimal(IssuedCurrencyAmount.MIN_POSITIVE_VALUE)
      .add(new BigDecimal(IssuedCurrencyAmount.MIN_POSITIVE_VALUE).scaleByPowerOfTen(-1));
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rQrbagUaGHMTvbveDB3XBHyKB4pmWzzAup"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(5))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("7872706C346A436F696E00000000000000000000")
        .issuer(Address.of("r4e6zHaTwNK5WLtRdFiSY3UitarXqCMyoD"))
        .value(value.toString())
        .build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED098659726D4B0CDCD35E37135766FF2038579291351CC08DE70C32851D049A83")
      )
      .build();

    String expectedBinary = "120014240000000563C043E871B540C0007872706C346A436F696E000000000000000" +
      "00000ED7C3F2940D37A688A1D3F9AB883A8E3AB4D5FE168400000000000000A7321ED098659726D4B0CDCD35E37135766FF20" +
      "38579291351CC08DE70C32851D049A838114FC675B42929326571785885ECD4E61FA82F4AAAC";
    String transactionJson = objectMapper.writeValueAsString(trustSet);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);

    String decodedBinary = binaryCodec.decode(transactionBinary);
    TrustSet deserialized = objectMapper.readValue(
      decodedBinary,
      TrustSet.class
    );

    assertThat(deserialized).usingRecursiveComparison().ignoringFields("limitAmount").isEqualTo(trustSet);
    BigDecimal deserializedValue = new BigDecimal(deserialized.limitAmount().value());
    BigDecimal originalValue = new BigDecimal(trustSet.limitAmount().value());
    assertThat(deserializedValue.setScale(96).toString()).isEqualTo(originalValue.setScale(96).toString());
  }

  @Test
  void serializeTrustSetWithMinPositiveValueMinusOneThrows() throws JsonProcessingException {
    BigDecimal value = new BigDecimal(IssuedCurrencyAmount.MIN_POSITIVE_VALUE)
      .subtract(new BigDecimal(IssuedCurrencyAmount.MIN_POSITIVE_VALUE).scaleByPowerOfTen(-1));
    TrustSet trustSet = TrustSet.builder()
      .account(Address.of("rQrbagUaGHMTvbveDB3XBHyKB4pmWzzAup"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(5))
      .limitAmount(IssuedCurrencyAmount.builder()
        .currency("7872706C346A436F696E00000000000000000000")
        .issuer(Address.of("r4e6zHaTwNK5WLtRdFiSY3UitarXqCMyoD"))
        .value(value.toString())
        .build())
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED098659726D4B0CDCD35E37135766FF2038579291351CC08DE70C32851D049A83")
      )
      .build();

    String transactionJson = objectMapper.writeValueAsString(trustSet);
    assertThatThrownBy(() -> binaryCodec.encode(transactionJson))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("exponent out of range");
  }

  @Test
  void serializeRippleStateObjectWithMinBalanceValue() throws JsonProcessingException {
    RippleStateObject rippleStateObject = RippleStateObject.builder()
      .flags(RippleStateFlags.HIGH_NO_RIPPLE)
      .balance(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .lowLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_VALUE)
          .currency("USD")
          .build()
      )
      .highLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .previousTransactionId(Hash256.of(Strings.repeat("0", 64)))
      .previousTransactionLedgerSequence(UnsignedInteger.ONE)
      .index(Hash256.of(Strings.repeat("0", 64)))
      .build();

    String expectedBinary = "1100722200200000250000000155000000000000000000000000000000000000000000000000000000000" +
      "000000062AC6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E366" +
      "EC6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E367AC6386F26" +
      "FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E3";
    String transactionJson = objectMapper.writeValueAsString(rippleStateObject);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);
  }

  @Test
  void serializeRippleStateObjectWithMinBalanceValuePlusOne() throws JsonProcessingException {
    String value = new BigDecimal(IssuedCurrencyAmount.MIN_VALUE).subtract(
        new BigDecimal("-0.100000000000000E+95"))
      .toString();
    RippleStateObject rippleStateObject = RippleStateObject.builder()
      .flags(RippleStateFlags.HIGH_NO_RIPPLE)
      .balance(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(
            value
          )
          .currency("USD")
          .build()
      )
      .lowLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_VALUE)
          .currency("USD")
          .build()
      )
      .highLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .previousTransactionId(Hash256.of(Strings.repeat("0", 64)))
      .previousTransactionLedgerSequence(UnsignedInteger.ONE)
      .index(Hash256.of(Strings.repeat("0", 64)))
      .build();

    String expectedBinary = "11007222002000002500000001550000000000000000000000000000000000000000000000000000000" +
      "00000000062AC632BFF5F46BFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183" +
      "E366EC6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E367AC6" +
      "386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E3";
    String transactionJson = objectMapper.writeValueAsString(rippleStateObject);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);
  }

  @Test
  void serializeRippleStateObjectWithMinBalanceValueMinusOneThrows() throws JsonProcessingException {
    String value = new BigDecimal(IssuedCurrencyAmount.MIN_VALUE).subtract(new BigDecimal("1e80"))
      .toString();
    RippleStateObject rippleStateObject = RippleStateObject.builder()
      .flags(RippleStateFlags.HIGH_NO_RIPPLE)
      .balance(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(
            value
          )
          .currency("USD")
          .build()
      )
      .lowLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_VALUE)
          .currency("USD")
          .build()
      )
      .highLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .previousTransactionId(Hash256.of(Strings.repeat("0", 64)))
      .previousTransactionLedgerSequence(UnsignedInteger.ONE)
      .index(Hash256.of(Strings.repeat("0", 64)))
      .build();

    String transactionJson = objectMapper.writeValueAsString(rippleStateObject);
    assertThatThrownBy(
      () -> binaryCodec.encode(transactionJson)
    ).isInstanceOf(IllegalArgumentException.class)
      .hasMessage("exponent out of range");
  }

  @Test
  void serializeRippleStateObjectWithMaxNegativeValue() throws JsonProcessingException {
    RippleStateObject rippleStateObject = RippleStateObject.builder()
      .flags(RippleStateFlags.HIGH_NO_RIPPLE)
      .balance(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_NEGATIVE_VALUE)
          .currency("USD")
          .build()
      )
      .lowLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_VALUE)
          .currency("USD")
          .build()
      )
      .highLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .previousTransactionId(Hash256.of(Strings.repeat("0", 64)))
      .previousTransactionLedgerSequence(UnsignedInteger.ONE)
      .index(Hash256.of(Strings.repeat("0", 64)))
      .build();

    String expectedBinary = "110072220020000025000000015500000000000000000000000000000000000000000000000000" +
      "000000000000006280438D7EA4C680000000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172" +
      "A6B9300183E366EC6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6" +
      "B9300183E367AC6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9" +
      "300183E3";
    String transactionJson = objectMapper.writeValueAsString(rippleStateObject);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);
  }

  @Test
  void serializeRippleStateObjectWithMaxNegativeValueMinusOne() throws JsonProcessingException {
    String value = new BigDecimal(IssuedCurrencyAmount.MAX_NEGATIVE_VALUE)
      .add(new BigDecimal(IssuedCurrencyAmount.MAX_NEGATIVE_VALUE).scaleByPowerOfTen(-1)).toString();
    RippleStateObject rippleStateObject = RippleStateObject.builder()
      .flags(RippleStateFlags.HIGH_NO_RIPPLE)
      .balance(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(value)
          .currency("USD")
          .build()
      )
      .lowLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_VALUE)
          .currency("USD")
          .build()
      )
      .highLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .previousTransactionId(Hash256.of(Strings.repeat("0", 64)))
      .previousTransactionLedgerSequence(UnsignedInteger.ONE)
      .index(Hash256.of(Strings.repeat("0", 64)))
      .build();

    String expectedBinary = "11007222002000002500000001550000000000000000000000000000000000000000000000000000000" +
      "000000000628043E871B540C0000000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183" +
      "E366EC6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E367AC" +
      "6386F26FC0FFFF0000000000000000000000005553440000000000BE6C30732AE33CF2AF3344CE8172A6B9300183E3";
    String transactionJson = objectMapper.writeValueAsString(rippleStateObject);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);
  }

  @Test
  void serializeRippleStateObjectWithMaxNegativeValuePlusOneThrows() throws JsonProcessingException {
    String value = new BigDecimal(IssuedCurrencyAmount.MAX_NEGATIVE_VALUE)
      .subtract(new BigDecimal(IssuedCurrencyAmount.MAX_NEGATIVE_VALUE).scaleByPowerOfTen(-1)).toString();
    RippleStateObject rippleStateObject = RippleStateObject.builder()
      .flags(RippleStateFlags.HIGH_NO_RIPPLE)
      .balance(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(value)
          .currency("USD")
          .build()
      )
      .lowLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MAX_VALUE)
          .currency("USD")
          .build()
      )
      .highLimit(
        IssuedCurrencyAmount.builder()
          .issuer(Address.of("rJMiz2rCMjZzEMijXNH1exNBryTQEjFd9S"))
          .value(IssuedCurrencyAmount.MIN_VALUE)
          .currency("USD")
          .build()
      )
      .previousTransactionId(Hash256.of(Strings.repeat("0", 64)))
      .previousTransactionLedgerSequence(UnsignedInteger.ONE)
      .index(Hash256.of(Strings.repeat("0", 64)))
      .build();

    String transactionJson = objectMapper.writeValueAsString(rippleStateObject);
    assertThatThrownBy(() -> binaryCodec.encode(transactionJson))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("exponent out of range");
  }

  @Test
  public void serializeOfferCreateWithEmptyFlags() throws JsonProcessingException {
    OfferCreate offerCreate = OfferCreate.builder()
      .takerGets(currencyAmount(100))
      .takerPays(currencyAmount(200))
      .fee(XrpCurrencyAmount.ofDrops(300))
      .account(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .sequence(UnsignedInteger.valueOf(11223344))
      .offerSequence(UnsignedInteger.valueOf(123))
      .expiration(UnsignedInteger.valueOf(456))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000721FFFFFFFF2400AB41302A000001C820190000007B64D5071AFD498D0000000000000000000" +
      "0000000005743470000000000832297BEF589D59F9C03A84F920F8D9128CC1CE465D5038D7EA4C6800000000000000000000000" +
      "00005743470000000000832297BEF589D59F9C03A84F920F8D9128CC1CE468400000000000012C73008114832297BEF589D59F9" +
      "C03A84F920F8D9128CC1CE4";
    assertSerializesAndDeserializes(offerCreate, expectedBinary);
  }

  @Test
  public void serializeOfferCreateWithZeroFlags() throws JsonProcessingException {
    OfferCreate offerCreate = OfferCreate.builder()
      .takerGets(currencyAmount(100))
      .takerPays(currencyAmount(200))
      .fee(XrpCurrencyAmount.ofDrops(300))
      .account(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .sequence(UnsignedInteger.valueOf(11223344))
      .offerSequence(UnsignedInteger.valueOf(123))
      .flags(OfferCreateFlags.of(0))
      .expiration(UnsignedInteger.valueOf(456))
      .build();

    String expectedBinary = "12000722000000002400AB41302A000001C820190000007B64D5071AFD498D0000000000000000000" +
      "0000000005743470000000000832297BEF589D59F9C03A84F920F8D9128CC1CE465D5038D7EA4C6800000000000000000000000" +
      "00005743470000000000832297BEF589D59F9C03A84F920F8D9128CC1CE468400000000000012C73008114832297BEF589D59F9" +
      "C03A84F920F8D9128CC1CE4";
    assertSerializesAndDeserializes(offerCreate, expectedBinary);
  }

  @Test
  public void serializeOfferCreateWithNonZeroFlags() throws JsonProcessingException {
    OfferCreate offerCreate = OfferCreate.builder()
      .takerGets(currencyAmount(100))
      .takerPays(currencyAmount(200))
      .fee(XrpCurrencyAmount.ofDrops(300))
      .account(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .sequence(UnsignedInteger.valueOf(11223344))
      .offerSequence(UnsignedInteger.valueOf(123))
      .flags(OfferCreateFlags.builder().tfSell(true).build())
      .expiration(UnsignedInteger.valueOf(456))
      .build();

    String expectedBinary = "12000722800800002400AB41302A000001C820190000007B64D5071AFD498D0000000000000000000" +
      "0000000005743470000000000832297BEF589D59F9C03A84F920F8D9128CC1CE465D5038D7EA4C6800000000000000000000000" +
      "00005743470000000000832297BEF589D59F9C03A84F920F8D9128CC1CE468400000000000012C73008114832297BEF589D59F9" +
      "C03A84F920F8D9128CC1CE4";
    assertSerializesAndDeserializes(offerCreate, expectedBinary);
  }

  @Test
  public void serializeOfferCancel() throws JsonProcessingException {
    OfferCancel offerCreate = OfferCancel.builder()
      .fee(XrpCurrencyAmount.ofDrops(300))
      .account(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .sequence(UnsignedInteger.valueOf(11223344))
      .offerSequence(UnsignedInteger.valueOf(123))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000821FFFFFFFF2400AB413020190000007B68400000000000012C73008114832297BEF589" +
      "D59F9C03A84F920F8D9128CC1CE4";
    assertSerializesAndDeserializes(offerCreate, expectedBinary);
  }

  @Test
  public void serializeOfferCancelWithZeroFlags() throws JsonProcessingException {
    OfferCancel offerCreate = OfferCancel.builder()
      .fee(XrpCurrencyAmount.ofDrops(300))
      .account(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .sequence(UnsignedInteger.valueOf(11223344))
      .offerSequence(UnsignedInteger.valueOf(123))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "12000822000000002400AB413020190000007B68400000000000012C73008114832297BEF589" +
      "D59F9C03A84F920F8D9128CC1CE4";
    assertSerializesAndDeserializes(offerCreate, expectedBinary);
  }

  @Test
  public void serializeOfferCancelWithNonZeroFlags() throws JsonProcessingException {
    OfferCancel offerCreate = OfferCancel.builder()
      .fee(XrpCurrencyAmount.ofDrops(300))
      .account(Address.of("rUx4xgE7bNWCCgGcXv1CCoQyTcCeZ275YG"))
      .sequence(UnsignedInteger.valueOf(11223344))
      .offerSequence(UnsignedInteger.valueOf(123))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "12000822800000002400AB413020190000007B68400000000000012C73008114832297BEF589" +
      "D59F9C03A84F920F8D9128CC1CE4";
    assertSerializesAndDeserializes(offerCreate, expectedBinary);
  }

  @Test
  public void serializeSetRegularKey() throws JsonProcessingException {
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .regularKey(Address.of("rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD"))
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000521FFFFFFFF240000000168400000000000000C730081144B4E9C06F24296074F7BC48F92" +
      "A97916C6DC5EA988140A4B24D606281E6E5A78D9F80E039F5E66FA5AC5";

    assertSerializesAndDeserializes(setRegularKey, expectedBinary);
  }

  @Test
  public void serializeSetRegularKeyWithZeroFlags() throws JsonProcessingException {
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .regularKey(Address.of("rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD"))
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "1200052200000000240000000168400000000000000C730081144B4E9C06F24296074F7BC48F92" +
      "A97916C6DC5EA988140A4B24D606281E6E5A78D9F80E039F5E66FA5AC5";

    assertSerializesAndDeserializes(setRegularKey, expectedBinary);
  }

  @Test
  public void serializeSetRegularKeyWithNonZeroFlags() throws JsonProcessingException {
    SetRegularKey setRegularKey = SetRegularKey.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .regularKey(Address.of("rAR8rR8sUkBoCZFawhkWzY4Y5YoyuznwD"))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "1200052280000000240000000168400000000000000C730081144B4E9C06F24296074F7BC48F92" +
      "A97916C6DC5EA988140A4B24D606281E6E5A78D9F80E039F5E66FA5AC5";

    assertSerializesAndDeserializes(setRegularKey, expectedBinary);
  }

  @Test
  void serializeSignerListSet() throws JsonProcessingException {
    SignerListSet signerListSet = SignerListSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .signerQuorum(UnsignedInteger.valueOf(3))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
            .signerWeight(UnsignedInteger.valueOf(2))
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v"))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n"))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .networkId(NetworkId.of(UnsignedInteger.MAX_VALUE))
      .build();

    String expectedBinary = "12000C21FFFFFFFF240000000120230000000368400000000000000C730081144B4E9C06F24296074" +
      "F7BC48F92A97916C6DC5EA9F4EB1300028114204288D2E47F8EF6C99BCC457966320D12409711E1EB13000181147908A7F0EDD4" +
      "8EA896C3580A399F0EE78611C8E3E1EB13000181143A4C02EA95AD6AC3BED92FA036E0BBFB712C030CE1F1";

    assertSerializesAndDeserializes(signerListSet, expectedBinary);
  }

  @Test
  void serializeSignerListSetWithZeroFlags() throws JsonProcessingException {
    SignerListSet signerListSet = SignerListSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .signerQuorum(UnsignedInteger.valueOf(3))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
            .signerWeight(UnsignedInteger.valueOf(2))
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v"))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n"))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String expectedBinary = "12000C2200000000240000000120230000000368400000000000000C730081144B4E9C06F24296074" +
      "F7BC48F92A97916C6DC5EA9F4EB1300028114204288D2E47F8EF6C99BCC457966320D12409711E1EB13000181147908A7F0EDD4" +
      "8EA896C3580A399F0EE78611C8E3E1EB13000181143A4C02EA95AD6AC3BED92FA036E0BBFB712C030CE1F1";

    assertSerializesAndDeserializes(signerListSet, expectedBinary);
  }

  @Test
  void serializeSignerListSetWithNonZeroFlags() throws JsonProcessingException {
    SignerListSet signerListSet = SignerListSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .signerQuorum(UnsignedInteger.valueOf(3))
      .addSignerEntries(
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW"))
            .signerWeight(UnsignedInteger.valueOf(2))
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v"))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        ),
        SignerEntryWrapper.of(
          SignerEntry.builder()
            .account(Address.of("raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n"))
            .signerWeight(UnsignedInteger.ONE)
            .build()
        )
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String expectedBinary = "12000C2280000000240000000120230000000368400000000000000C730081144B4E9C06F24296074" +
      "F7BC48F92A97916C6DC5EA9F4EB1300028114204288D2E47F8EF6C99BCC457966320D12409711E1EB13000181147908A7F0EDD4" +
      "8EA896C3580A399F0EE78611C8E3E1EB13000181143A4C02EA95AD6AC3BED92FA036E0BBFB712C030CE1F1";

    assertSerializesAndDeserializes(signerListSet, expectedBinary);
  }

  @Test
  void serializeXChainAccountCreateCommitTest() throws JsonProcessingException {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .amount(XrpCurrencyAmount.ofDrops(1000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .destination(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
      .sequence(UnsignedInteger.ONE)
      .signatureReward(XrpCurrencyAmount.ofDrops(10000))
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey("0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB3265" +
        "4A313222F7FD020"))
      .transactionSignature(Signature.fromBase16(
        "304402202984DDE7F0B566F081F7953D7212BF031ACBF8860FE114102E9512C4C8768C770220701" +
          "13F4630B1DC3045E4A98DDD648CEBC31B12774F7B44A1B8123CD2C9F5CF18")
      )
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
          .issuingChainIssue(
            Issue.builder()
              .currency("ETH")
              .issuer(Address.of("rPyfep3gcLzkosKC9XiE77Y8DZWG6iWDT9"))
              .build()
          )
          .build()
      )
      .build();

    String expectedBinary = "12002C228000000024000000016140000000000F424068400000000000000A601D4000000000002" +
      "71073210330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD0207446304402202984DDE7F0B566F0" +
      "81F7953D7212BF031ACBF8860FE114102E9512C4C8768C77022070113F4630B1DC3045E4A98DDD648CEBC31B12774F7B44A1B" +
      "8123CD2C9F5CF188114B5F762798A53D543A014CAF8B297CFF8F2F937E88314AF80285F637EE4AF3C20378F9DFB12511ACB8D" +
      "27011914AF80285F637EE4AF3C20378F9DFB12511ACB8D27000000000000000000000000000000000000000014550FC62003E" +
      "785DC231A1058A05E56E3F09CF4E60000000000000000000000004554480000000000FBEF9A3A2B814E807745FA3D9C32FFD155FA2E8C";

    assertSerializesAndDeserializes(commit, expectedBinary);
  }

  @Test
  void serializeXChainAddAccountCreateAttestation() throws JsonProcessingException {
    XChainAddAccountCreateAttestation attestation = XChainAddAccountCreateAttestation.builder()
      .account(Address.of("r9cYxdjQsoXAEz3qQJc961SNLaXRkWXCvT"))
      .otherChainSource(Address.of("raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym"))
      .destination(Address.of("rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi"))
      .amount(XrpCurrencyAmount.ofDrops(10000000))
      .publicKey(
        PublicKey.fromBase16EncodedPublicKey("ED1F4A024ACFEBDB6C7AA88DEDE3364E060487EA31B14CC9E0D610D152B31AADC2")
      )
      .signature(
        Signature.fromBase16("EEFCFA3DC2AB4AB7C4D2EBBC168CB621A11B82BABD86534DFC8EFA72439A49662" +
          "D744073CD848E7A587A95B35162CDF9A69BB237E72C9537A987F5B8C394F30D")
      )
      .wasLockingChainSend(true)
      .attestationRewardAccount(Address.of("r9cYxdjQsoXAEz3qQJc961SNLaXRkWXCvT"))
      .attestationSignerAccount(Address.of("r9cYxdjQsoXAEz3qQJc961SNLaXRkWXCvT"))
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.valueOf("ffffffffffffffff", 16)))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .fee(XrpCurrencyAmount.ofDrops(20))
      .sequence(UnsignedInteger.valueOf(5))
      .lastLedgerSequence(UnsignedInteger.valueOf(13))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("EDF54108BA2E0A0D3DC2AE3897F8BE0EFE776AE8D0F9FB0D0B9D64233084A8DDD1")
      )
      .transactionSignature(
        Signature.fromBase16("03E74AEF1F585F156786429D2FC87A89E5C6B5A56D68BFC9A6A329F3AC67CBF" +
          "2B6958283C663A4522278CA162C69B23CF75149AF022B410EA0508C16F4205800")
      )
      .build();

    String expectedBinary = "12002E2400000005201B0000000D3015FFFFFFFFFFFFFFFF61400000000098968068400000000000001" +
      "4601D40000000000000647121ED1F4A024ACFEBDB6C7AA88DEDE3364E060487EA31B14CC9E0D610D152B31AADC27321EDF54108BA" +
      "2E0A0D3DC2AE3897F8BE0EFE776AE8D0F9FB0D0B9D64233084A8DDD1744003E74AEF1F585F156786429D2FC87A89E5C6B5A56D68B" +
      "FC9A6A329F3AC67CBF2B6958283C663A4522278CA162C69B23CF75149AF022B410EA0508C16F42058007640EEFCFA3DC2AB4AB7C4" +
      "D2EBBC168CB621A11B82BABD86534DFC8EFA72439A49662D744073CD848E7A587A95B35162CDF9A69BB237E72C9537A987F5B8C39" +
      "4F30D81145E7A3E3D7200A794FA801C66CE3775B6416EE4128314C15F113E49BCC4B9FFF43CD0366C23ACD82F75638012143FD9ED" +
      "9A79DEA67CB5D585111FEF0A29203FA0408014145E7A3E3D7200A794FA801C66CE3775B6416EE4128015145E7A3E3D7200A794FA8" +
      "01C66CE3775B6416EE4120010130101191486F0B1126CE1205E59FDFDD2661A9FB7505CA70F000000000000000000000000000000" +
      "000000000014B5F762798A53D543A014CAF8B297CFF8F2F937E80000000000000000000000000000000000000000";

    assertSerializesAndDeserializes(attestation, expectedBinary);
  }

  @Test
  void serializeXChainAddClaimAttestation() throws JsonProcessingException {
    XChainAddClaimAttestation attestation = XChainAddClaimAttestation.builder()
      .account(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .amount(XrpCurrencyAmount.ofDrops(10000000))
      .attestationRewardAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .attestationSignerAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .destination(Address.of("rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi"))
      .fee(XrpCurrencyAmount.ofDrops(20))
      .lastLedgerSequence(UnsignedInteger.valueOf(19))
      .otherChainSource(Address.of("raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym"))
      .sequence(UnsignedInteger.valueOf(9))
      .publicKey(
        PublicKey.fromBase16EncodedPublicKey("ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136")
      )
      .signature(
        Signature.fromBase16("7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44" +
          "528FE99AA50E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C")
      )
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C")
      )
      .wasLockingChainSend(true)
      .xChainBridge(
        XChainBridge.builder()
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .lockingChainDoor(Address.of("rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg"))
          .lockingChainIssue(Issue.XRP)
          .build()
      )
      .xChainClaimId(XChainClaimId.of(UnsignedLong.MAX_VALUE))
      .transactionSignature(
        Signature.fromBase16("D0423649E48A44F181262CF5FC08A68E7FA5CD9E55843E4F09014B76E602574" +
          "741E8553383A4B43CABD194BB96713647FC0B885BE248E4FFA068FA3E6994CF04")
      )
      .build();

    String expectedBinary = "12002D2400000009201B000000133014FFFFFFFFFFFFFFFF614000000000989680684000000000000014" +
      "7121ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E11367321ED0406B134786FE0751717226657F7BF" +
      "8AFE96442C05D28ACEC66FB64852BA604C7440D0423649E48A44F181262CF5FC08A68E7FA5CD9E55843E4F09014B76E602574741E8" +
      "553383A4B43CABD194BB96713647FC0B885BE248E4FFA068FA3E6994CF0476407C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2" +
      "A049474275153D9D4DD44528FE99AA50E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C81141F30A4D728AB98B0" +
      "950EC3B9815E6C8D43A7D5598314C15F113E49BCC4B9FFF43CD0366C23ACD82F75638012143FD9ED9A79DEA67CB5D585111FEF0A29" +
      "203FA0408014141F30A4D728AB98B0950EC3B9815E6C8D43A7D5598015141F30A4D728AB98B0950EC3B9815E6C8D43A7D559001013" +
      "0101191486F0B1126CE1205E59FDFDD2661A9FB7505CA70F000000000000000000000000000000000000000014B5F762798A53D543" +
      "A014CAF8B297CFF8F2F937E80000000000000000000000000000000000000000";

    assertSerializesAndDeserializes(attestation, expectedBinary);
  }

  @Test
  void serializeXChainClaim() throws JsonProcessingException {
    XChainClaim claim = XChainClaim.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020")
      )
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .transactionSignature(
        Signature.fromBase16("30440220445F7469FDA401787D9EE8A9B6E24DFF81E94F4C09FD311D2C0" +
          "A58FCC02C684A022029E2EF34A5EA35F50D5BB57AC6320AD3AE12C13C8D1379B255A486D72CED142E")
      )
      .xChainClaimId(XChainClaimId.of(UnsignedLong.MAX_VALUE))
      .destination(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .build();

    String expectedBinary = "12002B228000000024000000013014FFFFFFFFFFFFFFFF61400000000000271068400000000000000A" +
      "73210330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020744630440220445F7469FDA401787D9EE8" +
      "A9B6E24DFF81E94F4C09FD311D2C0A58FCC02C684A022029E2EF34A5EA35F50D5BB57AC6320AD3AE12C13C8D1379B255A486D72C" +
      "ED142E8114B5F762798A53D543A014CAF8B297CFF8F2F937E88314550FC62003E785DC231A1058A05E56E3F09CF4E6011914AF80" +
      "285F637EE4AF3C20378F9DFB12511ACB8D27000000000000000000000000000000000000000014550FC62003E785DC231A1058A05" +
      "E56E3F09CF4E60000000000000000000000000000000000000000";

    assertSerializesAndDeserializes(claim, expectedBinary);
  }

  @Test
  void serializeXChainCommit() throws JsonProcessingException {
    XChainCommit commit = XChainCommit.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.MAX_VALUE))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020")
      )
      .transactionSignature(
        Signature.fromBase16("3043021F177323F0D93612C82A4393A99B23905A7E675753FD80C52997AFA" +
          "B13F5F9D002203BFFAF457E90BDA65AABE8F8762BD96162FAD98A0C030CCD69B06EE9B12BBFFE")
      )
      .build();

    String expectedBinary = "12002A228000000024000000013014FFFFFFFFFFFFFFFF6140000000000027106840000000000000" +
      "0A73210330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD02074453043021F177323F0D93612C82A" +
      "4393A99B23905A7E675753FD80C52997AFAB13F5F9D002203BFFAF457E90BDA65AABE8F8762BD96162FAD98A0C030CCD69B06E" +
      "E9B12BBFFE8114B5F762798A53D543A014CAF8B297CFF8F2F937E8011914AF80285F637EE4AF3C20378F9DFB12511ACB8D2700" +
      "0000000000000000000000000000000000000014550FC62003E785DC231A1058A05E56E3F09CF4E60000000000000000000000" +
      "000000000000000000";

    assertSerializesAndDeserializes(commit, expectedBinary);
  }

  @Test
  void serializeXChainCreateBridge() throws JsonProcessingException {
    XChainCreateBridge createBridge = XChainCreateBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .signatureReward(XrpCurrencyAmount.ofDrops(1000))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .minAccountCreateAmount(XrpCurrencyAmount.ofDrops(10000))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020")
      )
      .transactionSignature(
        Signature.fromBase16("30440220101BCA4B5B5A37C6F44480F9A34752C9AA8B2CDF5AD47E3CB424DEDC" +
          "21C06DB702206EEB257E82A89B1F46A0A2C7F070B0BD181D980FF86FE4269E369F6FC7A27091")
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String binary = "1200302200000000240000000168400000000000000A601D40000000000003E8601E40000000000027" +
      "1073210330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020744630440220101BCA4B5B5A" +
      "37C6F44480F9A34752C9AA8B2CDF5AD47E3CB424DEDC21C06DB702206EEB257E82A89B1F46A0A2C7F070B0BD181D980F" +
      "F86FE4269E369F6FC7A270918114B5F762798A53D543A014CAF8B297CFF8F2F937E8011914AF80285F637EE4AF3C2037" +
      "8F9DFB12511ACB8D27000000000000000000000000000000000000000014550FC62003E785DC231A1058A05E56E3F09C" +
      "F4E60000000000000000000000000000000000000000";

    assertSerializesAndDeserializes(createBridge, binary);
  }

  @Test
  void serializeXChainCreateClaimId() throws JsonProcessingException {
    XChainCreateClaimId claimId = XChainCreateClaimId.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .otherChainSource(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
      .signatureReward(XrpCurrencyAmount.ofDrops(10000))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020")
      )
      .transactionSignature(
        Signature.fromBase16("30440220247B20A1B9C48E21A374CB9B3E1FE2A7C528151868DF8D307E9FBE" +
          "15237E531A02207C20C092DDCC525E583EF4AB7CB91E862A6DED19426997D3F0A2C84E2BE8C5DD")
      )
      .build();

    String binary = "1200292280000000240000000168400000000000000A601D400000000000271073210330E7FC9D56BB25D68" +
      "93BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020744630440220247B20A1B9C48E21A374CB9B3E1FE2A7C528151868" +
      "DF8D307E9FBE15237E531A02207C20C092DDCC525E583EF4AB7CB91E862A6DED19426997D3F0A2C84E2BE8C5DD8114B5F7627" +
      "98A53D543A014CAF8B297CFF8F2F937E8801214AF80285F637EE4AF3C20378F9DFB12511ACB8D27011914AF80285F637EE4AF" +
      "3C20378F9DFB12511ACB8D27000000000000000000000000000000000000000014550FC62003E785DC231A1058A05E56E3F0" +
      "9CF4E60000000000000000000000000000000000000000";

    assertSerializesAndDeserializes(claimId, binary);
  }

  @Test
  void serializeXChainModifyBridge() throws JsonProcessingException {
    XChainModifyBridge transaction = XChainModifyBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rGzx83BVoqTYbGn7tiVAnFw7cbxjin13jL"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .signatureReward(XrpCurrencyAmount.ofDrops(1000))
      .minAccountCreateAmount(XrpCurrencyAmount.ofDrops(10000))
      .flags(XChainModifyBridgeFlags.UNSET)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("0330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD020")
      )
      .transactionSignature(
        Signature.fromBase16("3045022100D2CABC1B0E0635A8EE2E6554F6D474C49BC292C995C5C9F83179F" +
          "4A60634B04C02205D1DB569D9593136F2FBEA7140010C8F46794D653AFDBEA8D30B8750BA4805E5")
      )
      .build();

    String binary = "12002F2200000000240000000168400000000000000A601D40000000000003E8601E40000000000027107" +
      "3210330E7FC9D56BB25D6893BA3F317AE5BCF33B3291BD63DB32654A313222F7FD02074473045022100D2CABC1B0E0635A8E" +
      "E2E6554F6D474C49BC292C995C5C9F83179F4A60634B04C02205D1DB569D9593136F2FBEA7140010C8F46794D653AFDBEA8D" +
      "30B8750BA4805E58114B5F762798A53D543A014CAF8B297CFF8F2F937E8011914AF80285F637EE4AF3C20378F9DFB12511AC" +
      "B8D27000000000000000000000000000000000000000014550FC62003E785DC231A1058A05E56E3F09CF4E60000000000000" +
      "000000000000000000000000000";

    assertSerializesAndDeserializes(transaction, binary);
  }

  @Test
  void serializeDidSet() throws JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rfmDuhDyLGgx94qiwf3YF8BUV5j6KSvE8"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .sequence(UnsignedInteger.valueOf(3))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED9861C4CB029C0DA737B823D7D3459A70F227958D5C0C111CC7CF947FC5A93347")
      )
      .transactionSignature(
        Signature.fromBase16("AACD31A04CAE14670FC483A1382F393AA96B49C84479B58067F049FBD7729993" +
          "25667A6AA2520A63756EE84F3657298815019DD56A1AECE796B08535C4009C08")
      )
      .didDocument(DidDocument.of("646F63"))
      .data(DidData.of("617474657374"))
      .uri(DidUri.of("6469645F6578616D706C65"))
      .build();

    String binary = "1200312280000000240000000368400000000000000A7321ED9861C4CB029C0DA737B823D7D3459A70F227" +
      "958D5C0C111CC7CF947FC5A933477440AACD31A04CAE14670FC483A1382F393AA96B49C84479B58067F049FBD77299932566" +
      "7A6AA2520A63756EE84F3657298815019DD56A1AECE796B08535C4009C08750B6469645F6578616D706C65701A03646F6370" +
      "1B06617474657374811401476926B590BA3245F63C829116A0A3AF7F382D";

    assertSerializesAndDeserializes(transaction, binary);
  }

  @Test
  void serializeDidSetWithEmptyValues() throws JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rfmDuhDyLGgx94qiwf3YF8BUV5j6KSvE8"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .sequence(UnsignedInteger.valueOf(3))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED9861C4CB029C0DA737B823D7D3459A70F227958D5C0C111CC7CF947FC5A93347")
      )
      .transactionSignature(
        Signature.fromBase16("AACD31A04CAE14670FC483A1382F393AA96B49C84479B58067F049FBD7729993" +
          "25667A6AA2520A63756EE84F3657298815019DD56A1AECE796B08535C4009C08")
      )
      .didDocument(DidDocument.of(""))
      .data(DidData.of(""))
      .uri(DidUri.of(""))
      .build();

    String binary = "1200312280000000240000000368400000000000000A7321ED9861C4CB029C0DA737B823D7D3459A70F2" +
      "27958D5C0C111CC7CF947FC5A933477440AACD31A04CAE14670FC483A1382F393AA96B49C84479B58067F049FBD7729993" +
      "25667A6AA2520A63756EE84F3657298815019DD56A1AECE796B08535C4009C087500701A00701B00811401476926B590BA" +
      "3245F63C829116A0A3AF7F382D";

    assertSerializesAndDeserializes(transaction, binary);
  }

  @Test
  void serializeDidDelete() throws JsonProcessingException {
    DidDelete transaction = DidDelete.builder()
      .account(Address.of("rfmDuhDyLGgx94qiwf3YF8BUV5j6KSvE8"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(4))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED9861C4CB029C0DA737B823D7D3459A70F227958D5C0C111CC7CF947FC5A93347")
      )
      .transactionSignature(
        Signature.fromBase16("71E28B12465A1B47162C22E121DF61089DCD9AAF5773704B76179E771666" +
          "886C8AAD5A33A87E34CC381A7D924E3FE3645F0BF98D565DE42C81E1A7A7E7981802")
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String binary = "1200322280000000240000000468400000000000000A7321ED9861C4CB029C0DA737B823D7D3459A70F22" +
      "7958D5C0C111CC7CF947FC5A93347744071E28B12465A1B47162C22E121DF61089DCD9AAF5773704B76179E771666886C8AA" +
      "D5A33A87E34CC381A7D924E3FE3645F0BF98D565DE42C81E1A7A7E7981802811401476926B590BA3245F63C829116A0A3AF7F382D";

    assertSerializesAndDeserializes(transaction, binary);
  }

  @Test
  void serializeOracleSet() throws JsonProcessingException {
    OracleSet oracleSet = OracleSet.builder()
      .account(Address.of("rMS69A6J39RmBg5yWDft5XAM8zTGbtMMZy"))
      .assetClass("63757272656E6379")
      .fee(XrpCurrencyAmount.ofDrops(10))
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.valueOf(3)))
      .provider(OracleProvider.of("68747470733A2F2F74687265657872702E646576"))
      .sequence(UnsignedInteger.valueOf(2019238))
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "EDA6501D3E53D47F10AE37A0C6B34194CF8A205DE9611FE81B63E7B62105E90EAC"
      ))
      .lastUpdateTime(UnsignedInteger.valueOf(1715785124))
      .addPriceDataSeries(
        PriceDataWrapper.of(
          PriceData.builder()
            .assetPrice(AssetPrice.of(UnsignedLong.valueOf("1ff4", 16)))
            .baseAsset("XRP")
            .quoteAsset("IDR")
            .build()
        ),
        PriceDataWrapper.of(
          PriceData.builder()
            .assetPrice(AssetPrice.of(UnsignedLong.valueOf("13652", 16)))
            .baseAsset("XRP")
            .quoteAsset("JPY")
            .scale(UnsignedInteger.valueOf(3))
            .build()
        ),
        PriceDataWrapper.of(
          PriceData.builder()
            .assetPrice(AssetPrice.of(UnsignedLong.valueOf("117bf", 16)))
            .baseAsset("XRP")
            .quoteAsset("KRW")
            .scale(UnsignedInteger.valueOf(2))
            .build()
        ),
        PriceDataWrapper.of(
          PriceData.builder()
            .assetPrice(AssetPrice.of(UnsignedLong.valueOf("14d6a", 16)))
            .baseAsset("XRP")
            .quoteAsset("MXN")
            .scale(UnsignedInteger.valueOf(4))
            .build()
        ),
        PriceDataWrapper.of(
          PriceData.builder()
            .assetPrice(AssetPrice.of(UnsignedLong.valueOf("5dfe", 16)))
            .baseAsset("XRP")
            .quoteAsset("MYR")
            .scale(UnsignedInteger.valueOf(4))
            .build()
        )
      )
      .transactionSignature(
        Signature.fromBase16("07205F518BC5D1D4F67E33101F140F69279670627825CDFA4BECDFE7C1D38" +
          "B40B4101F62264C280C3C9A492B3FF01DCF4AE0CEF12BB6FB2E4EB681B9B81D710A")
      )
      .build();

    String expectedBinary = "12003324001ECFA62F6644CDA420330000000368400000000000000A7321EDA6501D3E53D47" +
      "F10AE37A0C6B34194CF8A205DE9611FE81B63E7B62105E90EAC744007205F518BC5D1D4F67E33101F140F692796706278" +
      "25CDFA4BECDFE7C1D38B40B4101F62264C280C3C9A492B3FF01DCF4AE0CEF12BB6FB2E4EB681B9B81D710A701C0863757" +
      "272656E6379701D1468747470733A2F2F74687265657872702E6465768114E03E6F0A3D378C02E4CB7769053F8AA4EB3" +
      "649ECF018E02030170000000000001FF4011A0000000000000000000000000000000000000000021A000000000000000" +
      "0000000004944520000000000E1E02030170000000000013652041003011A00000000000000000000000000000000000" +
      "00000021A0000000000000000000000004A50590000000000E1E020301700000000000117BF041002011A00000000000" +
      "00000000000000000000000000000021A0000000000000000000000004B52570000000000E1E02030170000000000014" +
      "D6A041004011A0000000000000000000000000000000000000000021A0000000000000000000000004D584E000000000" +
      "0E1E02030170000000000005DFE041004011A0000000000000000000000000000000000000000021A000000000000000" +
      "0000000004D59520000000000E1F1";
    assertSerializesAndDeserializes(oracleSet, expectedBinary);
  }

  @Test
  void serializeOracleDelete() throws JsonProcessingException {
    OracleDelete oracleDelete = OracleDelete.builder()
      .account(Address.of("rsTkARdP8evWbRcAoD1ZJX7bDtAf5BVspg"))
      .fee(XrpCurrencyAmount.ofDrops(15))
      .sequence(UnsignedInteger.valueOf(10))
      .lastLedgerSequence(UnsignedInteger.valueOf(17))
      .signingPublicKey(PublicKey.fromBase16EncodedPublicKey(
        "ED6884529D21E8059F689827F5BF2AD5CBC138A3427A04C12B004C9BE9BBB22A77"
      ))
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .build();
    String expectedBinary = "120034240000000A201B0000001120330000000168400000000000000F7321ED6884529" +
      "D21E8059F689827F5BF2AD5CBC138A3427A04C12B004C9BE9BBB22A7781141AFFBE9553C7325305491B100D02E6BED8CE6BA5";

    assertSerializesAndDeserializes(oracleDelete, expectedBinary);
  }

  @Test
  void serializeAmmWithdraw() throws JsonProcessingException {
    AmmWithdraw ammWithdraw = AmmWithdraw.builder()
      .account(Address.of("rpgAg9CgNV9MYUECHTx26h1PEARhreuap7"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC110C234D04AF15F3F654A")
      )
      .flags(AmmWithdrawFlags.LP_TOKEN)
      .asset(Issue.builder()
        .currency("4755534400000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .asset2(Issue.builder()
        .currency("524C555344000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .lpTokensIn(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .amount2(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .effectivePrice(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .build();

    String expectedBinary = "1200252200010000240000000161D5071AFD498D00000000000000000000000000004C50540000" +
      "000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C568400000000000000A6BD5071AFD498D0000000000000000000000" +
      "0000004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5601AD5071AFD498D0000000000000000000000" +
      "0000004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5601BD5071AFD498D0000000000000000000000" +
      "0000004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5732102B4A8F64B97151FA303F86417751B7EA5" +
      "AF1D0014FCC110C234D04AF15F3F654A81141285FD19EDD3FB7146B87251AFB0E17742B37271031847555344000000000000" +
      "00000000000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C50418524C55534400000000000000000000000000" +
      "0000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5";
    assertSerializesAndDeserializes(ammWithdraw, expectedBinary);
  }

  @Test
  void serializeAmmBid() throws JsonProcessingException {
    AmmBid ammBid = AmmBid.builder()
      .account(Address.of("rpgAg9CgNV9MYUECHTx26h1PEARhreuap7"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC110C234D04AF15F3F654A"))
      .asset(Issue.builder()
        .currency("4755534400000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .asset2(Issue.builder()
        .currency("524C555344000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .bidMin(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .bidMax(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .addAuthAccounts(
        AuthAccountWrapper.of(AuthAccount.of(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy")))
      )
      .build();

    String expectedBinary = "120027240000000168400000000000000A6CD5071AFD498D00000000000000000000000000004C5" +
      "0540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C56DD5071AFD498D00000000000000000000000000004C5054" +
      "0000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5732102B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC11" +
      "0C234D04AF15F3F654A81141285FD19EDD3FB7146B87251AFB0E17742B37271F019E01B8114F78AD4FDA66653106AE45EE5FE" +
      "683C457E6BA9C5E1F103184755534400000000000000000000000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9" +
      "C50418524C555344000000000000000000000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5";

    assertSerializesAndDeserializes(ammBid, expectedBinary);
  }

  @Test
  void serializeAmmCreate() throws JsonProcessingException {
    AmmCreate ammCreate = AmmCreate.builder()
      .account(Address.of("rpgAg9CgNV9MYUECHTx26h1PEARhreuap7"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC110C234D04AF15F3F654A")
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .amount2(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .tradingFee(TradingFee.of(UnsignedInteger.ONE))
      .build();

    String expectedBinary = "120023150001240000000161D5071AFD498D00000000000000000000000000004C505400000000" +
      "00F78AD4FDA66653106AE45EE5FE683C457E6BA9C568400000000000000A6BD5071AFD498D00000000000000000000000000" +
      "004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5732102B4A8F64B97151FA303F86417751B7EA5AF1D" +
      "0014FCC110C234D04AF15F3F654A81141285FD19EDD3FB7146B87251AFB0E17742B37271";

    assertSerializesAndDeserializes(ammCreate, expectedBinary);
  }

  @Test
  void serializeAmmDelete() throws JsonProcessingException {
    AmmDelete ammDelete = AmmDelete.builder()
      .account(Address.of("rpgAg9CgNV9MYUECHTx26h1PEARhreuap7"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC110C234D04AF15F3F654A")
      )
      .asset(Issue.builder()
        .currency("4755534400000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .asset2(Issue.builder()
        .currency("524C555344000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .build();

    String expectedBinary = "120028240000000168400000000000000A732102B4A8F64B97151FA303F86417751B7EA5AF1" +
      "D0014FCC110C234D04AF15F3F654A81141285FD19EDD3FB7146B87251AFB0E17742B37271031847555344000000000000" +
      "00000000000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C50418524C5553440000000000000000000000" +
      "00000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5";

    assertSerializesAndDeserializes(ammDelete, expectedBinary);
  }

  @Test
  void serializeAmmDeposit() throws JsonProcessingException {
    AmmDeposit ammDeposit = AmmDeposit.builder()
      .account(Address.of("rpgAg9CgNV9MYUECHTx26h1PEARhreuap7"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC110C234D04AF15F3F654A")
      )
      .flags(AmmDepositFlags.SINGLE_ASSET)
      .asset(Issue.builder()
        .currency("4755534400000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .asset2(Issue.builder()
        .currency("524C555344000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .amount(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .amount2(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .effectivePrice(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .lpTokenOut(
        IssuedCurrencyAmount.builder()
          .value("200")
          .currency("LPT")
          .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
          .build()
      )
      .tradingFee(TradingFee.ofPercent(BigDecimal.ONE))
      .build();

    String expectedBinary = "1200241503E82200080000240000000161D5071AFD498D00000000000000000000000000004C50" +
      "540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C568400000000000000A6BD5071AFD498D0000000000000000" +
      "0000000000004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C56019D5071AFD498D0000000000000000" +
      "0000000000004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5601BD5071AFD498D0000000000000000" +
      "0000000000004C50540000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5732102B4A8F64B97151FA303F8641775" +
      "1B7EA5AF1D0014FCC110C234D04AF15F3F654A81141285FD19EDD3FB7146B87251AFB0E17742B37271031847555344000000" +
      "00000000000000000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C50418524C55534400000000000000000000" +
      "0000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5";

    assertSerializesAndDeserializes(ammDeposit, expectedBinary);
  }

  @Test
  void serializeAmmVote() throws JsonProcessingException {
    AmmVote ammVote = AmmVote.builder()
      .account(Address.of("rpgAg9CgNV9MYUECHTx26h1PEARhreuap7"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02B4A8F64B97151FA303F86417751B7EA5AF1D0014FCC110C234D04AF15F3F654A")
      )
      .asset(Issue.builder()
        .currency("4755534400000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .asset2(Issue.builder()
        .currency("524C555344000000000000000000000000000000")
        .issuer(Address.of("rPZtDb6ZHtfYzMmzyUGvehoi2vYhrNAPhy"))
        .build()
      )
      .tradingFee(TradingFee.ofPercent(BigDecimal.ONE))
      .build();

    String expectedBinary = "1200261503E8240000000168400000000000000A732102B4A8F64B97151FA303F86417751B7" +
      "EA5AF1D0014FCC110C234D04AF15F3F654A81141285FD19EDD3FB7146B87251AFB0E17742B37271031847555344000000" +
      "00000000000000000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C50418524C55534400000000000000000" +
      "0000000000000F78AD4FDA66653106AE45EE5FE683C457E6BA9C5";

    assertSerializesAndDeserializes(ammVote, expectedBinary);
  }

  private <T extends Transaction> void assertSerializesAndDeserializes(
    T transaction,
    String expectedBinary
  ) throws JsonProcessingException {
    String transactionJson = objectMapper.writeValueAsString(transaction);
    String transactionBinary = binaryCodec.encode(transactionJson);
    assertThat(transactionBinary).isEqualTo(expectedBinary);

    String decodedBinary = binaryCodec.decode(transactionBinary);
    T deserialized = objectMapper.readValue(
      decodedBinary,
      objectMapper.getTypeFactory().constructType(transaction.getClass())
    );
    assertThat(deserialized).isEqualTo(transaction);
  }

}
