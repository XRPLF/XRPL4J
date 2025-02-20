package org.xrpl.xrpl4j.model.transactions;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
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
import com.google.common.primitives.UnsignedLong;
import org.immutables.value.Value;
import org.xrpl.xrpl4j.model.flags.NfTokenMintFlags;

import java.util.Optional;

/**
 * The {@link NfTokenMint} transaction creates an NfToken object and adds it to the
 * relevant NfTokenPage object of the minter(issuer). If the transaction is
 * successful, the newly minted token will be owned by the minter(issuer) account
 * specified by the transaction.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableNfTokenMint.class)
@JsonDeserialize(as = ImmutableNfTokenMint.class)
public interface NfTokenMint extends Transaction {

  /**
   * Construct a builder for this class.
   *
   * @return An {@link ImmutableNfTokenMint.Builder}.
   */
  static ImmutableNfTokenMint.Builder builder() {
    return ImmutableNfTokenMint.builder();
  }

  /**
   * Indicates the taxon associated with this token. The taxon is generally a
   * value chosen by the minter of the token and a given taxon may be used for
   * multiple tokens. The implementation reserves taxon identifiers greater
   * than or equal to 2147483648 (0x80000000).
   *
   * @return Taxon of type {@link int} associated with the Token.
   */
  @JsonProperty("NFTokenTaxon")
  UnsignedLong tokenTaxon();

  /**
   * Indicates the account that should be the issuer of this token. This value
   * is optional and should only be specified if the account executing the
   * transaction is not the Issuer of the `NfToken` object. If it is
   * present, the MintAccount field in the AccountRoot of the `Issuer`
   * field must match the `Account`, otherwise the transaction will fail.
   *
   * @return An {@link Optional} field Issuer of type {@link Address}.
   */
  @JsonProperty("Issuer")
  Optional<Address> issuer();

  /**
   * Specifies the fee charged by the issuer for secondary sales of the Token,
   * if such sales are allowed. Valid values for this field are between 0 and
   * 50000 inclusive, allowing transfer rates between 0.000% and 50.000% in
   * increments of 0.001%. This field must NOT be present if the
   * `tfTransferable` flag is not set.
   *
   * @return An {@link Optional} {@link TransferFee}.
   */
  @JsonProperty("TransferFee")
  Optional<TransferFee> transferFee();

  /**
   * URI that points to the data and/or metadata associated with the NfT.
   * This field need not be an HTTP or HTTPS URL; it could be an IPFS URI, a
   * magnet link, immediate data encoded as an RFC2379 "data" URL, or even an
   * opaque issuer-specific encoding. The URI is NOT checked for validity, but
   * the field is limited to a maximum length of 256 bytes.
   *
   * @return An {@link Optional} field URI of type {@link String}.
   */
  @JsonProperty("URI")
  Optional<NfTokenUri> uri();

  /**
   * Set of {@link NfTokenMintFlags}s for this {@link NfTokenMint}.
   *
   * @return The {@link NfTokenMintFlags} for this transaction.
   */
  @JsonProperty("Flags")
  @Value.Default
  default NfTokenMintFlags flags() {
    return NfTokenMintFlags.empty();
  }

  /**
   * Immutables Check to ensure property state after construction.
   */
  @Value.Check
  default NfTokenMint normalize() {
    Preconditions.checkState(!unknownFields().containsKey("TransactionType"));
    Preconditions.checkState(!unknownFields().containsKey("Account"));
    Preconditions.checkState(transactionType() == TransactionType.NFTOKEN_MINT);
    return this;
  }
}
