package org.xrpl.xrpl4j.model.flags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class AccountSetTransactionFlagsTests extends AbstractFlagsTest {

  public static Stream<Arguments> data() {
    return getBooleanCombinations(7);
  }

  @ParameterizedTest
  @MethodSource("data")
  public void testFlagsConstructionWithIndividualFlags(
    boolean tfFullyCanonicalSig,
    boolean tfRequireDestTag,
    boolean tfOptionalDestTag,
    boolean tfRequireAuth,
    boolean tfOptionalAuth,
    boolean tfDisallowXrp,
    boolean tfAllowXrp
  ) {
    Flags.AccountSetTransactionFlags.Builder builder = Flags.AccountSetTransactionFlags.builder()
      .tfFullyCanonicalSig(tfFullyCanonicalSig);

    if (tfRequireDestTag) {
      builder.tfRequireDestTag();
    }

    if (tfOptionalDestTag) {
      builder.tfOptionalDestTag();
    }

    if (tfRequireAuth) {
      builder.tfRequireAuth();
    }

    if (tfOptionalAuth) {
      builder.tfOptionalAuth();
    }

    if (tfDisallowXrp) {
      builder.tfDisallowXrp();
    }

    if (tfAllowXrp) {
      builder.tfAllowXrp();
    }

    if (tfRequireDestTag && tfOptionalDestTag) {
      assertThatThrownBy(
        builder::build
      ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("tfRequireDestTag and tfOptionalDestTag cannot both be set to true.");
      return;
    }

    if (tfRequireAuth && tfOptionalAuth) {
      assertThatThrownBy(
        builder::build
      ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("tfRequireAuth and tfOptionalAuth cannot both be set to true.");
      return;
    }

    if (tfDisallowXrp && tfAllowXrp) {
      assertThatThrownBy(
        builder::build
      ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("tfDisallowXrp and tfAllowXrp cannot both be set to true.");
      return;
    }

    Flags.AccountSetTransactionFlags flags = builder.build();
    long expectedFlags = getExpectedFlags(
      tfFullyCanonicalSig,
      tfRequireDestTag,
      tfOptionalDestTag,
      tfRequireAuth,
      tfOptionalAuth,
      tfDisallowXrp,
      tfAllowXrp
    );
    assertThat(flags.getValue()).isEqualTo(expectedFlags);
  }

  @ParameterizedTest
  @MethodSource("data")
  public void testDeriveIndividualFlagsFromFlags(
    boolean tfFullyCanonicalSig,
    boolean tfRequireDestTag,
    boolean tfOptionalDestTag,
    boolean tfRequireAuth,
    boolean tfOptionalAuth,
    boolean tfDisallowXrp,
    boolean tfAllowXrp
  ) {
    long expectedFlags = getExpectedFlags(
      tfFullyCanonicalSig,
      tfRequireDestTag,
      tfOptionalDestTag,
      tfRequireAuth,
      tfOptionalAuth,
      tfDisallowXrp,
      tfAllowXrp
    );

    if (tfRequireDestTag && tfOptionalDestTag) {
      assertThatThrownBy(
        () -> Flags.AccountSetTransactionFlags.of(expectedFlags)
      ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("tfRequireDestTag and tfOptionalDestTag cannot both be set to true.");
      return;
    }

    if (tfRequireAuth && tfOptionalAuth) {
      assertThatThrownBy(
        () -> Flags.AccountSetTransactionFlags.of(expectedFlags)
      ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("tfRequireAuth and tfOptionalAuth cannot both be set to true.");
      return;
    }

    if (tfDisallowXrp && tfAllowXrp) {
      assertThatThrownBy(
        () -> Flags.AccountSetTransactionFlags.of(expectedFlags)
      ).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("tfDisallowXrp and tfAllowXrp cannot both be set to true.");
      return;
    }

    Flags.AccountSetTransactionFlags flags = Flags.AccountSetTransactionFlags.of(expectedFlags);

    assertThat(flags.getValue()).isEqualTo(expectedFlags);
    assertThat(flags.tfFullyCanonicalSig()).isEqualTo(tfFullyCanonicalSig);
    assertThat(flags.tfRequireDestTag()).isEqualTo(tfRequireDestTag);
    assertThat(flags.tfRequireAuth()).isEqualTo(tfRequireAuth);
    assertThat(flags.tfOptionalAuth()).isEqualTo(tfOptionalAuth);
    assertThat(flags.tfDisallowXrp()).isEqualTo(tfDisallowXrp);
    assertThat(flags.tfAllowXrp()).isEqualTo(tfAllowXrp);
  }

  private long getExpectedFlags(
    boolean tfFullyCanonicalSig,
    boolean tfRequireDestTag,
    boolean tfOptionalDestTag,
    boolean tfRequireAuth,
    boolean tfOptionalAuth,
    boolean tfDisallowXrp,
    boolean tfAllowXrp
  ) {
    return (tfFullyCanonicalSig ? Flags.AccountSetTransactionFlags.FULLY_CANONICAL_SIG.getValue() : 0L) |
      (tfRequireDestTag ? Flags.AccountSetTransactionFlags.REQUIRE_DEST_TAG.getValue() : 0L) |
      (tfOptionalDestTag ? Flags.AccountSetTransactionFlags.OPTIONAL_DEST_TAG.getValue() : 0L) |
      (tfRequireAuth ? Flags.AccountSetTransactionFlags.REQUIRE_AUTH.getValue() : 0L) |
      (tfOptionalAuth ? Flags.AccountSetTransactionFlags.OPTIONAL_AUTH.getValue() : 0L) |
      (tfDisallowXrp ? Flags.AccountSetTransactionFlags.DISALLOW_XRP.getValue() : 0L) |
      (tfAllowXrp ? Flags.AccountSetTransactionFlags.ALLOW_XRP.getValue() : 0L);
  }
}
