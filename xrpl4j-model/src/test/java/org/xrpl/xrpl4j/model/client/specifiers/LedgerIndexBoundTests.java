package org.xrpl.xrpl4j.model.client.specifiers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Test;

public class LedgerIndexBoundTests {

  @Test
  void constructValidBounds() {
    LedgerIndexBound one = LedgerIndexBound.of(1);
    assertThat(one.value()).isEqualTo(1);

    LedgerIndexBound maxValue = LedgerIndexBound.of(Long.MAX_VALUE);
    assertThat(maxValue.value()).isEqualTo(Long.MAX_VALUE);

    LedgerIndexBound negativeOne = LedgerIndexBound.of(-1);
    assertThat(negativeOne.value()).isEqualTo(-1);
  }

  @Test
  void constructInvalidBounds() {
    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(0)
    );

    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(-2)
    );
  }

  @Test
  void addLedgerIndexToBound() {
    LedgerIndexBound ledgerIndexBound = LedgerIndexBound.of(1);
    LedgerIndex ledgerIndex = LedgerIndex.of(UnsignedLong.valueOf(1000));
    LedgerIndexBound added = ledgerIndexBound.plus(ledgerIndex);
    assertThat(added.value()).isEqualTo(1001);
  }

  @Test
  void addLedgerIndexBoundToBound() {
    LedgerIndexBound ledgerIndexBound1 = LedgerIndexBound.of(1);
    LedgerIndexBound ledgerIndexBound2 = LedgerIndexBound.of(1000);
    LedgerIndexBound added1 = ledgerIndexBound1.plus(ledgerIndexBound2);
    assertThat(added1.value()).isEqualTo(1001);
    assertThat(added1).isEqualTo(LedgerIndexBound.of(1001));
    LedgerIndexBound added2 = ledgerIndexBound2.plus(ledgerIndexBound1);
    assertThat(added2.value()).isEqualTo(1001);
    assertThat(added2).isEqualTo(LedgerIndexBound.of(1001));

    assertThat(added2).isEqualTo(added1);
  }

  @Test
  void addLongToBound() {
    LedgerIndexBound ledgerIndexBound = LedgerIndexBound.of(1);
    final LedgerIndexBound added = ledgerIndexBound.plus(1000);
    assertThat(added).isEqualTo(LedgerIndexBound.of(1001));
    assertThat(added.value()).isEqualTo(1001);
  }

  @Test
  void subtractBoundFromBound() {
    LedgerIndexBound ledgerIndexBound1 = LedgerIndexBound.of(1000);
    LedgerIndexBound ledgerIndexBound2 = LedgerIndexBound.of(900);
    LedgerIndexBound subtracted = ledgerIndexBound1.minus(ledgerIndexBound2);
    assertThat(subtracted).isEqualTo(LedgerIndexBound.of(100));
    assertThat(subtracted.value()).isEqualTo(100);

    assertDoesNotThrow(
      () -> ledgerIndexBound1.minus(LedgerIndexBound.of(999))
    );
  }

  @Test
  void subtractBoundTooLarge() {
    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(1000).minus(LedgerIndexBound.of(1000))
    );

    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(1000).minus(LedgerIndexBound.of(10000))
    );
  }

  @Test
  void subtractLedgerIndexFromBound() {
    LedgerIndexBound ledgerIndexBound = LedgerIndexBound.of(1000);
    LedgerIndex ledgerIndex = LedgerIndex.of(UnsignedLong.valueOf(900));
    LedgerIndexBound subtracted = ledgerIndexBound.minus(ledgerIndex);
    assertThat(subtracted).isEqualTo(LedgerIndexBound.of(100));
    assertThat(subtracted.value()).isEqualTo(100L);

    assertDoesNotThrow(
      () -> ledgerIndexBound.minus(LedgerIndex.of(UnsignedLong.valueOf(999)))
    );
  }

  @Test
  void subtractLedgerIndexTooLarge() {
    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(1000).minus(LedgerIndex.of(UnsignedLong.valueOf(1000)))
    );

    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(1000).minus(LedgerIndex.of(UnsignedLong.valueOf(10000)))
    );
  }

  @Test
  void subtractLongFromBound() {
    LedgerIndexBound ledgerIndexBound = LedgerIndexBound.of(1000);
    Long longValue = 900L;
    LedgerIndexBound subtractedLong = ledgerIndexBound.minus(longValue);
    assertThat(subtractedLong).isEqualTo(LedgerIndexBound.of(100));
    assertThat(subtractedLong.value()).isEqualTo(100L);

    Integer intValue = 900;
    LedgerIndexBound subtractedInt = ledgerIndexBound.minus(intValue);
    assertThat(subtractedInt).isEqualTo(LedgerIndexBound.of(100));
    assertThat(subtractedInt.value()).isEqualTo(100L);

    assertDoesNotThrow(
      () -> ledgerIndexBound.minus(999)
    );

    assertDoesNotThrow(
      () -> ledgerIndexBound.minus(999L)
    );
  }

  @Test
  void subtractUnsignedLongTooLarge() {
    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(1000).minus(1000)
    );

    assertThrows(
      IllegalArgumentException.class,
      () -> LedgerIndexBound.of(1000).minus(10000)
    );
  }
}
