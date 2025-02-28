package org.xrpl.xrpl4j.codec.binary.types;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: binary-codec
 * %%
 * Copyright (C) 2020 - 2022 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xrpl.xrpl4j.codec.binary.definitions.DefinitionsService;
import org.xrpl.xrpl4j.codec.fixtures.FixtureUtils;
import org.xrpl.xrpl4j.codec.fixtures.data.ValueTest;

import java.io.IOException;
import java.util.stream.Stream;

abstract class BaseSerializerTypeTest {

  protected static Stream<Arguments> dataDrivenFixturesForType(SerializedType serializedType) throws IOException {
    return FixtureUtils.getDataDrivenFixtures().valuesTests()
      .stream()
      .filter(fixture -> fixture.type().equals(SerializedType.getNameByType(serializedType)))
      .map(Arguments::of);
  }

  abstract SerializedType getType();

  @ParameterizedTest
  @MethodSource("dataDrivenFixtures")
  void fixtureTests(ValueTest fixture) throws IOException {
    SerializedType<?> serializedType = getType();
    JsonNode value = getValue(fixture);
    if (fixture.error() != null) {
      assertThrows(Exception.class, () -> serializedType.fromJson(value));
    } else {
      SerializedType<?> serialized = serializedType.fromJson(value);
      if (fixture.type().equals("Amount")) {
        AmountType amountType = (AmountType) serialized;
        assertThat(amountType.isPositive()).isEqualTo(!fixture.isNegative());
        assertThat(amountType.isNative()).isEqualTo(fixture.isNative());
      }
      assertThat(serialized.toHex()).isEqualTo(fixture.expectedHex());
    }
  }

  private JsonNode getValue(ValueTest test) {
    return DefinitionsService.getInstance()
      .mapFieldSpecialization(test.typeSpecializationField(), test.testJson().asText())
      .map(val -> val.toString())
      .map(TextNode::new)
      .map(JsonNode.class::cast)
      .orElse(test.testJson());
  }

}
