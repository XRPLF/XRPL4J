package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class XChainCommitTest extends AbstractJsonTest {

  @Test
  void testJsonWithIssuedCurrencyAmount() throws JSONException, JsonProcessingException {
    XChainCommit commit = baseBuilder()
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("CNY")
          .issuer(Address.of("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK"))
          .value("5000")
          .build()
      )
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCommit\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Amount\": {" +
      "      \"currency\": \"CNY\",\n" +
      "      \"value\": \"5000\",\n" +
      "      \"issuer\": \"r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK\"\n" +
      "  },\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainClaimID\": \"13f\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    XChainCommit commit = baseBuilder().build();

    String json = String.format("{\n" +
      "  \"Account\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCommit\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainClaimID\": \"13f\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainCommit commit = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCommit\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"Flags\": 0,\n" +
      "  \"XChainClaimID\": \"13f\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithFullyCanonicalSigFlags() throws JSONException, JsonProcessingException {
    XChainCommit commit = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCommit\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"Flags\": %s,\n" +
      "  \"XChainClaimID\": \"13f\"\n" +
      "}", ED_PUBLIC_KEY.base16Value(), TransactionFlags.FULLY_CANONICAL_SIG);

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainCommit commit = baseBuilder()
      .amount(
        IssuedCurrencyAmount.builder()
          .currency("CNY")
          .issuer(Address.of("r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK"))
          .value("5000")
          .build()
      )
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCommit\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"Amount\": {" +
      "      \"currency\": \"CNY\",\n" +
      "      \"value\": \"5000\",\n" +
      "      \"issuer\": \"r45dBj4S3VvMMYXxr9vHX4Z4Ma6ifPMCkK\"\n" +
      "  },\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"XChainClaimID\": \"13f\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  private ImmutableXChainCommit.Builder baseBuilder() {
    return XChainCommit.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo"))
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.valueOf(0x13f)))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .signingPublicKey(ED_PUBLIC_KEY);
  }
}