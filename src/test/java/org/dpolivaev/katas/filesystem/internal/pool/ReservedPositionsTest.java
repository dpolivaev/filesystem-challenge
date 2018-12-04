package org.dpolivaev.katas.filesystem.internal.pool;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ReservedPositionsTest {

    @Test
    public void constructorThrowsException_ifAvailablePositionsAreNotInValidRange() {
        final int memorySize = 2;
        final int pageSize = 2;
        final Random random = Mockito.mock(Random.class);
        Assertions.assertThatThrownBy(() -> new ReservedPositions(
                new TestPages(memorySize, pageSize), -1, random)
        ).isInstanceOf(AssertionError.class);
        Assertions.assertThatThrownBy(() -> new ReservedPositions(
                new TestPages(memorySize, pageSize), memorySize * pageSize * Byte.SIZE + 1, random)
        ).isInstanceOf(AssertionError.class);
    }

    @Test
    public void throwsOutOfMemoryException_ifAllAvailablePositionsAreReserved() {
        final Random random = new Random();
        final ReservedPositions uut = new ReservedPositions(new TestPages(1, 1), 1, random);
        uut.reservePosition();
        Assertions.assertThatThrownBy(() -> uut.reservePosition()).isInstanceOf(OutOfMemoryException.class);
    }

    @Test
    public void reservesGivenPosition() {
        final Random random = new Random();
        final ReservedPositions uut = new ReservedPositions(new TestPages(100, 100), 1000, random);
        uut.reservePosition(5);
        assertThat(uut.isReserved(5)).isTrue();
    }

    @Test
    public void reservesRandomPositions() {
        final int memorySize = 2;
        final int pageSize = 3;
        final long availablePositions = memorySize * pageSize * Byte.SIZE;

        final long bitOffset = 2;
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, availablePositions)).thenReturn(LongStream.iterate(bitOffset, x -> bitOffset));

        final ReservedPositions uut = new ReservedPositions(new TestPages(memorySize, pageSize), availablePositions, random);

        LongStream.range(bitOffset, availablePositions).forEach(
                expected -> assertThatReservedPositionIsExpected(uut, expected)
        );

        LongStream.range(0, bitOffset).forEach(
                expected -> assertThatReservedPositionIsExpected(uut, expected)
        );
        Assertions.assertThatThrownBy(() -> uut.reservePosition()).isInstanceOf(OutOfMemoryException.class);
    }

    private void assertThatReservedPositionIsExpected(final ReservedPositions uut, final long expectedPosition) {
        final long position = uut.reservePosition();
        assertThat(position).isEqualTo(expectedPosition);
        assertThat(uut.isReserved(expectedPosition)).isTrue();
    }

    @Test
    public void releasesPositions() {
        final int memorySize = 2;
        final int pageSize = 3;
        final long availablePositions = memorySize * pageSize * Byte.SIZE - 3;

        final long bitOffset = 2;
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, availablePositions)).thenReturn(LongStream.iterate(bitOffset, x -> bitOffset));

        final ReservedPositions uut = new ReservedPositions(new TestPages(memorySize, pageSize), availablePositions, random);

        uut.reservePosition();
        uut.releasePosition(bitOffset);
        assertThat(uut.isReserved(bitOffset)).isFalse();
        assertThatReservedPositionIsExpected(uut, bitOffset);
    }


    @Test
    public void releasePositionThrowsException_ifPositionWasNotReserved() {
        final Random random = new Random();
        final ReservedPositions uut = new ReservedPositions(new TestPages(1, 1), 1, random);
        Assertions.assertThatThrownBy(() -> uut.releasePosition(0L)).isInstanceOf(IllegalArgumentException.class);
    }
}