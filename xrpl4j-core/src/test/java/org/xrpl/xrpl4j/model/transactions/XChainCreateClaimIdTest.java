package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class XChainCreateClaimIdTest extends AbstractJsonTest {

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    XChainCreateClaimId claimId = baseBuilder().build();

    String json = String.format("\n" +
      "{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"OtherChainSource\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCreateClaimID\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(claimId, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainCreateClaimId claimId = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"OtherChainSource\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCreateClaimID\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(claimId, json);
  }

  @Test
  void testJsonWithFullyCanonicalSigFlags() throws JSONException, JsonProcessingException {
    XChainCreateClaimId claimId = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"OtherChainSource\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCreateClaimID\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": %s,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG, ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(claimId, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainCreateClaimId claimId = baseBuilder()
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"OtherChainSource\": \"rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo\",\n" +
      "  \"TransactionType\": \"XChainCreateClaimID\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(claimId, json);
  }

  private ImmutableXChainCreateClaimId.Builder baseBuilder() {
    return XChainCreateClaimId.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw"))
      .otherChainSource(Address.of("rMTi57fNy2UkUb4RcdoUeJm7gjxVQvxzUo"))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
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