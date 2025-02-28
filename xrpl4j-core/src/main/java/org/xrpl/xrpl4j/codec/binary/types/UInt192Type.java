package org.xrpl.xrpl4j.codec.binary.types;

import com.fasterxml.jackson.databind.JsonNode;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.codec.binary.serdes.BinaryParser;

/**
 * Codec for XRPL UInt192 type.
 */
public class UInt192Type extends UIntType<UInt192Type> {

  public static final int WIDTH_BYTES = 24;

  public UInt192Type() {
    this(UnsignedByteArray.ofSize(WIDTH_BYTES));
  }

  public UInt192Type(UnsignedByteArray list) {
    super(list, WIDTH_BYTES * 8);
  }

  @Override
  public UInt192Type fromParser(BinaryParser parser) {
    return new UInt192Type(parser.read(WIDTH_BYTES));
  }

  @Override
  public UInt192Type fromJson(JsonNode node) {
    return new UInt192Type(UnsignedByteArray.fromHex(node.asText()));
  }
}
