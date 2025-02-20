package org.xrpl.xrpl4j.model.transactions.json;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: model
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.model.AbstractJsonTest;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.TransactionType;

import java.util.Objects;

/**
 * An abstract class that helps test various flavors of serialization and deserialization for implementations of
 * {@link Transaction}.
 *
 * @param <I> The type of Immutable this class will test.
 * @param <B> The type of Immutable Builder this class will test.
 * @param <T> The type of {@link Transaction} this class will test.
 */
public abstract class AbstractTransactionJsonTest<I, B, T extends Transaction> extends AbstractJsonTest {

  private final Class<T> interfaceClass;
  private final Class<I> immutableClass;
  private final TransactionType expectedTransactionType;

  /**
   * Required-args Constructor.
   *
   * @param interfaceClass          A class of type {@link T} that for the Immutables intefarce.
   * @param immutableClass          A class of type {@link I} that for the Immutables implementation.
   * @param expectedTransactionType A {@link TransactionType} representing the expected transaction type.
   */
  protected AbstractTransactionJsonTest(
    final Class<T> interfaceClass,
    final Class<I> immutableClass,
    final TransactionType expectedTransactionType
  ) {
    this.interfaceClass = Objects.requireNonNull(interfaceClass);
    this.immutableClass = Objects.requireNonNull(immutableClass);
    this.expectedTransactionType = expectedTransactionType;
  }

  /**
   * Construct and return a builder for the Immutable object of type {@link B}.
   *
   * @return An instance of {@link B}.
   */
  protected abstract B builder();

  /**
   * Construct and return a fully-populated transaction of type {@link T}.
   *
   * @return An instance of {@link T}.
   */
  protected abstract T fullyPopulatedTransaction();

  /**
   * Construct and return a fully-populated transaction of the proper type that includes some unknown fields.
   *
   * @return An instance of {@link T}.
   */
  protected abstract T fullyPopulatedTransactionWithUnknownFields();

  /**
   * Construct and return a minimally-populated transaction of type {@link T}.
   *
   * @return An instance of {@link T}.
   */
  protected abstract T minimallyPopulatedTransaction();

  @Test
  void testTransactionType() {
    assertThat(fullyPopulatedTransaction().transactionType()).isEqualTo(expectedTransactionType);
    assertThat(fullyPopulatedTransactionWithUnknownFields().transactionType()).isEqualTo(expectedTransactionType);
    assertThat(minimallyPopulatedTransaction().transactionType()).isEqualTo(expectedTransactionType);
  }

  /**
   * This test asserts that deserialization works the same regardless of whether a Java interface, immutable, or
   * instance of the {@link Transaction} parent class are supplied to the {@link ObjectMapper}.
   */
  @Test
  public void assertCanDeserializeAllTypesFullyPopulated() throws JsonProcessingException {
    T expectedObject = fullyPopulatedTransaction();
    String serialized = objectMapper.writeValueAsString(expectedObject);

    Transaction deserializedTransaction = objectMapper.readValue(serialized, Transaction.class);
    assertThat(deserializedTransaction).isEqualTo(expectedObject);
    T deserializedInterface = objectMapper.readValue(serialized, interfaceClass);
    assertThat(deserializedInterface).isEqualTo(expectedObject);
    I deserializedImmutable = objectMapper.readValue(serialized, immutableClass);
    assertThat(deserializedImmutable).isEqualTo(expectedObject);
  }

  /**
   * This test asserts that deserialization works the same regardless of whether a Java interface, immutable, or
   * instance of the {@link Transaction} parent class are supplied to the {@link ObjectMapper}.
   */
  @Test
  public void assertCanDeserializeAllTypesFullyPopulatedWithUnknownFields() throws JsonProcessingException {
    T expectedObject = fullyPopulatedTransactionWithUnknownFields();
    String serialized = objectMapper.writeValueAsString(expectedObject);

    Transaction deserializedTransaction = objectMapper.readValue(serialized, Transaction.class);
    assertThat(deserializedTransaction).isEqualTo(expectedObject);
    T deserializedInterface = objectMapper.readValue(serialized, interfaceClass);
    assertThat(deserializedInterface).isEqualTo(expectedObject);
    I deserializedImmutable = objectMapper.readValue(serialized, immutableClass);
    assertThat(deserializedImmutable).isEqualTo(expectedObject);
  }

  /**
   * This test asserts that deserialization works the same regardless of whether a Java interface, immutable, or
   * instance of the {@link Transaction} parent class are supplied to the {@link ObjectMapper}.
   */
  @Test
  public void assertCanDeserializeAllTypesWithMinimallyPopulated() throws JsonProcessingException {
    T expectedObject = minimallyPopulatedTransaction();
    String serialized = objectMapper.writeValueAsString(expectedObject);

    Transaction deserializedTransaction = objectMapper.readValue(serialized, Transaction.class);
    assertThat(deserializedTransaction).isEqualTo(expectedObject);
    T deserializedInterface = objectMapper.readValue(serialized, interfaceClass);
    assertThat(deserializedInterface).isEqualTo(expectedObject);
    I deserializedImmutable = objectMapper.readValue(serialized, immutableClass);
    assertThat(deserializedImmutable).isEqualTo(expectedObject);
  }

}