package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.signing.Signature;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class XChainAddAccountCreateAttestationTest extends AbstractJsonTest {

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    XChainAddAccountCreateAttestation transaction = baseBuilder().build();
    String json = String.format("{\n" +
      "  \"Account\": \"rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv\",\n" +
      "  \"TransactionType\": \"XChainAddAccountCreateAttestation\",\n" +
      "  \"OtherChainSource\": \"rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U\",\n" +
      "  \"Destination\": \"rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd\",\n" +
      "  \"Amount\": \"2000000000\",\n" +
      "  \"PublicKey\": \"EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614\",\n" +
      "  \"Signature\": \"F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E9AFF11A4AA46F09EC" +
      "FFB04C6A8DAE8284AF3ED8128C7D0046D842448478500\",\n" +
      "  \"WasLockingChainSend\": 1,\n" +
      "  \"AttestationRewardAccount\": \"rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es\",\n" +
      "  \"AttestationSignerAccount\": \"rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw\",\n" +
      "  \"XChainAccountCreateCount\": \"2\",\n" +
      "  \"SignatureReward\": \"204\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Fee\": \"20\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainAddAccountCreateAttestation transaction = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();
    String json = String.format("{\n" +
      "  \"Account\": \"rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv\",\n" +
      "  \"TransactionType\": \"XChainAddAccountCreateAttestation\",\n" +
      "  \"OtherChainSource\": \"rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U\",\n" +
      "  \"Destination\": \"rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd\",\n" +
      "  \"Amount\": \"2000000000\",\n" +
      "  \"PublicKey\": \"EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614\",\n" +
      "  \"Signature\": \"F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E9AFF11A4AA46" +
      "F09ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500\",\n" +
      "  \"WasLockingChainSend\": 1,\n" +
      "  \"AttestationRewardAccount\": \"rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es\",\n" +
      "  \"AttestationSignerAccount\": \"rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw\",\n" +
      "  \"XChainAccountCreateCount\": \"2\",\n" +
      "  \"SignatureReward\": \"204\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"Flags\": 0,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Fee\": \"20\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithFullyCanonicalSig() throws JSONException, JsonProcessingException {
    XChainAddAccountCreateAttestation transaction = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();
    String json = String.format("{\n" +
      "  \"Account\": \"rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv\",\n" +
      "  \"TransactionType\": \"XChainAddAccountCreateAttestation\",\n" +
      "  \"OtherChainSource\": \"rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U\",\n" +
      "  \"Destination\": \"rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd\",\n" +
      "  \"Amount\": \"2000000000\",\n" +
      "  \"PublicKey\": \"EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614\",\n" +
      "  \"Signature\": \"F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E9AFF11A4AA46F09ECFF" +
      "B04C6A8DAE8284AF3ED8128C7D0046D842448478500\",\n" +
      "  \"WasLockingChainSend\": 1,\n" +
      "  \"AttestationRewardAccount\": \"rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es\",\n" +
      "  \"AttestationSignerAccount\": \"rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw\",\n" +
      "  \"XChainAccountCreateCount\": \"2\",\n" +
      "  \"SignatureReward\": \"204\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"Flags\": %s,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Fee\": \"20\"\n" +
      "}", ED_PUBLIC_KEY.base16Value(), TransactionFlags.FULLY_CANONICAL_SIG);

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithWasLockingChainSendTrue() throws JSONException, JsonProcessingException {
    XChainAddAccountCreateAttestation transaction = baseBuilder()
      .wasLockingChainSend(true)
      .build();
    String json = String.format("{\n" +
      "  \"Account\": \"rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv\",\n" +
      "  \"TransactionType\": \"XChainAddAccountCreateAttestation\",\n" +
      "  \"OtherChainSource\": \"rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U\",\n" +
      "  \"Destination\": \"rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd\",\n" +
      "  \"Amount\": \"2000000000\",\n" +
      "  \"PublicKey\": \"EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614\",\n" +
      "  \"Signature\": \"F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E9AFF11A4AA46F09E" +
      "CFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500\",\n" +
      "  \"WasLockingChainSend\": 1,\n" +
      "  \"AttestationRewardAccount\": \"rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es\",\n" +
      "  \"AttestationSignerAccount\": \"rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw\",\n" +
      "  \"XChainAccountCreateCount\": \"2\",\n" +
      "  \"SignatureReward\": \"204\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Fee\": \"20\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithWasLockingChainSendFalse() throws JSONException, JsonProcessingException {
    XChainAddAccountCreateAttestation transaction = baseBuilder()
      .wasLockingChainSend(false)
      .build();
    String json = String.format("{\n" +
      "  \"Account\": \"rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv\",\n" +
      "  \"TransactionType\": \"XChainAddAccountCreateAttestation\",\n" +
      "  \"OtherChainSource\": \"rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U\",\n" +
      "  \"Destination\": \"rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd\",\n" +
      "  \"Amount\": \"2000000000\",\n" +
      "  \"PublicKey\": \"EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614\",\n" +
      "  \"Signature\": \"F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E9AFF11A4AA46F0" +
      "9ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500\",\n" +
      "  \"WasLockingChainSend\": 0,\n" +
      "  \"AttestationRewardAccount\": \"rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es\",\n" +
      "  \"AttestationSignerAccount\": \"rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw\",\n" +
      "  \"XChainAccountCreateCount\": \"2\",\n" +
      "  \"SignatureReward\": \"204\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Fee\": \"20\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  private ImmutableXChainAddAccountCreateAttestation.Builder baseBuilder() {
    return XChainAddAccountCreateAttestation.builder()
      .account(Address.of("rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv"))
      .otherChainSource(Address.of("rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U"))
      .destination(Address.of("rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd"))
      .amount(XrpCurrencyAmount.ofDrops(2000000000))
      .publicKey(
        PublicKey.fromBase16EncodedPublicKey("EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614")
      )
      .signature(
        Signature.fromBase16("F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E" +
          "9AFF11A4AA46F09ECFFB04C6A8DAE8284AF3ED8128C7D0046D842448478500")
      )
      .wasLockingChainSend(true)
      .attestationRewardAccount(Address.of("rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es"))
      .attestationSignerAccount(Address.of("rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw"))
      .xChainAccountCreateCount(XChainCount.of(UnsignedLong.valueOf(2)))
      .signatureReward(XrpCurrencyAmount.ofDrops(204))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .fee(XrpCurrencyAmount.ofDrops(20))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(ED_PUBLIC_KEY);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainAddAccountCreateAttestation transaction = baseBuilder()
      .putUnknownFields("Foo", "Bar")
      .build();
    String json = String.format("{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rDr5okqGKmMpn44Bbhe5WAfDQx8e9XquEv\",\n" +
      "  \"TransactionType\": \"XChainAddAccountCreateAttestation\",\n" +
      "  \"OtherChainSource\": \"rUzB7yg1LcFa7m3q1hfrjr5w53vcWzNh3U\",\n" +
      "  \"Destination\": \"rJMfWNVbyjcCtds8kpoEjEbYQ41J5B6MUd\",\n" +
      "  \"Amount\": \"2000000000\",\n" +
      "  \"PublicKey\": \"EDF7C3F9C80C102AF6D241752B37356E91ED454F26A35C567CF6F8477960F66614\",\n" +
      "  \"Signature\": \"F95675BA8FDA21030DE1B687937A79E8491CE51832D6BEEBC071484FA5AF5B8A0E9AFF11A4AA46F09EC" +
      "FFB04C6A8DAE8284AF3ED8128C7D0046D842448478500\",\n" +
      "  \"WasLockingChainSend\": 1,\n" +
      "  \"AttestationRewardAccount\": \"rpFp36UHW6FpEcZjZqq5jSJWY6UCj3k4Es\",\n" +
      "  \"AttestationSignerAccount\": \"rpWLegmW9WrFBzHUj7brhQNZzrxgLj9oxw\",\n" +
      "  \"XChainAccountCreateCount\": \"2\",\n" +
      "  \"SignatureReward\": \"204\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"r3nCVTbZGGYoWvZ58BcxDmiMUU7ChMa1eC\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Fee\": \"20\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }
}