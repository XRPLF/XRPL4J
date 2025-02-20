package org.xrpl.xrpl4j.model.jackson.modules;

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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.serverinfo.ServerInfo;
import org.xrpl.xrpl4j.model.flags.Flags;
import org.xrpl.xrpl4j.model.transactions.CurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.metadata.AffectedNode;

/**
 * Jackson module for the xrpl4j-model project.
 */
public class Xrpl4jModule extends SimpleModule {

  private static final String NAME = "Xrpl4jModule";

  /**
   * No-arg constructor.
   */
  public Xrpl4jModule() {
    super(
      NAME,
      new Version(
        1,
        0,
        0,
        null,
        "org.xrpl.xrpl4j",
        "xrpl4j"
      )
    );

    addDeserializer(CurrencyAmount.class, new CurrencyAmountDeserializer());

    addSerializer(LedgerIndex.class, new LedgerIndexSerializer());
    addDeserializer(LedgerIndex.class, new LedgerIndexDeserializer());

    addDeserializer(Transaction.class, new TransactionDeserializer());

    addDeserializer(ServerInfo.class, new ServerInfoDeserializer());

    addSerializer(UnsignedByteArray.class, new UnsignedByteArraySerializer());
    addDeserializer(UnsignedByteArray.class, new UnsignedByteArrayDeserializer());

    addSerializer(Flags.class, new FlagsSerializer());

    addDeserializer(AffectedNode.class, new AffectedNodeDeserializer());
  }
}
