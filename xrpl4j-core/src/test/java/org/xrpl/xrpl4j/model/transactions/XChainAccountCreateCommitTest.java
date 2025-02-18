package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.Issue;

class XChainAccountCreateCommitTest extends AbstractJsonTest {

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(Address.of("rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .signingPublicKey(ED_PUBLIC_KEY)
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(
            Issue.builder()
              .currency("TST")
              .issuer(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
              .build()
          )
          .build()
      )
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa\",\n" +
      "  \"Fee\": \"1\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Destination\": \"rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo\",\n" +
      "  \"TransactionType\": \"XChainAccountCreateCommit\",\n" +
      "  \"Amount\": \"20000000\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
      "  \"SigningPubKey\": \"%s\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"TST\",\n" +
      "      \"issuer\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(Address.of("rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .signingPublicKey(ED_PUBLIC_KEY)
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(
            Issue.builder()
              .currency("TST")
              .issuer(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
              .build()
          )
          .build()
      )
      .flags(TransactionFlags.UNSET)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa\",\n" +
      "  \"Fee\": \"1\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Destination\": \"rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo\",\n" +
      "  \"TransactionType\": \"XChainAccountCreateCommit\",\n" +
      "  \"Amount\": \"20000000\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
      "  \"SigningPubKey\": \"%s\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"TST\",\n" +
      "      \"issuer\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithFullyCanonicalSigFlags() throws JSONException, JsonProcessingException {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(Address.of("rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .signingPublicKey(ED_PUBLIC_KEY)
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(
            Issue.builder()
              .currency("TST")
              .issuer(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
              .build()
          )
          .build()
      )
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa\",\n" +
      "  \"Fee\": \"1\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Destination\": \"rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo\",\n" +
      "  \"TransactionType\": \"XChainAccountCreateCommit\",\n" +
      "  \"Amount\": \"20000000\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
      "  \"SigningPubKey\": \"%s\",\n" +
      "  \"Flags\": %s,\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"TST\",\n" +
      "      \"issuer\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value(), TransactionFlags.FULLY_CANONICAL_SIG);

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithEmptySignatureReward() throws JSONException, JsonProcessingException {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(Address.of("rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signingPublicKey(ED_PUBLIC_KEY)
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(
            Issue.builder()
              .currency("TST")
              .issuer(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
              .build()
          )
          .build()
      )
      .build();

    String json = String.format("{\n" +
      "  \"Account\": \"rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa\",\n" +
      "  \"Fee\": \"1\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Destination\": \"rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo\",\n" +
      "  \"TransactionType\": \"XChainAccountCreateCommit\",\n" +
      "  \"Amount\": \"20000000\",\n" +
      "  \"SigningPubKey\": \"%s\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"TST\",\n" +
      "      \"issuer\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainAccountCreateCommit commit = XChainAccountCreateCommit.builder()
      .account(Address.of("rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa"))
      .fee(XrpCurrencyAmount.ofDrops(1))
      .sequence(UnsignedInteger.ONE)
      .destination(Address.of("rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo"))
      .amount(XrpCurrencyAmount.ofDrops(20000000))
      .signatureReward(XrpCurrencyAmount.ofDrops(100))
      .signingPublicKey(ED_PUBLIC_KEY)
      .xChainBridge(
        XChainBridge.builder()
          .lockingChainDoor(Address.of("rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4"))
          .lockingChainIssue(Issue.XRP)
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(
            Issue.builder()
              .currency("TST")
              .issuer(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
              .build()
          )
          .build()
      )
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rwEqJ2UaQHe7jihxGqmx6J4xdbGiiyMaGa\",\n" +
      "  \"Fee\": \"1\",\n" +
      "  \"Sequence\": 1,\n" +
      "  \"Destination\": \"rD323VyRjgzzhY4bFpo44rmyh2neB5d8Mo\",\n" +
      "  \"TransactionType\": \"XChainAccountCreateCommit\",\n" +
      "  \"Amount\": \"20000000\",\n" +
      "  \"SignatureReward\": \"100\",\n" +
      "  \"SigningPubKey\": \"%s\",\n" +
      "  \"XChainBridge\": {\n" +
      "    \"LockingChainDoor\": \"rMAXACCrp3Y8PpswXcg3bKggHX76V3F8M4\",\n" +
      "    \"LockingChainIssue\": {\n" +
      "      \"currency\": \"XRP\"\n" +
      "    },\n" +
      "    \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "    \"IssuingChainIssue\": {\n" +
      "      \"currency\": \"TST\",\n" +
      "      \"issuer\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\"\n" +
      "    }\n" +
      "  }\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(commit, json);
  }
}