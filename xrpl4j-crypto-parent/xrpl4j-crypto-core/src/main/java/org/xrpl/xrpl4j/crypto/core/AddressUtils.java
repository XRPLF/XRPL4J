package org.xrpl.xrpl4j.crypto.core;

import org.xrpl.xrpl4j.crypto.core.keys.PublicKey;
import org.xrpl.xrpl4j.model.transactions.Address;

/**
 * A utility interface to help with interactions involving XRPL addresses.
 */
public interface AddressUtils {

  /**
   * Derive an XRPL address from a public key.
   *
   * @param publicKey The hexadecimal encoded public key of the account.
   *
   * @return A Base58Check encoded XRPL address in Classic Address form.
   */
  Address deriveAddress(final PublicKey publicKey);

}
