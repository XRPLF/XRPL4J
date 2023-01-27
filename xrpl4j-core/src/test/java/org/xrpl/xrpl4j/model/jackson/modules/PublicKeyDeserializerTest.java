package org.xrpl.xrpl4j.model.jackson.modules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xrpl.xrpl4j.crypto.TestConstants.ED_PUBLIC_KEY_HEX;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;

import java.io.IOException;

/**
 * Unit tests for {@link PublicKeyDeserializer}.
 */
class PublicKeyDeserializerTest {

  private PublicKeyDeserializer deserializer;

  @BeforeEach
  void setUp() {
    deserializer = new PublicKeyDeserializer();
  }

  @Test
  void testDeserialize() throws IOException {
    PublicKey expected = PublicKey.fromBase16EncodedPublicKey(ED_PUBLIC_KEY_HEX);
    JsonParser mockJsonParser = mock(JsonParser.class);
    when(mockJsonParser.getText()).thenReturn(ED_PUBLIC_KEY_HEX);
    PublicKey publicKey = deserializer.deserialize(mockJsonParser, mock(DeserializationContext.class));
    assertThat(publicKey).isEqualTo(expected);
  }

  @Test
  void testDeserializeEmptyString() throws IOException {
    PublicKey expected = PublicKey.MULTI_SIGN_PUBLIC_KEY;
    JsonParser mockJsonParser = mock(JsonParser.class);
    when(mockJsonParser.getText()).thenReturn(PublicKey.MULTI_SIGN_PUBLIC_KEY.base16Value());
    PublicKey publicKey = deserializer.deserialize(mockJsonParser, mock(DeserializationContext.class));
    assertThat(publicKey).isEqualTo(expected);
  }

}