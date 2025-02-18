package org.xrpl.xrpl4j.model.transactions;

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

import java.util.Optional;

class XChainAddClaimAttestationTest extends AbstractJsonTest {

  @Test
  void testJsonWithFalseWasLockingChainSend() throws JSONException, JsonProcessingException {
    XChainAddClaimAttestation attestation = baseBuilder()
      .wasLockingChainSend(false)
      .build();

    String json = "{\n" +
      "        \"Account\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Amount\": \"10000000\",\n" +
      "        \"AttestationRewardAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"AttestationSignerAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Destination\": \"rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi\",\n" +
      "        \"Fee\": \"20\",\n" +
      "        \"LastLedgerSequence\": 19,\n" +
      "        \"OtherChainSource\": \"raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym\",\n" +
      "        \"PublicKey\": \"ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136\",\n" +
      "        \"Sequence\": 9,\n" +
      "        \"Signature\": \"7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44528FE99AA5" +
      "0E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C\",\n" +
      "        \"SigningPubKey\": \"ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C\",\n" +
      "        \"TransactionType\": \"XChainAddClaimAttestation\",\n" +
      "        \"WasLockingChainSend\": 0,\n" +
      "        \"XChainBridge\": {\n" +
      "          \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "          \"IssuingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          },\n" +
      "          \"LockingChainDoor\": \"rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg\",\n" +
      "          \"LockingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          }\n" +
      "        },\n" +
      "        \"XChainClaimID\": \"1\"\n" +
      "      }";

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithEmptyDestination() throws JSONException, JsonProcessingException {
    XChainAddClaimAttestation attestation = baseBuilder()
      .destination(Optional.empty())
      .build();

    String json = "{\n" +
      "        \"Account\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Amount\": \"10000000\",\n" +
      "        \"AttestationRewardAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"AttestationSignerAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Fee\": \"20\",\n" +
      "        \"LastLedgerSequence\": 19,\n" +
      "        \"OtherChainSource\": \"raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym\",\n" +
      "        \"PublicKey\": \"ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136\",\n" +
      "        \"Sequence\": 9,\n" +
      "        \"Signature\": \"7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44528FE99AA50E716" +
      "60A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C\",\n" +
      "        \"SigningPubKey\": \"ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C\",\n" +
      "        \"TransactionType\": \"XChainAddClaimAttestation\",\n" +
      "        \"WasLockingChainSend\": 1,\n" +
      "        \"XChainBridge\": {\n" +
      "          \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "          \"IssuingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          },\n" +
      "          \"LockingChainDoor\": \"rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg\",\n" +
      "          \"LockingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          }\n" +
      "        },\n" +
      "        \"XChainClaimID\": \"1\"\n" +
      "      }";

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithEmptyFlags() throws JSONException, JsonProcessingException {
    XChainAddClaimAttestation attestation = baseBuilder().build();

    String json = "{\n" +
      "        \"Account\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Amount\": \"10000000\",\n" +
      "        \"AttestationRewardAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"AttestationSignerAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Destination\": \"rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi\",\n" +
      "        \"Fee\": \"20\",\n" +
      "        \"LastLedgerSequence\": 19,\n" +
      "        \"OtherChainSource\": \"raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym\",\n" +
      "        \"PublicKey\": \"ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136\",\n" +
      "        \"Sequence\": 9,\n" +
      "        \"Signature\": \"7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44528FE99AA50E7166" +
      "0A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C\",\n" +
      "        \"SigningPubKey\": \"ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C\",\n" +
      "        \"TransactionType\": \"XChainAddClaimAttestation\",\n" +
      "        \"WasLockingChainSend\": 1,\n" +
      "        \"XChainBridge\": {\n" +
      "          \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "          \"IssuingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          },\n" +
      "          \"LockingChainDoor\": \"rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg\",\n" +
      "          \"LockingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          }\n" +
      "        },\n" +
      "        \"XChainClaimID\": \"1\"\n" +
      "      }";

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    XChainAddClaimAttestation attestation = baseBuilder()
      .flags(TransactionFlags.UNSET)
      .build();

    String json = "{\n" +
      "        \"Account\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Amount\": \"10000000\",\n" +
      "        \"AttestationRewardAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"AttestationSignerAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Destination\": \"rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi\",\n" +
      "        \"Fee\": \"20\",\n" +
      "        \"Flags\": 0,\n" +
      "        \"LastLedgerSequence\": 19,\n" +
      "        \"OtherChainSource\": \"raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym\",\n" +
      "        \"PublicKey\": \"ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136\",\n" +
      "        \"Sequence\": 9,\n" +
      "        \"Signature\": \"7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44528FE99AA5" +
      "0E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C\",\n" +
      "        \"SigningPubKey\": \"ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C\",\n" +
      "        \"TransactionType\": \"XChainAddClaimAttestation\",\n" +
      "        \"WasLockingChainSend\": 1,\n" +
      "        \"XChainBridge\": {\n" +
      "          \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "          \"IssuingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          },\n" +
      "          \"LockingChainDoor\": \"rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg\",\n" +
      "          \"LockingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          }\n" +
      "        },\n" +
      "        \"XChainClaimID\": \"1\"\n" +
      "      }";

    assertCanSerializeAndDeserialize(attestation, json);
  }

  @Test
  void testJsonWithFullyCanonicalSigFlags() throws JSONException, JsonProcessingException {
    XChainAddClaimAttestation attestation = baseBuilder()
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json = String.format("{\n" +
      "        \"Account\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Amount\": \"10000000\",\n" +
      "        \"AttestationRewardAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"AttestationSignerAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Destination\": \"rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi\",\n" +
      "        \"Fee\": \"20\",\n" +
      "        \"Flags\": %s,\n" +
      "        \"LastLedgerSequence\": 19,\n" +
      "        \"OtherChainSource\": \"raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym\",\n" +
      "        \"PublicKey\": \"ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136\",\n" +
      "        \"Sequence\": 9,\n" +
      "        \"Signature\": \"7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44528FE99A" +
      "A50E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C\",\n" +
      "        \"SigningPubKey\": \"ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C\",\n" +
      "        \"TransactionType\": \"XChainAddClaimAttestation\",\n" +
      "        \"WasLockingChainSend\": 1,\n" +
      "        \"XChainBridge\": {\n" +
      "          \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "          \"IssuingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          },\n" +
      "          \"LockingChainDoor\": \"rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg\",\n" +
      "          \"LockingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          }\n" +
      "        },\n" +
      "        \"XChainClaimID\": \"1\"\n" +
      "      }", TransactionFlags.FULLY_CANONICAL_SIG);

    assertCanSerializeAndDeserialize(attestation, json);
  }

  private ImmutableXChainAddClaimAttestation.Builder baseBuilder() {
    return XChainAddClaimAttestation.builder()
      .account(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .amount(XrpCurrencyAmount.ofDrops(10000000))
      .attestationRewardAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .attestationSignerAccount(Address.of("rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3"))
      .destination(Address.of("rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi"))
      .fee(XrpCurrencyAmount.ofDrops(20))
      .lastLedgerSequence(UnsignedInteger.valueOf(19))
      .otherChainSource(Address.of("raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym"))
      .sequence(UnsignedInteger.valueOf(9))
      .publicKey(
        PublicKey.fromBase16EncodedPublicKey("ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136")
      )
      .signature(
        Signature.fromBase16("7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44" +
          "528FE99AA50E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C")
      )
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C")
      )
      .wasLockingChainSend(true)
      .xChainBridge(
        XChainBridge.builder()
          .issuingChainDoor(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
          .issuingChainIssue(Issue.XRP)
          .lockingChainDoor(Address.of("rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg"))
          .lockingChainIssue(Issue.XRP)
          .build()
      )
      .xChainClaimId(XChainClaimId.of(UnsignedLong.ONE));
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    XChainAddClaimAttestation attestation = baseBuilder()
      .wasLockingChainSend(false)
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = "{\n" +
      "        \"Foo\" : \"Bar\",\n" +
      "        \"Account\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Amount\": \"10000000\",\n" +
      "        \"AttestationRewardAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"AttestationSignerAccount\": \"rsqvD8WFFEBBv4nztpoW9YYXJ7eRzLrtc3\",\n" +
      "        \"Destination\": \"rJdTJRJZ6GXCCRaamHJgEqVzB7Zy4557Pi\",\n" +
      "        \"Fee\": \"20\",\n" +
      "        \"LastLedgerSequence\": 19,\n" +
      "        \"OtherChainSource\": \"raFcdz1g8LWJDJWJE2ZKLRGdmUmsTyxaym\",\n" +
      "        \"PublicKey\": \"ED7541DEC700470F54276C90C333A13CDBB5D341FD43C60CEA12170F6D6D4E1136\",\n" +
      "        \"Sequence\": 9,\n" +
      "        \"Signature\": \"7C175050B08000AD35EEB2D87E16CD3F95A0AEEBF2A049474275153D9D4DD44528FE99AA5" +
      "0E71660A15B0B768E1B90E609BBD5DC7AFAFD45D9705D72D40EA10C\",\n" +
      "        \"SigningPubKey\": \"ED0406B134786FE0751717226657F7BF8AFE96442C05D28ACEC66FB64852BA604C\",\n" +
      "        \"TransactionType\": \"XChainAddClaimAttestation\",\n" +
      "        \"WasLockingChainSend\": 0,\n" +
      "        \"XChainBridge\": {\n" +
      "          \"IssuingChainDoor\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "          \"IssuingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          },\n" +
      "          \"LockingChainDoor\": \"rDJVtEuDKr4rj1B3qtW7R5TVWdXV2DY7Qg\",\n" +
      "          \"LockingChainIssue\": {\n" +
      "            \"currency\": \"XRP\"\n" +
      "          }\n" +
      "        },\n" +
      "        \"XChainClaimID\": \"1\"\n" +
      "      }";

    assertCanSerializeAndDeserialize(attestation, json);
  }
}