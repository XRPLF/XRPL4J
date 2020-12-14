package org.xrpl.xrpl4j.codec.addresses;

import com.google.common.primitives.UnsignedInteger;
import org.immutables.value.Value;

/**
 * An address on the XRP Ledger represented in Classic Address form.  This form includes a Base58Check encoded
 * address, as well as a destination tag and an indicator of if the address is on XRPL-testnet or XRPL-mainnet.
 *
 * @see "https://xrpl.org/accounts.html#addresses"
 */
@Value.Immutable
public interface ClassicAddress {

  /**
   * Get a new {@link ImmutableClassicAddress.Builder} instance.
   *
   * @return A {@link ImmutableClassicAddress.Builder}.
   */
  static ImmutableClassicAddress.Builder builder() {
    return ImmutableClassicAddress.builder();
  }

  /**
   * A classic address, as a {@link String}.
   *
   * @return A {@link String} containing the classic address.
   */
  String classicAddress();

  /**
   * The tag of the classic address.
   *
   * @return An {@link UnsignedInteger}.
   */
  UnsignedInteger tag();

  /**
   * Whether or not this address exists on mainnet or testnet.
   *
   * @return {@code true} if it is a tesnet address, {@code false} if it is mainnet.
   */
  boolean test();
}
