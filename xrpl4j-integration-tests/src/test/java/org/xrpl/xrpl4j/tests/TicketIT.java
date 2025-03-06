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
import com.google.common.primitives.UnsignedInteger;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.ledger.SignerListObject;
import org.xrpl.xrpl4j.model.ledger.TicketObject;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.TicketCreate;
import org.xrpl.xrpl4j.model.transactions.metadata.AffectedNode;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaLedgerEntryType;

import java.util.List;
import java.util.Optional;

public class TicketIT extends AbstractIT {

  @Test
  void createTicketAndUseSequenceNumber() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair sourceKeyPair = createRandomAccountEd25519();

    FeeResult feeResult = xrplClient.fee();
    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    assertThat(accountInfo.accountData().ticketCount()).isEmpty();
    
    TicketCreate ticketCreate = TicketCreate.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .sequence(accountInfo.accountData().sequence())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .ticketCount(UnsignedInteger.ONE)
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<TicketCreate> signedCreate = signatureService.sign(
      sourceKeyPair.privateKey(),
      ticketCreate
    );
    SubmitResult<TicketCreate> submitResult = xrplClient.submit(signedCreate);
    assertThat(submitResult.engineResult()).isEqualTo(SUCCESS_STATUS);
    logSubmitResult(submitResult);

    Hash256 ticketId = this.scanForResult(
      () -> this.getValidatedTransaction(
        submitResult.transactionResult().hash(),
        TicketCreate.class)
      ).metadata().get()
      .affectedNodes()
      .stream()
      .filter(affectedNode -> affectedNode.ledgerEntryType().equals(MetaLedgerEntryType.TICKET))
      .findFirst()
      .map(AffectedNode::ledgerIndex)
      .get();

    Optional<UnsignedInteger> ticketCount = this.scanForResult(
      () -> getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress()),
      result -> result.accountData().ticketCount().isPresent()
    ).accountData().ticketCount();
    assertThat(ticketCount).isNotEmpty().get()
      .isEqualTo(ticketCreate.ticketCount());

    List<TicketObject> tickets = getValidatedAccountObjects(
      sourceKeyPair.publicKey().deriveAddress(),
      TicketObject.class
    );
    assertThat(tickets).asList().hasSize(1);

    LedgerEntryResult<TicketObject> entryByIndex = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(ticketId, TicketObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(tickets.get(0));

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.index(ticketId, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());

    AccountSet accountSet = AccountSet.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .ticketSequence(tickets.get(0).ticketSequence())
      .signingPublicKey(sourceKeyPair.publicKey())
      .build();

    SingleSignedTransaction<AccountSet> signedAccountSet = signatureService.sign(
      sourceKeyPair.privateKey(),
      accountSet
    );
    SubmitResult<AccountSet> accountSetResult = xrplClient.submit(signedAccountSet);
    assertThat(accountSetResult.engineResult()).isEqualTo(SUCCESS_STATUS);
    logSubmitResult(accountSetResult);
  }
}
