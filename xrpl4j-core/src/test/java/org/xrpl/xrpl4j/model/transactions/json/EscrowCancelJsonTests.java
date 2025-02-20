package org.xrpl.xrpl4j.model.transactions.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.EscrowCancel;
import org.xrpl.xrpl4j.model.transactions.ImmutableEscrowCancel;
import org.xrpl.xrpl4j.model.transactions.NetworkId;
import org.xrpl.xrpl4j.model.transactions.TransactionType;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

public class EscrowCancelJsonTests
  extends AbstractTransactionJsonTest<ImmutableEscrowCancel, ImmutableEscrowCancel.Builder, EscrowCancel> {

  /**
   * No-args Constructor.
   */
  protected EscrowCancelJsonTests() {
    super(EscrowCancel.class, ImmutableEscrowCancel.class, TransactionType.ESCROW_CANCEL);
  }

  @Override
  protected ImmutableEscrowCancel.Builder builder() {
    return ImmutableEscrowCancel.builder();
  }

  @Override
  protected EscrowCancel fullyPopulatedTransaction() {
    return EscrowCancel.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .owner(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .offerSequence(UnsignedInteger.valueOf(7))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .networkId(NetworkId.of(1024))
      .build();
  }

  @Override
  protected EscrowCancel fullyPopulatedTransactionWithUnknownFields() {
    return builder().from(fullyPopulatedTransaction())
      .putUnknownFields("Foo", "Bar")
      .build();
  }

  @Override
  protected EscrowCancel minimallyPopulatedTransaction() {
    return EscrowCancel.builder()
      .account(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .fee(XrpCurrencyAmount.ofDrops(12))
      .sequence(UnsignedInteger.ONE)
      .owner(Address.of("rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn"))
      .offerSequence(UnsignedInteger.valueOf(7))
      .signingPublicKey(
        PublicKey.fromBase16EncodedPublicKey("02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC")
      )
      .build();
  }

  @Test
  public void testEscrowCancelJson() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"TransactionType\": \"EscrowCancel\",\n" +
      "  \"Owner\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"OfferSequence\": 7,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";
    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testEscrowCancelJsonWithUnsetFlags() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"TransactionType\": \"EscrowCancel\",\n" +
      "  \"Owner\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"OfferSequence\": 7,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"Fee\": \"12\"\n" +
      "}";
    assertCanSerializeAndDeserialize(fullyPopulatedTransaction(), json);
  }

  @Test
  public void testEscrowCancelJsonWithNonZeroFlags() throws JsonProcessingException, JSONException {
    EscrowCancel transaction = builder().from(fullyPopulatedTransaction())
      .flags(TransactionFlags.FULLY_CANONICAL_SIG)
      .build();

    String json =
      "{\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"Fee\": \"12\",\n" +
      "  \"Flags\": " + TransactionFlags.FULLY_CANONICAL_SIG.getValue() + ",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Owner\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"OfferSequence\": 7,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"TransactionType\": \"EscrowCancel\"\n" +
      "}";
    assertCanSerializeAndDeserialize(transaction, json);
  }

  @Test
  public void testEscrowCancelJsonWithUnknownFields() throws JsonProcessingException, JSONException {
    String json =
      "{\n" +
      "  \"Foo\" : \"Bar\",\n" +
      "  \"Account\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"TransactionType\": \"EscrowCancel\",\n" +
      "  \"Owner\": \"rf1BiGeXwwQoi8Z2ueFYTEXSwuJYfV2Jpn\",\n" +
      "  \"OfferSequence\": 7,\n" +
      "  \"Sequence\": 1,\n" +
      "  \"SigningPubKey\" : \"02356E89059A75438887F9FEE2056A2890DB82A68353BE9C0C0C8F89C0018B37FC\",\n" +
      "  \"NetworkID\": 1024,\n" +
      "  \"Fee\": \"12\"\n" +
      "}";
    assertCanSerializeAndDeserialize(fullyPopulatedTransactionWithUnknownFields(), json);
  }
}
