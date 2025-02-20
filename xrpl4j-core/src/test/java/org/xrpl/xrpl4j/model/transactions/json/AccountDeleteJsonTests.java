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
import org.xrpl.xrpl4j.model.transactions.AccountDelete;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutableAccountDelete;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class AccountDeleteJsonTests
  extends AbstractTransactionJsonTest<ImmutableAccountDelete, ImmutableAccountDelete.Builder, AccountDelete> {

  /**
   * No-args Constructor.
   */
  protected AccountDeleteJsonTests() {
    super(AccountDelete.class, ImmutableAccountDelete.class, TransactionType.ACCOUNT_DELETE);
  }

  @Override
  protected ImmutableAccountDelete.Builder builder() {
    return AccountDelete.builder();
  }

  @Override
  protected AccountDelete fullyPopulatedTransaction() {
    return AccountDelete.builder()
      .account(Address.of("rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm"))
      .fee(XrpCurrencyAmount.ofDrops(5000000))
      .sequence(UnsignedInteger.valueOf(2470665))
      .destination(Address.of("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe"))
      .destinationTag(UnsignedInteger.valueOf(13))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .flags(TransactionFlags.UNSET)
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected AccountDelete fullyPopulatedTransactionWithUnknownFields() {
    return AccountDelete.builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected AccountDelete minimallyPopulatedTransaction() {
    return AccountDelete.builder()
      .account(Address.of("rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm"))
      .fee(XrpCurrencyAmount.ofDrops(5000000))
      .destination(Address.of("rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe"))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testFullyPopulatedJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"AccountDelete\",\n" +
        "  \"Account\": \"rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm\",\n" +
        "  \"Destination\": \"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe\",\n" +
        "  \"DestinationTag\": 13,\n" +
        "  \"Fee\": \"5000000\",\n" +
        "  \"Flags\": 0,\n" +
        "  \"Sequence\": 2470665,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    this.assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

  @Test
  public void testFullyPopulatedWithUnknownFieldsJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"AccountDelete\",\n" +
        "  \"Account\": \"rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm\",\n" +
        "  \"Destination\": \"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe\",\n" +
        "  \"DestinationTag\": 13,\n" +
        "  \"Fee\": \"5000000\",\n" +
        "  \"Flags\": 0,\n" +
        "  \"Sequence\": 2470665,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    this.assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }

  @Test
  public void testMinimallyPopulatedJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"AccountDelete\",\n" +
        "  \"Account\": \"rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm\",\n" +
        "  \"Destination\": \"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe\",\n" +
        "  \"Fee\": \"5000000\",\n" +
        "  \"Sequence\": 0,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    this.assertCanSerializeAndDeserialize(minimallyPopulatedTransaction(), json);
  }

  @Test
  public void testJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    AccountDelete fullyPopulatedAccountDelete = AccountDelete.builder()
      .from(fullyPopulatedTransactionWithUnknownFields())
      .flags(TransactionFlags.UNSET)
      .build();

    String json =
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"AccountDelete\",\n" +
        "  \"Account\": \"rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm\",\n" +
        "  \"Destination\": \"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe\",\n" +
        "  \"DestinationTag\": 13,\n" +
        "  \"Fee\": \"5000000\",\n" +
        "  \"Flags\": 0,\n" +
        "  \"Sequence\": 2470665,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedAccountDelete, json);
  }

  @Test
  public void testJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {

    AccountDelete fullyPopulatedTransaction = AccountDelete.builder()
      .from(fullyPopulatedTransactionWithUnknownFields())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format(
      "{\n" +
        "  \"Foo\" : \"Bar\",\n" +
        "  \"TransactionType\": \"AccountDelete\",\n" +
        "  \"Account\": \"rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm\",\n" +
        "  \"Destination\": \"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe\",\n" +
        "  \"DestinationTag\": 13,\n" +
        "  \"Fee\": \"5000000\",\n" +
        "  \"Flags\": %s,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"Sequence\": 2470665,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}", TransactionFlags.FULLY_CANONICAL_SIG.getValue());

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
        "  \"TransactionType\": \"AccountDelete\",\n" +
        "  \"Account\": \"rWYkbWkCeg8dP6rXALnjgZSjjLyih5NXm\",\n" +
        "  \"Destination\": \"rPT1Sjq2YGrBMTttX4GZHjKu9dyfzbpAYe\",\n" +
        "  \"DestinationTag\": 13,\n" +
        "  \"Flags\": 0,\n" +
        "  \"Fee\": \"5000000\",\n" +
        "  \"Sequence\": 2470665,\n" +
        "  \"NetworkID\": 1024,\n" +
        "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\"\n" +
        "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }
}
