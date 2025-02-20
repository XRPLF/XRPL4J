package org.xrpl.xrpl4j.model.transactions.json;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.SignerEntry;
import org.xrpl.xrpl4j.model.ledger.SignerEntryWrapper;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutableSignerListSet;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.SignerListSet;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class SignerListSetJsonTests
  extends AbstractTransactionJsonTest<ImmutableSignerListSet, ImmutableSignerListSet.Builder, SignerListSet> {

  /**
   * No-args Constructor.
   */
  protected SignerListSetJsonTests() {
    super(SignerListSet.class, ImmutableSignerListSet.class, TransactionType.SIGNER_LIST_SET);
  }

  @Override
  protected ImmutableSignerListSet.Builder builder() {
    return ImmutableSignerListSet.builder();
  }

  @Override
  protected SignerListSet fullyPopulatedTransaction() {
    return SignerListSet.builder()
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
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected SignerListSet fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected SignerListSet minimallyPopulatedTransaction() {
    return SignerListSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .signerQuorum(UnsignedInteger.valueOf(3))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testSignerListSetJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"SignerListSet\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"SignerQuorum\": 3,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"SignerEntries\": [\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "                \"SignerWeight\": 2\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        }\n" +
      "    ]\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testSignerListSetForDeleteJson() throws JsonProcessingException, JSONException {
    SignerListSet signerListSet = SignerListSet.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .signerQuorum(UnsignedInteger.ZERO)
      .networkId(NetworkId.of(1024))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();

    String json =
      "{\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"SignerListSet\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"SignerQuorum\": 0\n" +
      "}";

    assertCanSerializeAndDeserialize(signerListSet, json);
  }

  @Test
  public void testSignerListSetJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    SignerListSet transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"SignerListSet\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"SignerEntries\": [\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "                \"SignerWeight\": 2\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        }\n" +
      "    ],\n" +
      "  \"SignerQuorum\": 3\n" +
      "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testSignerListSetJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {

    SignerListSet transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"SignerListSet\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"SignerEntries\": [\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "                \"SignerWeight\": 2\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        }\n" +
      "    ],\n" +
      "  \"SignerQuorum\": 3\n" +
      "}";

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testSignerListSetJsonWithNonZeroFlagsWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"TransactionType\": \"SignerListSet\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"SignerEntries\": [\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rsA2LpzuawewSBQXkiju3YQTMzW13pAAdW\",\n" +
      "                \"SignerWeight\": 2\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"rUpy3eEg8rqjqfUoLeBnZkscbKbFsKXC3v\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        },\n" +
      "        {\n" +
      "            \"SignerEntry\": {\n" +
      "                \"Account\": \"raKEEVSGnKSD9Zyvxu4z6Pqpm4ABH8FS6n\",\n" +
      "                \"SignerWeight\": 1\n" +
      "            }\n" +
      "        }\n" +
      "    ],\n" +
      "  \"SignerQuorum\": 3\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

}
