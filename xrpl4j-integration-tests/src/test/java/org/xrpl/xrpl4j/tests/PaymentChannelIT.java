package org.xrpl.xrpl4j.tests;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountChannelsRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountChannelsResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.PaymentChannelResultObject;
import org.xrpl.xrpl4j.model.client.channels.ChannelVerifyRequestParams;
import org.xrpl.xrpl4j.model.client.channels.ChannelVerifyResult;
import org.xrpl.xrpl4j.model.client.channels.UnsignedClaim;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.ledger.OfferLedgerEntryParams;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.ledger.OfferObject;
import org.xrpl.xrpl4j.model.ledger.PayChannelObject;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelClaim;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelCreate;
import org.xrpl.xrpl4j.model.transactions.PaymentChannelFund;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.time.Duration;
import java.time.Instant;

/**
 * An Integration Test to validate submission of PaymentChannel transactions.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class PaymentChannelIT extends AbstractIT {

  @Test
  public void createPaymentChannel() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////////
    // Create source and destination accounts on ledger
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    //////////////////////////
    // Submit a PaymentChannelCreate transaction to create a payment channel between
    // the source and destination accounts
    PaymentChannelCreate paymentChannelCreate = PaymentChannelCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(senderAccountInfo.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .settleDelay(UnsignedInteger.ONE)
      .publicKey(sourceKeyPair.publicKey().base16Value())
      .cancelAfter(UnsignedLong.valueOf(533171558))
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();
    SingleSignedTransaction<PaymentChannelCreate> signedPaymentChannelCreate = signatureService.sign(
      sourceKeyPair.privateKey(), paymentChannelCreate
    );
    //////////////////////////
    // Validate that the transaction was submitted successfully
    SubmitResult<PaymentChannelCreate> createResult = xrplClient.submit(signedPaymentChannelCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////////
    // Wait for the payment channel to exist in a validated ledger
    // and validate its fields
    PaymentChannelResultObject paymentChannel = scanForResult(
      () -> getValidatedAccountChannels(sourceKeyPair.publicKey().deriveAddress()),
      channels -> channels.channels().stream()
        .anyMatch(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
    )
      .channels().stream()
      .filter(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Could not find payment channel for destination address."));

    assertThat(paymentChannel.amount()).isEqualTo(paymentChannelCreate.amount());
    assertThat(paymentChannel.settleDelay()).isEqualTo(paymentChannelCreate.settleDelay());
    assertThat(paymentChannel.publicKeyHex()).isNotEmpty().get().isEqualTo(paymentChannelCreate.publicKey());
    assertThat(paymentChannel.cancelAfter()).isNotEmpty().get().isEqualTo(paymentChannelCreate.cancelAfter().get());

    //////////////////////////
    // Also validate that the channel exists in the account's objects
    PayChannelObject payChannelObject = scanForPayChannelObject(sourceKeyPair, destinationKeyPair);
    assertThatEntryEqualsObjectFromAccountObjects(payChannelObject);

    //////////////////////////
    // Validate that the amount of the payment channel was deducted from the source
    // accounts XRP balance
    AccountInfoResult senderAccountInfoAfterCreate = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    assertThat(senderAccountInfoAfterCreate.accountData().balance())
      .isEqualTo(senderAccountInfo.accountData().balance()
        .minus(paymentChannelCreate.amount())
        .minus(paymentChannelCreate.fee())
      );
  }

  @Test
  void createAndClaimPaymentChannel() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////////
    // Create source and destination accounts on ledger
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    //////////////////////////
    // Submit a PaymentChannelCreate transaction to create a payment channel between the source and destination accounts
    PaymentChannelCreate paymentChannelCreate = PaymentChannelCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(senderAccountInfo.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(10000000))
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .settleDelay(UnsignedInteger.ONE)
      .publicKey(sourceKeyPair.publicKey().base16Value())
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    //////////////////////////
    // Validate that the transaction was submitted successfully
    SingleSignedTransaction<PaymentChannelCreate> signedPaymentChannelCreate = signatureService.sign(
      sourceKeyPair.privateKey(), paymentChannelCreate
    );
    SubmitResult<PaymentChannelCreate> createResult = xrplClient.submit(signedPaymentChannelCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////////
    // Wait for the payment channel to exist in a validated ledger
    // and validate its fields
    PaymentChannelResultObject paymentChannel = scanForResult(
      () -> getValidatedAccountChannels(sourceKeyPair.publicKey().deriveAddress()),
      channels -> channels.channels().stream()
        .anyMatch(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
    )
      .channels().stream()
      .filter(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Could not find payment channel for destination address."));

    assertThat(paymentChannel.amount()).isEqualTo(paymentChannelCreate.amount());
    assertThat(paymentChannel.settleDelay()).isEqualTo(paymentChannelCreate.settleDelay());
    assertThat(paymentChannel.publicKeyHex()).isNotEmpty().get().isEqualTo(paymentChannelCreate.publicKey());
    assertThat(paymentChannel.cancelAfter()).isEmpty();

    PayChannelObject payChannelObject = scanForPayChannelObject(sourceKeyPair, destinationKeyPair);
    assertThatEntryEqualsObjectFromAccountObjects(payChannelObject);

    AccountInfoResult destinationAccountInfo = scanForResult(
      () -> getValidatedAccountInfo(destinationKeyPair.publicKey().deriveAddress())
    );

    //////////////////////////
    // Source account signs a claim
    UnsignedClaim unsignedClaim = UnsignedClaim.builder()
      .channel(paymentChannel.channelId())
      .amount(XrpCurrencyAmount.ofDrops(1000000))
      .build();

    Signature signedClaimSignature = signatureService.sign(sourceKeyPair.privateKey(), unsignedClaim);

    //////////////////////////
    // Destination account verifies the claim signature
    ChannelVerifyResult channelVerifyResult = xrplClient.channelVerify(
      ChannelVerifyRequestParams.builder()
        .channelId(paymentChannel.channelId())
        .amount(unsignedClaim.amount())
        .signature(signedClaimSignature.base16Value())
        .publicKey(sourceKeyPair.publicKey().base16Value())
        .build()
    );
    assertThat(channelVerifyResult.signatureVerified()).isTrue();

    //////////////////////////
    // Destination account submits the signed claim to the ledger to get their XRP
    PaymentChannelClaim paymentChannelClaim = PaymentChannelClaim.builder()
      .account(destinationKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(destinationAccountInfo.accountData().sequence())
      .channel(paymentChannel.channelId())
      .balance(paymentChannel.balance().plus(unsignedClaim.amount()))
      .amount(unsignedClaim.amount())
      .signature(signedClaimSignature.base16Value())
      .publicKey(sourceKeyPair.publicKey().base16Value())
      .signingPublicKey(destinationKeyPair.publicKey())
      .build();

    SingleSignedTransaction<PaymentChannelClaim> signedPaymentChannelClaim = signatureService.sign(
      destinationKeyPair.privateKey(), paymentChannelClaim
    );
    SubmitResult<PaymentChannelClaim> claimResult = xrplClient.submit(signedPaymentChannelClaim);
    assertThat(claimResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(claimResult);

    //////////////////////////
    // Validate that the destination account balance has gone up by the claim amount
    this.scanForResult(
      () -> this.getValidatedAccountInfo(destinationKeyPair.publicKey().deriveAddress()),
      infoResult -> infoResult.accountData().balance().equals(
        destinationAccountInfo.accountData().balance()
          .minus(paymentChannelClaim.fee())
          .plus(paymentChannelClaim.balance().get())
      )
    );

    PayChannelObject payChannelObjectAfterClaim = scanForPayChannelObject(sourceKeyPair, destinationKeyPair);
    assertThatEntryEqualsObjectFromAccountObjects(payChannelObjectAfterClaim);
  }

  @Test
  void createAddFundsAndSetExpirationToPaymentChannel() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////////
    // Create source and destination accounts on ledger
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    //////////////////////////
    // Submit a PaymentChannelCreate transaction to create a payment channel between
    // the source and destination accounts
    PaymentChannelCreate paymentChannelCreate = PaymentChannelCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(senderAccountInfo.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(10_000_000)) // <-- 10 XRP or 10m drops
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .settleDelay(UnsignedInteger.ONE)
      .publicKey(sourceKeyPair.publicKey().base16Value())
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    //////////////////////////
    // Validate that the transaction was submitted successfully
    SingleSignedTransaction<PaymentChannelCreate> signedPaymentChannelCreate = signatureService.sign(
      sourceKeyPair.privateKey(), paymentChannelCreate
    );
    SubmitResult<PaymentChannelCreate> createResult = xrplClient.submit(signedPaymentChannelCreate);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////////
    // Wait for the payment channel to exist in a validated ledger and validate its fields
    PaymentChannelResultObject paymentChannel = scanForResult(
      () -> getValidatedAccountChannels(sourceKeyPair.publicKey().deriveAddress()),
      channels -> channels.channels().stream()
        .anyMatch(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
    )
      .channels().stream()
      .filter(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Could not find payment channel for destination address."));

    assertThat(paymentChannel.amount()).isEqualTo(paymentChannelCreate.amount());
    assertThat(paymentChannel.settleDelay()).isEqualTo(paymentChannelCreate.settleDelay());
    assertThat(paymentChannel.publicKeyHex()).isNotEmpty().get().isEqualTo(paymentChannelCreate.publicKey());
    assertThat(paymentChannel.cancelAfter()).isEmpty();

    PayChannelObject payChannelObject = scanForPayChannelObject(sourceKeyPair, destinationKeyPair);
    assertThatEntryEqualsObjectFromAccountObjects(payChannelObject);

    PaymentChannelFund paymentChannelFund = PaymentChannelFund.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(senderAccountInfo.accountData().sequence().plus(UnsignedInteger.ONE))
      .signingPublicKey(sourceKeyPair.publicKey())
      .channel(paymentChannel.channelId())
      .amount(XrpCurrencyAmount.ofDrops(10_000)) // <-- 10k drops
      .build();

    //////////////////////////
    // Validate that the transaction was submitted successfully
    SingleSignedTransaction<PaymentChannelFund> signedPaymentChannelFund = signatureService.sign(
      sourceKeyPair.privateKey(), paymentChannelFund
    );
    SubmitResult<PaymentChannelFund> fundResult = xrplClient.submit(signedPaymentChannelFund);
    assertThat(fundResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(fundResult);

    //////////////////////////
    // Validate that the amount in the channel increased by the fund amount
    scanForResult(
      () -> getValidatedAccountChannels(sourceKeyPair.publicKey().deriveAddress()),
      channelsResult -> channelsResult.channels().stream()
        .anyMatch(channel -> {
            logger.warn("PAYCHAN: channel={} paymentChannel={}", channel, paymentChannel);

            return channel.channelId().equals(paymentChannel.channelId()) &&
              channel.amount().equals(paymentChannel.amount().plus(paymentChannelFund.amount()));
          }
        )
    );

    PayChannelObject payChannelObjectAfterFund = scanForPayChannelObject(sourceKeyPair, destinationKeyPair);
    assertThatEntryEqualsObjectFromAccountObjects(payChannelObjectAfterFund);

    //////////////////////////
    // Then set a new expiry on the channel by submitting a PaymentChannelFund
    // transaction with an expiration and 1 drop of XRP in the amount field
    UnsignedLong newExpiry = instantToXrpTimestamp(Instant.now())
      .plus(UnsignedLong.valueOf(paymentChannel.settleDelay().longValue()))
      .plus(UnsignedLong.valueOf(300000));

    PaymentChannelFund paymentChannelFundWithNewExpiry = PaymentChannelFund.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(senderAccountInfo.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
      .signingPublicKey(sourceKeyPair.publicKey())
      .channel(paymentChannel.channelId())
      .amount(XrpCurrencyAmount.ofDrops(1))
      .expiration(newExpiry)
      .build();

    //////////////////////////
    // Validate that the transaction was submitted successfully
    SingleSignedTransaction<PaymentChannelFund> signedPaymentChannelFundWithExpiry = signatureService.sign(
      sourceKeyPair.privateKey(), paymentChannelFundWithNewExpiry
    );
    SubmitResult<PaymentChannelFund> expiryResult = xrplClient.submit(signedPaymentChannelFundWithExpiry);
    assertThat(expiryResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(expiryResult);

    //////////////////////////
    // Validate that the expiration was set properly
    scanForResult(
      () -> getValidatedAccountChannels(sourceKeyPair.publicKey().deriveAddress()),
      channelsResult -> channelsResult.channels().stream()
        .anyMatch(
          channel ->
            channel.channelId().equals(paymentChannel.channelId()) &&
              channel.expiration().isPresent() &&
              channel.expiration().get().equals(newExpiry)
        )
    );

    PayChannelObject payChannelObjectAfterExpiryBump = scanForPayChannelObject(sourceKeyPair, destinationKeyPair);
    assertThatEntryEqualsObjectFromAccountObjects(payChannelObjectAfterExpiryBump);
  }

  @Test
  void testCurrentAccountChannels() throws JsonRpcClientErrorException, JsonProcessingException {
    //////////////////////////
    // Create source and destination accounts on ledger
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult senderAccountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    //////////////////////////
    // Submit a PaymentChannelCreate transaction to create a payment channel between
    // the source and destination accounts
    PaymentChannelCreate createPaymentChannel = PaymentChannelCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(senderAccountInfo.accountData().sequence())
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .settleDelay(UnsignedInteger.ONE)
      .publicKey(sourceKeyPair.publicKey().base16Value())
      .cancelAfter(UnsignedLong.valueOf(533171558))
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    //////////////////////////
    // Validate that the transaction was submitted successfully
    SingleSignedTransaction<PaymentChannelCreate> signedCreatePaymentChannel = signatureService.sign(
      sourceKeyPair.privateKey(), createPaymentChannel
    );
    SubmitResult<PaymentChannelCreate> createResult = xrplClient.submit(signedCreatePaymentChannel);
    assertThat(createResult.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(createResult);

    //////////////////////////
    // Wait for the payment channel to exist in a validated ledger
    // and validate its fields
    AccountChannelsResult accountChannelsResult = scanForResult(
      () -> {
        try {
          return xrplClient.accountChannels(AccountChannelsRequestParams.builder()
            .account(sourceKeyPair.publicKey().deriveAddress())
            .ledgerSpecifier(LedgerSpecifier.CURRENT)
            .build());
        } catch (JsonRpcClientErrorException e) {
          throw new RuntimeException(e);
        }
      },
      channels -> channels.channels().stream()
        .anyMatch(channel -> channel.destinationAccount().equals(destinationKeyPair.publicKey().deriveAddress()))
    );

    assertThat(accountChannelsResult.ledgerHash()).isEmpty();
    assertThat(accountChannelsResult.ledgerIndex()).isEmpty();
    assertThat(accountChannelsResult.ledgerCurrentIndex()).isNotEmpty();
  }

  private PayChannelObject scanForPayChannelObject(KeyPair sourceKeyPair, KeyPair destinationKeyPair) {
    return (PayChannelObject) scanForResult(
      () -> getValidatedAccountObjects(sourceKeyPair.publicKey().deriveAddress()),
      objectsResult -> objectsResult.accountObjects().stream()
        .anyMatch(object ->
          PayChannelObject.class.isAssignableFrom(object.getClass()) &&
            ((PayChannelObject) object).destination().equals(destinationKeyPair.publicKey().deriveAddress())
        )
    ).accountObjects().stream()
      .filter(object -> PayChannelObject.class.isAssignableFrom(object.getClass()) &&
        ((PayChannelObject) object).destination().equals(destinationKeyPair.publicKey().deriveAddress()))
      .findFirst()
      .get();
  }

  private void assertThatEntryEqualsObjectFromAccountObjects(PayChannelObject payChannelObject)
    throws JsonRpcClientErrorException {
    LedgerEntryResult<PayChannelObject> entry = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.paymentChannel(
        payChannelObject.index(),
        LedgerSpecifier.VALIDATED
      )
    );

    LedgerEntryResult<PayChannelObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(payChannelObject.index(), PayChannelObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entry.node());

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(payChannelObject.index(), LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());
  }
}
