package org.xrpl.xrpl4j.model.jackson.modules;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.xrpl.xrpl4j.model.client.specifiers.LedgerIndexBound;

import java.io.IOException;

/**
 * Custom Jackson serializer for {@link LedgerIndexBound}s.
 */
public class LedgerIndexBoundSerializer extends StdSerializer<LedgerIndexBound> {

  public LedgerIndexBoundSerializer() {
    super(LedgerIndexBound.class, false);
  }

  @Override
  public void serialize(
    LedgerIndexBound ledgerIndexBound,
    JsonGenerator jsonGenerator,
    SerializerProvider serializerProvider
  ) throws IOException {
    jsonGenerator.writeNumber(ledgerIndexBound.value());
  }
}
