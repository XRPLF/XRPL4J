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

class XChainClaimTest extends AbstractJsonTest {

  @Test
  void testJsonWithDestinationTag() throws JSONException, JsonProcessingException {
    XChainClaim attestation = baseBuilder()
      .destinationTag(UnsignedInteger.ONE)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"TransactionType\": \"XChainClaim\",\n" +
      "  \"XChainClaimID\": \"13f\",\n" +
      "  \"Destination\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"DestinationTag\": 1,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    XChainClaim attestation = baseBuilder().build();

    String json = String.format("{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"TransactionType\": \"XChainClaim\",\n" +
      "  \"XChainClaimID\": \"13f\",\n" +
      "  \"Destination\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainClaim attestation = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"Flags\": 0,\n" +
      "  \"TransactionType\": \"XChainClaim\",\n" +
      "  \"XChainClaimID\": \"13f\",\n" +
      "  \"Destination\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithFullyCanonicalSigFlags() throws JSONException, JsonProcessingException {
    XChainClaim attestation = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"Flags\": %s,\n" +
      "  \"TransactionType\": \"XChainClaim\",\n" +
      "  \"XChainClaimID\": \"13f\",\n" +
      "  \"Destination\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value(), TransactionFlags.FULLY_CANONICAL_SIG);

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainClaim attestation = baseBuilder()
      .destinationTag(UnsignedInteger.ONE)
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"Amount\": \"10000\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s,\n" +
      "  \"TransactionType\": \"XChainClaim\",\n" +
      "  \"XChainClaimID\": \"13f\",\n" +
      "  \"Destination\": \"rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw\",\n" +
      "  \"DestinationTag\": 1,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(attestation, json);
  }

  private ImmutableXChainClaim.Builder baseBuilder() {
    return XChainClaim.builder()
      .account(Address.of("rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .signingPublicKey(ED_PUBLIC_KEY)
      .amount(XrpCurrencyAmount.ofDrops(10000))
      .xChainClaimId(XChainClaimId.of(UnsignedLong.valueOf(0x13F)))
      .destination(Address.of("rahDmoXrtPdh7sUdrPjini3gcnTVYjbjjw"))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .build()
      );
  }
}