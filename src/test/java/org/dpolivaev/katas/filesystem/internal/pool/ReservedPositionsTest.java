package org.dpolivaev.katas.filesystem.internal.pool;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.OutOfMemoryException;
import org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservedPositionsTest {

    private static final int MEMORY_SIZE = 2;
    private static final int PAGE_SIZE = 3 * Long.BYTES;
    private static final long AVAILABLE_POSITIONS = MEMORY_SIZE * PAGE_SIZE * Byte.SIZE;

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
        final ReservedPositions uut = new ReservedPositions(new TestPages(1, Long.BYTES), 1, random);
        uut.reservePosition();
        Assertions.assertThatThrownBy(() -> uut.reservePosition()).isInstanceOf(OutOfMemoryException.class);
    }

    @Test
    public void reservesPosition_5() {
        final int memorySize = 2;
        final int pageSize = 3 * Long.BYTES;
        final long availablePositions = memorySize * pageSize * Byte.SIZE;

        final long bitOffset = 0;
        final Random random = TestRandomFactory.mockRandomWithConstantValue(availablePositions, bitOffset);
        final ReservedPositions uut = new ReservedPositions(new TestPages(memorySize, pageSize), availablePositions, random);

        uut.reservePosition(5);
        assertThat(uut.isReserved(5)).isTrue();
    }

    @Test
    public void reservesPosition_34() {
        final int availablePositions = 1000;
        final Random random = TestRandomFactory.mockRandomWithConstantValue(availablePositions, 34);
        final ReservedPositions uut = new ReservedPositions(new TestPages(100, 100), availablePositions, random);
        uut.reservePosition();
        assertThat(uut.isReserved(34)).isTrue();
    }

    @Test
    public void reservesPosition_1025() {
        final Random random = TestRandomFactory.mockRandomWithSequenceFrom0();
        final ReservedPositions uut = new ReservedPositions(new TestPages(100, 100), 1026, random);
        uut.reservePosition(1025);
        assertThat(uut.isReserved(1025)).isTrue();
    }
    @Test
    public void reservesAllPositions() {
        final long bitOffset = 2;
        final ReservedPositions uut = createDeterministicTestTarget(bitOffset);

        LongStream.range(bitOffset, AVAILABLE_POSITIONS).forEach(
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
    public void releasesAllPositions() {
        final long bitOffset = 2;
        final ReservedPositions uut = createDeterministicTestTarget(bitOffset);

        final long reservedPosition = uut.reservePosition();
        assertThat(reservedPosition).isEqualTo(bitOffset);
        uut.releasePosition(bitOffset);
        assertThat(uut.isReserved(bitOffset)).isFalse();
        assertThatReservedPositionIsExpected(uut, bitOffset);
    }

    private ReservedPositions createDeterministicTestTarget(final long bitOffset) {

        final Random random = TestRandomFactory.mockRandomWithConstantValue(AVAILABLE_POSITIONS, bitOffset);

        return new ReservedPositions(new TestPages(MEMORY_SIZE, PAGE_SIZE), AVAILABLE_POSITIONS, random);
    }


    @Test
    public void releasePositionThrowsException_ifPositionWasNotReserved() {
        final Random random = new Random();
        final ReservedPositions uut = new ReservedPositions(new TestPages(1, 1), 1, random);
        Assertions.assertThatThrownBy(() -> uut.releasePosition(0L)).isInstanceOf(IllegalArgumentException.class);
    }
}