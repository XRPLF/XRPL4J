package org.xrpl.xrpl4j.crypto.core.signing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.xrpl.xrpl4j.crypto.core.TestConstants.ED_PUBLIC_KEY;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xrpl.xrpl4j.codec.addresses.KeyType;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;
import org.xrpl.xrpl4j.crypto.core.keys.PublicKey;
import org.xrpl.xrpl4j.model.transactions.Transaction;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests for {@link AbstractTransactionVerifier}.
 */
public class AbstractTransactionVerifierTest {

  @Mock
  SignatureUtils signatureUtilsMock;
  @Mock
  Signature ed25519SignatureMock;
  @Mock
  Signature secp256k1SignatureMock;
  @Mock
  Transaction transactionMock;
  @Mock
  SignatureWithPublicKey signatureWithPublicKeyMock;

  @Mock
  PublicKey edPublicKeyMock;

  @Mock
  PublicKey ecPublicKeyMock;

  private AtomicBoolean ed25519VerifyCalled;
  private AtomicBoolean secp256k1VerifyCalled;

  private AbstractTransactionVerifier transactionVerifier;

  @BeforeEach
  void setUp() {
    openMocks(this);

    ed25519VerifyCalled = new AtomicBoolean(false);
    secp256k1VerifyCalled = new AtomicBoolean(false);

    when(edPublicKeyMock.versionType()).thenReturn(KeyType.ED25519);
    when(ecPublicKeyMock.versionType()).thenReturn(KeyType.SECP256K1);

    when(signatureUtilsMock.toSignableBytes(Mockito.<Transaction>any())).thenReturn(UnsignedByteArray.empty());
    when(signatureUtilsMock.toMultiSignableBytes(any(), any())).thenReturn(UnsignedByteArray.empty());

    when(signatureWithPublicKeyMock.signingPublicKey()).thenReturn(ED_PUBLIC_KEY);
    when(signatureWithPublicKeyMock.transactionSignature()).thenReturn(ed25519SignatureMock);

    this.transactionVerifier = new AbstractTransactionVerifier(signatureUtilsMock) {
      @Override
      protected boolean edDsaVerify(
        PublicKey publicKey, UnsignedByteArray signableTransactionBytes, Signature transactionSignature
      ) {
        ed25519VerifyCalled.set(true);
        return ed25519VerifyCalled.get();
      }

      @Override
      protected boolean ecDsaVerify(
        PublicKey publicKey, UnsignedByteArray signableTransactionBytes, Signature transactionSignature
      ) {
        secp256k1VerifyCalled.set(true);
        return secp256k1VerifyCalled.get();
      }
    };
  }

  ///////////////////
  // verify (signatureWithKeyMetadata, transaction)
  ///////////////////

  @Test
  void verifyWithNullMetadata() {
    assertThrows(NullPointerException.class,
      () -> transactionVerifier.verifyMultiSigned(null, transactionMock));
  }

  @Test
  void verifyWithNullTransaction() {
    assertThrows(NullPointerException.class,
      () -> transactionVerifier.verify(signatureWithPublicKeyMock, null));
  }

  @Test
  void verifyEd25519() {
    when(signatureWithPublicKeyMock.signingPublicKey()).thenReturn(edPublicKeyMock);
    boolean actual = transactionVerifier.verify(signatureWithPublicKeyMock, transactionMock);

    assertThat(actual).isTrue();
    assertThat(ed25519VerifyCalled.get()).isTrue();
    assertThat(secp256k1VerifyCalled.get()).isFalse();
    verify(signatureUtilsMock).toSignableBytes(transactionMock);
    verifyNoMoreInteractions(signatureUtilsMock);
  }

  @Test
  void verifySecp256k1() {
    when(signatureWithPublicKeyMock.signingPublicKey()).thenReturn(ecPublicKeyMock);
    boolean actual = transactionVerifier.verify(signatureWithPublicKeyMock, transactionMock);

    assertThat(actual).isTrue();
    assertThat(secp256k1VerifyCalled.get()).isTrue();
    assertThat(ed25519VerifyCalled.get()).isFalse();
    verify(signatureUtilsMock).toSignableBytes(transactionMock);
    verifyNoMoreInteractions(signatureUtilsMock);
  }

  ///////////////////
  // verify (signatureWithKeyMetadata, transaction)
  ///////////////////

  @Test
  void verifyMultiWithNullSet() {
    assertThrows(NullPointerException.class,
      () -> transactionVerifier.verifyMultiSigned(null, transactionMock));
  }

  @Test
  void verifyMultiWithNullTransaction() {
    assertThrows(NullPointerException.class,
      () -> transactionVerifier.verifyMultiSigned(Sets.newHashSet(), null));
  }

  @Test
  void verifyMultiWithEmptySet() {
    assertThrows(IllegalArgumentException.class,
      () -> transactionVerifier.verifyMultiSigned(Sets.newHashSet(), transactionMock));
  }

  @Test
  void verifyMultiEd25519() {
    when(signatureWithPublicKeyMock.transactionSignature()).thenReturn(ed25519SignatureMock);
    when(signatureWithPublicKeyMock.signingPublicKey()).thenReturn(edPublicKeyMock);

    final Set<SignatureWithPublicKey> signatureWithKeyMetadataSet = Sets.newLinkedHashSet(signatureWithPublicKeyMock);
    boolean actual = transactionVerifier.verifyMultiSigned(signatureWithKeyMetadataSet, transactionMock, 1);

    assertThat(actual).isTrue();
    assertThat(ed25519VerifyCalled.get()).isTrue();
    assertThat(secp256k1VerifyCalled.get()).isFalse();
    verify(signatureUtilsMock).toMultiSignableBytes(any(), any());
    verifyNoMoreInteractions(signatureUtilsMock);
  }

  @Test
  void verifyMultiSecp256k1() {
    when(signatureWithPublicKeyMock.transactionSignature()).thenReturn(secp256k1SignatureMock);
    when(signatureWithPublicKeyMock.signingPublicKey()).thenReturn(ecPublicKeyMock);

    final Set<SignatureWithPublicKey> signatureWithKeyMetadataSet = Sets.newLinkedHashSet(signatureWithPublicKeyMock);
    boolean actual = transactionVerifier.verifyMultiSigned(signatureWithKeyMetadataSet, transactionMock, 1);

    assertThat(actual).isTrue();
    assertThat(secp256k1VerifyCalled.get()).isTrue();
    assertThat(ed25519VerifyCalled.get()).isFalse();
    verify(signatureUtilsMock).toMultiSignableBytes(any(), any());
    verifyNoMoreInteractions(signatureUtilsMock);
  }

  ///////////////////
  // edDsaVerify
  ///////////////////

  @Test
  void edDsaVerify() {
    boolean actual = transactionVerifier.edDsaVerify(edPublicKeyMock, UnsignedByteArray.empty(), ed25519SignatureMock);

    assertThat(actual).isTrue();
    assertThat(ed25519VerifyCalled.get()).isTrue();
    assertThat(secp256k1VerifyCalled.get()).isFalse();
    verifyNoMoreInteractions(signatureUtilsMock);
  }

  ///////////////////
  // ecDsaVerify
  ///////////////////

  @Test
  void ecDsaVerify() {
    boolean actual = transactionVerifier.ecDsaVerify(ecPublicKeyMock, UnsignedByteArray.empty(),
      secp256k1SignatureMock);

    assertThat(actual).isTrue();
    assertThat(secp256k1VerifyCalled.get()).isTrue();
    assertThat(ed25519VerifyCalled.get()).isFalse();
    verifyNoMoreInteractions(signatureUtilsMock);
  }
}
