package org.xrpl.xrpl4j.tests;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: integration-tests
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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.PrivateKeyReference;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.transactions.Memo;
import org.xrpl.xrpl4j.model.transactions.MemoWrapper;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class TransactionWithMemoIT extends AbstractIT {

  /**
   * Tests a transaction that has a memo containing only a nibble (i.e., a half byte).
   */
  @Test
  public void transactionWithMemoNibble() throws JsonRpcClientErrorException, JsonProcessingException {
    PrivateKeyReference sourcePrivateKey = createRandomPrivateKeyReferenceEd25519();
    PrivateKeyReference destinationPrivateKey = createRandomPrivateKeyReferenceEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(toAddress(sourcePrivateKey))
    );
    XrpCurrencyAmount amount = XrpCurrencyAmount.ofDrops(12345);
    Payment payment = Payment.builder()
      .account(toAddress(sourcePrivateKey))
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(toAddress(destinationPrivateKey))
      .amount(amount)
      .signingPublicKey(toPublicKey(sourcePrivateKey))
      .addMemos(MemoWrapper.builder()
        .memo(Memo.builder()
          .memoData("A")
          .build())
        .build())
      .build();

    SingleSignedTransaction<Payment> signedTransaction = derivedKeySignatureService.sign(sourcePrivateKey, payment);
    SubmitResult<Payment> result = xrplClient.submit(signedTransaction);
    assertThat(result.engineResult()).isEqualTo(SUCCESS_STATUS);
    assertThat(signedTransaction.hash()).isEqualTo(result.transactionResult().hash());
    logSubmitResult(result);

    TransactionResult<Payment> validatedPayment = this.scanForResult(
      () -> this.getValidatedTransaction(
        result.transactionResult().hash(),
        Payment.class)
    );

    assertThat(validatedPayment.metadata().get().deliveredAmount()).hasValue(amount);
    assertThat(validatedPayment.metadata().get().transactionResult()).isEqualTo(SUCCESS_STATUS);
  }

  /**
   * Tests a transaction that has a memo containing only a nibble (i.e., a half byte).
   */
  @Test
  public void transactionWithPlaintextMemo() throws JsonRpcClientErrorException, JsonProcessingException {
    PrivateKeyReference sourcePrivateKey = createRandomPrivateKeyReferenceEd25519();
    PrivateKeyReference destinationPrivateKey = createRandomPrivateKeyReferenceEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(toAddress(sourcePrivateKey))
    );
    XrpCurrencyAmount amount = XrpCurrencyAmount.ofDrops(12345);
    Payment payment = Payment.builder()
      .account(toAddress(sourcePrivateKey))
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(toAddress(destinationPrivateKey))
      .amount(amount)
      .signingPublicKey(toPublicKey(sourcePrivateKey))
      .addMemos(MemoWrapper.builder()
        .memo(Memo.withPlaintext("Hello World").build())
        .build()
      )
      .build();

    SingleSignedTransaction<Payment> signedTransaction = derivedKeySignatureService.sign(sourcePrivateKey, payment);
    SubmitResult<Payment> result = xrplClient.submit(signedTransaction);

    assertThat(result.engineResult()).isEqualTo(SUCCESS_STATUS);
    assertThat(signedTransaction.hash()).isEqualTo(result.transactionResult().hash());
    logSubmitResult(result);

    TransactionResult<Payment> validatedPayment = this.scanForResult(
      () -> this.getValidatedTransaction(
        result.transactionResult().hash(),
        Payment.class)
    );

    assertThat(validatedPayment.metadata().get().deliveredAmount()).hasValue(amount);
    assertThat(validatedPayment.metadata().get().transactionResult()).isEqualTo(SUCCESS_STATUS);
  }

}
