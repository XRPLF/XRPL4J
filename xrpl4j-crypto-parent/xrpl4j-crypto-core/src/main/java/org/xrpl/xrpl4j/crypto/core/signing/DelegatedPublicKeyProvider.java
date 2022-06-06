package org.xrpl.xrpl4j.crypto.core.signing;

import org.xrpl.xrpl4j.crypto.core.KeyMetadata;
import org.xrpl.xrpl4j.crypto.core.keys.PublicKey;

/**
 * Defines how to retrieve a {@link PublicKey} in a delegated manner.
 */
public interface DelegatedPublicKeyProvider {

  /**
   * Accessor for the public-key corresponding to the supplied key meta-data. This method exists to support
   * implementations that hold private-key material internally, yet need a way for external callers to determine the
   * actual public key for signature verification or other purposes.
   *
   * @param keyMetadata A {@link KeyMetadata} for a key-pair.
   *
   * @return A {@link PublicKey}.
   */
  PublicKey getPublicKey(KeyMetadata keyMetadata);

}
