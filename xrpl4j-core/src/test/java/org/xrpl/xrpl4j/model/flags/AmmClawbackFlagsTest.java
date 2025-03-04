package org.xrpl.xrpl4j.model.flags;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

class AmmClawbackFlagsTest extends AbstractFlagsTest {

  @Test
  void testFlagWithValue() {
    AmmClawbackFlags flags = AmmClawbackFlags.CLAW_TWO_ASSETS;
    assertThat(flags.isEmpty()).isFalse();

    assertThat(flags.tfClawTwoAssets()).isTrue();
    assertThat(flags.tfFullyCanonicalSig()).isFalse();
    assertThat(flags.getValue()).isEqualTo(1L);
  }

  @Test
  void testEmptyFlags() {
    AmmClawbackFlags flags = AmmClawbackFlags.empty();
    assertThat(flags.isEmpty()).isTrue();

    assertThat(flags.tfClawTwoAssets()).isFalse();
    assertThat(flags.tfFullyCanonicalSig()).isFalse();
    assertThat(flags.getValue()).isZero();
  }

  @Test
  void testJson() throws JSONException, JsonProcessingException {
    TransactionFlagsWrapper wrapper = TransactionFlagsWrapper.of(AmmClawbackFlags.CLAW_TWO_ASSETS);
    String json = String.format("{\n" +
        "               \"flags\": %s\n" +
        "}", AmmClawbackFlags.CLAW_TWO_ASSETS.getValue());

    assertCanSerializeAndDeserialize(wrapper, json);
  }

  @Test
  void testEmptyJson() throws JSONException, JsonProcessingException {
    AmmClawbackFlags flags = AmmClawbackFlags.empty();
    AbstractFlagsTest.TransactionFlagsWrapper wrapper = AbstractFlagsTest.TransactionFlagsWrapper.of(flags);
    String json = "{\n" +
        "}";

    assertCanSerializeAndDeserialize(wrapper, json);
  }
}
