package org.xrpl.xrpl4j.model.transactions;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.TransactionFlags;
import org.xrpl.xrpl4j.model.ledger.SignerEntryWrapper;

import java.util.List;

/**
 * The SignerListSet transaction creates, replaces, or removes a list of signers that can be used to multi-sign a
 * {@link Transaction}.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableSignerListSet.class)
@JsonDeserialize(as = ImmutableSignerListSet.class)
public interface SignerListSet extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableSignerListSet.Builder}.
   */
  static ImmutableSignerListSet.Builder builder() {
    return ImmutableSignerListSet.builder();
  }

  /**
   * Set of {@link TransactionFlags}s for this {@link SignerListSet}, which only allows the {@code tfFullyCanonicalSig}
   * flag, which is deprecated.
   *
   * <p>The value of the flags cannot be set manually, but exists for JSON serialization/deserialization only and for
   * proper signature computation in rippled.
   *
   * @return Always {@link TransactionFlags#EMPTY}.
   */
  @JsonProperty("Flags")
  @Value.Default
  default TransactionFlags flags() {
    return TransactionFlags.EMPTY;
  }

  /**
   * A target number for the signer weights. A multi-signature from this list is valid only if the sum weights of the
   * signatures provided is greater than or equal to this value. To delete a signer list, use the value 0.
   *
   * @return An {@link UnsignedInteger} representing the singer quorum.
   */
  @JsonProperty("SignerQuorum")
  UnsignedInteger signerQuorum();

  /**
   * (Omitted when deleting) Array of {@link org.xrpl.xrpl4j.model.ledger.SignerEntry} objects, indicating the addresses
   * and weights of signers in this list. This signer list must have at least 1 member and no more than 8 members. No
   * {@link Address} may appear more than once in the list, nor may the {@link #account()} submitting the transaction
   * appear in the list.
   *
   * @return A {@link List} of {@link SignerEntryWrapper}s.
   */
  @JsonProperty("SignerEntries")
  List<SignerEntryWrapper> signerEntries();

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default SignerListSet normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.SIGNER_LIST_SET);
    return this;
  }
  
}
