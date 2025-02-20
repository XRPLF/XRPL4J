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
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.PaymentFlags;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.ImmutablePayment;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.PathStep;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class PaymentJsonTests
  extends AbstractTransactionJsonTest<ImmutablePayment, ImmutablePayment.Builder, Payment> {

  /**
   * No-args Constructor.
   */
  protected PaymentJsonTests() {
    super(Payment.class, ImmutablePayment.class, TransactionType.PAYMENT);
  }


  @Override
  protected ImmutablePayment.Builder builder() {
    return ImmutablePayment.builder();
  }

  @Override
  protected Payment fullyPopulatedTransaction() {
    return Payment.builder()
      .account(Address.of("r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb"))
      .destination(Address.of("r4BPgS7DHebQiU31xWELvZawwSG2fSPJ7C"))
      .amount(XrpCurrencyAmount.ofDrops(25000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .flags(PaymentFlags.UNSET)
      .sequence(UnsignedInteger.valueOf(2))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected Payment fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected Payment minimallyPopulatedTransaction() {
    return Payment.builder()
      .account(Address.of("r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb"))
      .destination(Address.of("r4BPgS7DHebQiU31xWELvZawwSG2fSPJ7C"))
      .amount(XrpCurrencyAmount.ofDrops(25000000))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Account\": \"r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb\",\n" +
      "  \"Destination\": \"r4BPgS7DHebQiU31xWELvZawwSG2fSPJ7C\",\n" +
      "  \"TransactionType\": \"Payment\",\n" +
      "  \"Amount\": \"25000000\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Sequence\": 2\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testJsonWithFlags() throws JsonProcessingException, JSONException {
    PaymentFlags flags = PaymentFlags.builder()
      .tfPartialPayment(true)
      .tfLimitQuality(true)
      .tfNoDirectRipple(true)
      .build();

    Payment payment = builder().from(fullyPopulatedTransaction())
      .flags(flags)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb\",\n" +
      "  \"Amount\": \"25000000\",\n" +
      "  \"Destination\": \"r4BPgS7DHebQiU31xWELvZawwSG2fSPJ7C\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Flags\": 2147942400,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Sequence\": 2,\n" +
      "  \"TransactionType\": \"Payment\"\n" +
      "}";

    assertCanSerializeAndDeserialize(payment, json);
  }

  @Test
  public void testComplicatedJson() throws JsonProcessingException, JSONException {
    Payment payment = Payment.builder()
      .account(Address.of("rHXUjUtk5eiPFYpg27izxHeZ1t4x835Ecn"))
      .destination(Address.of("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK"))
      .amount(IssuedCurrencyAmount.builder()
        .currency("CNY")
        .value("5000")
        .issuer(Address.of("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK"))
        .build()
      )
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sendMax(IssuedCurrencyAmount.builder()
        .currency("CNY")
        .value("5050")
        .issuer(Address.of("rHXUjUtk5eiPFYpg27izxHeZ1t4x835Ecn"))
        .build()
      )
      .sequence(UnsignedInteger.valueOf(6))
      .addPaths(Lists.newArrayList(
        PathStep.builder()
          .account(Address.of("razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA"))
          .build()
      ))
      .flags(PaymentFlags.of(TransactionFlags.FULLY_CANONICAL_SIG.getValue()))
      .destinationTag(UnsignedInteger.valueOf(736049272))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rHXUjUtk5eiPFYpg27izxHeZ1t4x835Ecn\",\n" +
      "  \"Destination\": \"r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK\",\n" +
      "  \"TransactionType\": \"Payment\",\n" +
      "  \"Amount\": {\n" +
      "    \"currency\": \"CNY\",\n" +
      "    \"value\": \"5000\",\n" +
      "    \"issuer\": \"r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK\"\n" +
      "  },\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"SendMax\": {\n" +
      "    \"currency\": \"CNY\",\n" +
      "    \"value\": \"5050\",\n" +
      "    \"issuer\": \"rHXUjUtk5eiPFYpg27izxHeZ1t4x835Ecn\"\n" +
      "  },\n" +
      "  \"Flags\": 2147483648,\n" +
      "  \"Sequence\": 6,\n" +
      "  \"Paths\": [[{\n" +
      "    \"account\": \"razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA\"\n" +
      "  }]],\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"DestinationTag\": 736049272\n" +
      "}";

    assertCanSerializeAndDeserialize(payment, json);
  }

  @Test
  public void testJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "    \"Account\": \"r9TeThyi5xiuUUrFjtPKZiHcDxs7K9H6Rb\",\n" +
      "    \"Destination\": \"r4BPgS7DHebQiU31xWELvZawwSG2fSPJ7C\",\n" +
      "    \"TransactionType\": \"Payment\",\n" +
      "    \"Amount\": \"25000000\",\n" +
      "    \"Fee\": \"10\",\n" +
      "    \"Flags\": 0,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Sequence\": 2\n" +
      "}";

    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
