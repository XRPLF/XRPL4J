package org.xrpl.xrpl4j.model.transactions;

import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;

class DidSetTest extends AbstractJsonTest {

  @Test
  void testJsonWithNonEmptyFields() throws JSONException, JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .didDocument(DidDocument.of("697066733A2F2F62616679626569676479727A74357366703775646D3768753736756" +
        "8377932366E6634646675796C71616266336F636C67747179353566627A6469"))
      .uri(DidUri.of("697066733A2F2F62616679626569676479727A74357366703775646D3768753736756" +
        "8377932366E6634646675796C71616266336F636C67747179353566627A6469"))
      .data(DidData.of("697066733A2F2F62616679626569676479727A74357366703775646D3768753736756" +
        "8377932366E6634646675796C71616266336F636C67747179353566627A6469"))
      .signingPublicKey(ED_PUBLIC_KEY)
      .build();

    String json = String.format("{\n" +
      "  \"TransactionType\": \"DIDSet\",\n" +
      "  \"Account\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"DIDDocument\": \"697066733A2F2F62616679626569676479727A74357366703775646D37687537367568377932" +
      "366E6634646675796C71616266336F636C67747179353566627A6469\",\n" +
      "  \"URI\": \"697066733A2F2F62616679626569676479727A74357366703775646D37687537367568377932366E663464" +
      "6675796C71616266336F636C67747179353566627A6469\",\n" +
      "  \"Data\": \"697066733A2F2F62616679626569676479727A74357366703775646D37687537367568377932366E66346" +
      "46675796C71616266336F636C67747179353566627A6469\",\n" +
      "  \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithEmptyFields() throws JSONException, JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .didDocument(DidDocument.of(""))
      .uri(DidUri.of(""))
      .data(DidData.of(""))
      .signingPublicKey(ED_PUBLIC_KEY)
      .build();
    String json = String.format("{\n" +
      "  \"TransactionType\": \"DIDSet\",\n" +
      "  \"Account\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"DIDDocument\": \"\",\n" +
      "  \"URI\": \"\",\n" +
      "  \"Data\": \"\",\n" +
      "  \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithUnsetFlags() throws JSONException, JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .didDocument(DidDocument.of(""))
      .flags(TransactionFlags.UNSET)
      .signingPublicKey(ED_PUBLIC_KEY)
      .build();
    String json = String.format("{\n" +
      "  \"TransactionType\": \"DIDSet\",\n" +
      "  \"Account\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"DIDDocument\": \"\",\n" +
      "  \"Flags\": 0,\n" +
      "  \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithSetFlags() throws JSONException, JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .didDocument(DidDocument.of(""))
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .signingPublicKey(ED_PUBLIC_KEY)
      .build();
    String json = String.format("{\n" +
      "  \"TransactionType\": \"DIDSet\",\n" +
      "  \"Account\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"DIDDocument\": \"\",\n" +
      "  \"Flags\": %s,\n" +
      "  \"SigningPubKey\":\"%s\"\n" +
      "}", TransactionFlags.FULLY_CANONICAL_SIG, ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  void testJsonWithUnknownFields() throws JSONException, JsonProcessingException {
    DidSet transaction = DidSet.builder()
      .account(Address.of("rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh"))
      .fee(XrpCurrencyAmount.ofDrops(10))
      .sequence(UnsignedInteger.valueOf(391))
      .didDocument(DidDocument.of(""))
      .uri(DidUri.of(""))
      .data(DidData.of(""))
      .signingPublicKey(ED_PUBLIC_KEY)
      .putUnknownFields("Foo", "Bar")
      .build();

    String json = String.format("{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"TransactionType\": \"DIDSet\",\n" +
      "  \"Account\": \"rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh\",\n" +
      "  \"Fee\": \"10\",\n" +
      "  \"Sequence\": 391,\n" +
      "  \"DIDDocument\": \"\",\n" +
      "  \"URI\": \"\",\n" +
      "  \"Data\": \"\",\n" +
      "  \"SigningPubKey\":\"%s\"\n" +
      "}", ED_PUBLIC_KEY.base16Value());

    assertCanSerializeAndDeserialize(transaction, json);
  }
}