package org.xrpl.xrpl4j.model.transactions.json;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.EnableAmendment;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableEnableAmendment;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.util.Optional;

public class EnableAmendmentJsonTests
  extends AbstractTransactionJsonTest<ImmutableEnableAmendment, ImmutableEnableAmendment.Builder, EnableAmendment> {

  /**
   * No-args Constructor.
   */
  protected EnableAmendmentJsonTests() {
    super(EnableAmendment.class, ImmutableEnableAmendment.class, TransactionType.ENABLE_AMENDMENT);
  }


  @Override
  protected ImmutableEnableAmendment.Builder builder() {
    return ImmutableEnableAmendment.builder();
  }

  @Override
  protected EnableAmendment fullyPopulatedTransaction() {
    return EnableAmendment.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.valueOf(2470665))
      .amendment(Hash256.of("42426C4D4F1009EE67080A9B7965B44656D7714D104A72F9B4369F97ABF044EE"))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(67850752))))
      .build();
  }

  @Override
  protected EnableAmendment fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected EnableAmendment minimallyPopulatedTransaction() {
    return EnableAmendment.builder()
      .account(Address.of("rrrrrrrrrrrrrrrrrrrrrhoLvTp"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .amendment(Hash256.of("42426C4D4F1009EE67080A9B7965B44656D7714D104A72F9B4369F97ABF044EE"))
      .ledgerSequence(Optional.of(LedgerIndex.of(UnsignedInteger.valueOf(67850752))))
      .build();
  }

  @Test
  public void testJson() throws JsonProcessingException, JSONException {
    String json = "{" +
                  "\"Account\":\"rrrrrrrrrrrrrrrrrrrrrhoLvTp\"," +
                  "\"Fee\":\"12\"," +
                  "\"LedgerSequence\":67850752," +
                  "\"Sequence\":2470665," +
                  "\"SigningPubKey\":\"\"," +
                  "\"TransactionType\":\"EnableAmendment\"," +
                  "\"Amendment\":\"42426C4D4F1009EE67080A9B7965B44656D7714D104A72F9B4369F97ABF044EE\"}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json = "{" +
                  "\"Foo\" : \"Bar\",\n" +
                  "\"Account\":\"rrrrrrrrrrrrrrrrrrrrrhoLvTp\"," +
                  "\"Fee\":\"12\"," +
                  "\"LedgerSequence\":67850752," +
                  "\"Sequence\":2470665," +
                  "\"SigningPubKey\":\"\"," +
                  "\"TransactionType\":\"EnableAmendment\"," +
                  "\"Amendment\":\"42426C4D4F1009EE67080A9B7965B44656D7714D104A72F9B4369F97ABF044EE\"}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
