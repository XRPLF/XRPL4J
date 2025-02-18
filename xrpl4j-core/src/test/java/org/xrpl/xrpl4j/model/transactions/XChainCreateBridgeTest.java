package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class XChainCreateBridgeTest extends AbstractJsonTest {

  @Test
  void testJsonWithMinAccountCreateAmount() throws JSONException, JsonProcessingException {
    XChainCreateBridge createBridge = baseBuilder()
      .minAccountCreateAmount(XrpCurrencyAmount.ofDrops(1000000))
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainCreateBridge\",\n" +
      "  \"Account\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"SignatureReward\": \"200\",\n" +
      "  \"MinAccountCreateAmount\": \"1000000\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(createBridge, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainCreateBridge createBridge = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainCreateBridge\",\n" +
      "  \"Account\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"SignatureReward\": \"200\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(createBridge, json);
  }


  @Test
  void testJsonWithFullyCanonicalSigFlags() throws JSONException, JsonProcessingException {
    XChainCreateBridge createBridge = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainCreateBridge\",\n" +
      "  \"Account\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"SignatureReward\": \"200\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": %s,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG, ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(createBridge, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainCreateBridge createBridge = baseBuilder()
      .minAccountCreateAmount(XrpCurrencyAmount.ofDrops(1000000))
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"XChainCreateBridge\",\n" +
      "  \"Account\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"SignatureReward\": \"200\",\n" +
      "  \"MinAccountCreateAmount\": \"1000000\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(createBridge, json);
  }

  private ImmutableXChainCreateBridge.Builder baseBuilder() {
    return XChainCreateBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg"))
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .build()
      )
      .signingPublicKey(ED_PUBLIC_KEY);
  }

}