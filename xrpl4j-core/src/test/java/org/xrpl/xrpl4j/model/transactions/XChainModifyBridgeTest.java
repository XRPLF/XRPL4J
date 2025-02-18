package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.XChainModifyBridgeFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class XChainModifyBridgeTest extends AbstractJsonTest {

  @Test
  void testWithEmptySigRewardAndMinAccountCreateAmount() throws JSONException, JsonProcessingException {
    XChainModifyBridge modify = baseBuilder().build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainModifyBridge\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(modify, json);
  }

  @Test
  void testWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainModifyBridge modify = baseBuilder()
      .flags(XChainModifyBridgeFlags.UNSET)
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainModifyBridge\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(modify, json);
  }

  @Test
  void testWithClearAccountCreateAmountFlags() throws JSONException, JsonProcessingException {
    XChainModifyBridge modify = baseBuilder()
      .flags(XChainModifyBridgeFlags.CLEAR_ACCOUNT_CREATE_AMOUNT)
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainModifyBridge\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Flags\": %s,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", XChainModifyBridgeFlags.CLEAR_ACCOUNT_CREATE_AMOUNT, ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(modify, json);
  }

  @Test
  void testWithSigRewardAndMinAccountCreateAmount() throws JSONException, JsonProcessingException {
    XChainModifyBridge modify = baseBuilder()
      .signatureReward(XrpCurrencyAmount.ofDrops(200))
      .minAccountCreateAmount(XrpCurrencyAmount.ofDrops(1000000))
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"TransactionType\": \"XChainModifyBridge\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SignatureReward\": \"200\",\n" +
      "  \"MinAccountCreateAmount\": \"1000000\",\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(modify, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainModifyBridge modify = baseBuilder()
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("\n" +
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"XChainModifyBridge\",\n" +
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
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\": %s\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(modify, json);
  }

  private ImmutableXChainModifyBridge.Builder baseBuilder() {
    return XChainModifyBridge.builder()
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.ONE)
      .account(Address.of("rhWQzvdmhf5vFS35vtKUSUwNZHGT53qQsg"))
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