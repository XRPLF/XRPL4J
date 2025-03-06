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
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.TransactionMetadata;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

/**
 * Integration test to validate submission of Payment transactions.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SubmitPaymentIT extends AbstractIT {

  public static final String SUCCESS_STATUS = "tesSUCCESS";

  @Test
  public void sendPayment() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair sourceKeyPair = createRandomAccountEd25519();
    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );
    XrpCurrencyAmount amount = XrpCurrencyAmount.ofDrops(12345);
    Payment payment = Payment.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .amount(amount)
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<Payment> signedPayment = signatureService.sign(sourceKeyPair.privateKey(), payment);
    SubmitResult<Payment> result = xrplClient.submit(signedPayment);
    assertThat(result.engineResult()).isEqualTo(SUCCESS_STATUS);
    logSubmitResult(result);

    TransactionResult<Payment> validatedPayment = this.scanForResult(
      () -> this.getValidatedTransaction(result.transactionResult().hash(), Payment.class)
    );

    assertThat(validatedPayment.metadata().flatMap(TransactionMetadata::deliveredAmount)).hasValue(amount);
    assertThat(validatedPayment.metadata().get().transactionResult()).isEqualTo(SUCCESS_STATUS);

    assertPaymentCloseTimeMatchesLedgerCloseTime(validatedPayment);
  }

  @Test
  public void sendPaymentFromSecp256k1KeyPair() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair senderKeyPair = this.createRandomAccountSecp256k1();
    logger.info("Generated source testnet wallet with address " + senderKeyPair.publicKey().deriveAddress());

    KeyPair destinationKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(senderKeyPair.publicKey().deriveAddress())
    );

    Payment payment = Payment.builder()
      .account(senderKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(destinationKeyPair.publicKey().deriveAddress())
      .amount(XrpCurrencyAmount.ofDrops(12345))
      .signingPublicKey(senderKeyPair.publicKey())
      .build();

    SingleSignedTransaction<Payment> signedPayment = signatureService.sign(senderKeyPair.privateKey(), payment);
    SubmitResult<Payment> result = xrplClient.submit(signedPayment);
    assertThat(result.engineResult()).isEqualTo("tesSUCCESS");
    logSubmitResult(result);

    this.scanForResult(() -> this.getValidatedTransaction(result.transactionResult().hash(), Payment.class));
  }

  private void assertPaymentCloseTimeMatchesLedgerCloseTime(TransactionResult<Payment> validatedPayment)
    throws JsonRpcClientErrorException {

    LedgerResult ledger = this.scanForResult(
      () -> {
        try {
          return xrplClient.ledger(
            LedgerRequestParams.builder()
              .ledgerSpecifier(LedgerSpecifier.of(validatedPayment.ledgerIndex().get()))
              .build()
          );
        } catch (JsonRpcClientErrorException e) {
          throw new RuntimeException(e);
        }
      }
    );

    assertThat(validatedPayment.closeDateHuman()).isNotEmpty();
    assertThat(ledger.ledger().closeTimeHuman()).isNotEmpty();
    assertThat(validatedPayment.closeDateHuman()).isEqualTo(ledger.ledger().closeTimeHuman());
  }

}
