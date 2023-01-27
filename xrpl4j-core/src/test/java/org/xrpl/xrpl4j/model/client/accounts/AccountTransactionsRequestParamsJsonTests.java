package org.xrpl.xrpl4j.model.client.accounts;

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

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.common.LedgerIndexBound;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.Marker;

import java.util.Optional;

public class AccountTransactionsRequestParamsJsonTests extends AbstractJsonTest {

  @Test
  public void testWithStringMarker() throws JsonProcessingException, JSONException {
    AccountTransactionsRequestParams params = AccountTransactionsRequestParams.unboundedBuilder()
      .account(Address.of("rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w"))
      .marker(Marker.of("marker1"))
      .limit(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index_max\": -1,\n" +
      "            \"ledger_index_min\": -1,\n" +
      "            \"marker\": \"marker1\",\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertCanSerializeAndDeserialize(params, json);
  }

  @Test
  public void testWithJsonMarker() throws JsonProcessingException, JSONException {
    AccountTransactionsRequestParams params = AccountTransactionsRequestParams.unboundedBuilder()
      .account(Address.of("rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w"))
      .marker(Marker.of("{\"marker\":\"1\"}"))
      .limit(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index_max\": -1,\n" +
      "            \"ledger_index_min\": -1,\n" +
      "            \"marker\": {\"marker\":\"1\"},\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertCanSerializeAndDeserialize(params, json);
  }

  @Test
  public void testWithJsonWithLedgerIndexBounds() throws JsonProcessingException, JSONException {
    AccountTransactionsRequestParams params = AccountTransactionsRequestParams.unboundedBuilder()
      .account(Address.of("rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w"))
      .ledgerIndexMinimum(LedgerIndexBound.of(-1))
      .ledgerIndexMaximum(LedgerIndexBound.of(-1))
      .limit(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index_max\": -1,\n" +
      "            \"ledger_index_min\": -1,\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertCanSerializeAndDeserialize(params, json);
  }

  @Test
  public void testJsonWithLedgerIndex() throws JsonProcessingException, JSONException {
    AccountTransactionsRequestParams params = AccountTransactionsRequestParams.unboundedBuilder()
      .account(Address.of("rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w"))
      .ledgerSpecifier(Optional.of(LedgerSpecifier.of(LedgerIndex.of(UnsignedInteger.ONE))))
      .limit(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index\": 1,\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertCanSerializeAndDeserialize(params, json);
  }

  @Test
  public void testJsonWithLedgerHash() throws JsonProcessingException, JSONException {
    AccountTransactionsRequestParams params = AccountTransactionsRequestParams.unboundedBuilder()
      .account(Address.of("rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w"))
      .ledgerSpecifier(Optional.of(
        LedgerSpecifier.of(Hash256.of("5DB01B7FFED6B67E6B0414DED11E051D2EE2B7619CE0EAA6286D67A3A4D5BDB3"))
      ))
      .limit(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_hash\": \"5DB01B7FFED6B67E6B0414DED11E051D2EE2B7619CE0EAA6286D67A3A4D5BDB3\",\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertCanSerializeAndDeserialize(params, json);
  }

  @Test
  public void testJsonWithValidatedLedgerIndexShortcut() throws JsonProcessingException, JSONException {
    AccountTransactionsRequestParams params = AccountTransactionsRequestParams.unboundedBuilder()
      .account(Address.of("rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w"))
      .ledgerSpecifier(Optional.of(LedgerSpecifier.VALIDATED))
      .limit(UnsignedInteger.valueOf(2))
      .build();

    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index\": \"validated\",\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertCanSerializeAndDeserialize(params, json);
  }

  @Test
  public void testJsonWithCurrentLedgerIndexShortcut() throws JsonProcessingException, JSONException {
    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index\": \"current\",\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertThrows(
      IllegalArgumentException.class,
      () -> objectMapper.readValue(json, AccountTransactionsRequestParams.class)
    );
  }

  @Test
  public void testJsonWithClosedLedgerIndexShortcut() throws JsonProcessingException, JSONException {
    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index\": \"closed\",\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertThrows(
      IllegalArgumentException.class,
      () -> objectMapper.readValue(json, AccountTransactionsRequestParams.class)
    );
  }

  @Test
  void testJsonWithInvalidShortcut() {
    String json = "{\n" +
      "            \"account\": \"rLNaPoKeeBjZe2qs6x52yVPZpZ8td4dc6w\",\n" +
      "            \"binary\": false,\n" +
      "            \"forward\": false,\n" +
      "            \"ledger_index\": \"never\",\n" +
      "            \"limit\": 2\n" +
      "        }";

    assertThrows(
      JsonParseException.class,
      () -> objectMapper.readValue(json, AccountTransactionsRequestParams.class),
      "Unrecognized LedgerIndex shortcut 'never'."
    );
  }
}
