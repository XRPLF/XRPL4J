package org.xrpl.xrpl4j.tests.environment;

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
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.PrivateKey;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.TransactionResultCodes;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

/**
 * Environment that runs a local rippled inside docker in standalone mode.
 */
public class LocalRippledEnvironment implements XrplEnvironment {

  private static final Logger LOGGER = getLogger(LocalRippledEnvironment.class);

  private static final RippledContainer rippledContainer = new RippledContainer().start();

  private final SignatureService<PrivateKey> signatureService = new BcSignatureService();

  @Override
  public XrplClient getXrplClient() {
    return rippledContainer.getXrplClient();
  }

  @Override
  public void fundAccount(Address classicAddress) {
    // accounts are funded from the genesis account that holds all XRP when the ledger container starts.
    try {
      sendPayment(
        RippledContainer.getMasterKeyPair(),
        classicAddress,
        XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(1000))
      );
    } catch (JsonRpcClientErrorException | JsonProcessingException e) {
      throw new RuntimeException("could not fund account", e);
    }
  }

  protected AccountInfoResult getCurrentAccountInfo(Address classicAddress) {
    try {
      AccountInfoRequestParams params = AccountInfoRequestParams.of(classicAddress);
      return getXrplClient().accountInfo(params);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  protected void sendPayment(KeyPair sourceKeyPair, Address destinationAddress, XrpCurrencyAmount paymentAmount)
    throws JsonRpcClientErrorException, JsonProcessingException {
    FeeResult feeResult = getXrplClient().fee();
    AccountInfoResult accountInfo = this.getCurrentAccountInfo(sourceKeyPair.publicKey().deriveAddress());
    Payment payment = Payment.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .destination(destinationAddress)
      .amount(paymentAmount)
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<Payment> signedPayment = signatureService.sign(sourceKeyPair.privateKey(), payment);
    SubmitResult<Payment> result = getXrplClient().submit(signedPayment);
    assertThat(result.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
    LOGGER.info(
      "Payment successful ({}): {}", rippledContainer.getBaseUri().toString(), result.transactionResult().transaction()
    );
  }

  /**
   * Method to accept next ledger ad hoc, only available in RippledContainer.java implementation.
   */
  @Override
  public void acceptLedger() {
    rippledContainer.acceptLedger();
  }

  @Override
  public void startLedgerAcceptor(Duration acceptIntervalMillis) {
    Objects.requireNonNull(acceptIntervalMillis);
    rippledContainer.startLedgerAcceptor(acceptIntervalMillis);
  }

  @Override
  public void stopLedgerAcceptor() {
    rippledContainer.stopLedgerAcceptor();
  }
}
