package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.mockito.Mockito.when;

public class ReservedPositionsTest {

    @Test
    public void constructorThrowsException_ifAvailablePositionsAreNotInValidRange() {
        final int memorySize = 2;
        final int blockSize = 2;
        final Random random = Mockito.mock(Random.class);
        Assertions.assertThatThrownBy(() -> new ReservedPositions(
                new TestMemory(memorySize, blockSize), blockSize, random, -1)
        ).isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> new ReservedPositions(
                new TestMemory(memorySize, blockSize), blockSize, random, memorySize * blockSize * Byte.SIZE + 1)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void throwsOutOfMemoryException_ifAllAvailablePositionsAreReserved() {
        final Random random = new Random();
        final ReservedPositions uut = new ReservedPositions(new TestMemory(1, 1), 1, random, 1);
        uut.reservePosition();
        Assertions.assertThatThrownBy(() -> uut.reservePosition()).isInstanceOf(OutOfMemoryException.class);
    }

    @Test
    public void reservesPositions() {
        final int memorySize = 2;
        final int blockSize = 3;
        final long availablePositions = memorySize * blockSize * Byte.SIZE;

        final long bitOffset = 2;
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, availablePositions)).thenReturn(LongStream.iterate(bitOffset, x -> bitOffset));

        final ReservedPositions uut = new ReservedPositions(new TestMemory(memorySize, blockSize), blockSize, random, availablePositions);

        LongStream.range(bitOffset, availablePositions).forEach(
                expected -> Assertions.assertThat(uut.reservePosition()).isEqualTo(expected)
        );

        LongStream.range(0, bitOffset).forEach(
                expected -> Assertions.assertThat(uut.reservePosition()).isEqualTo(expected)
        );
        Assertions.assertThatThrownBy(() -> uut.reservePosition()).isInstanceOf(OutOfMemoryException.class);
    }

    @Test
    public void releasesPositions() {
        final int memorySize = 2;
        final int blockSize = 3;
        final long availablePositions = memorySize * blockSize * Byte.SIZE - 3;

        final long bitOffset = 2;
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, availablePositions)).thenReturn(LongStream.iterate(bitOffset, x -> bitOffset));

        final ReservedPositions uut = new ReservedPositions(new TestMemory(memorySize, blockSize), blockSize, random, availablePositions);

        uut.reservePosition();
        uut.releasePosition(bitOffset);
        Assertions.assertThat(uut.reservePosition()).isEqualTo(bitOffset);
    }


    @Test
    public void releasePositionThrowsException_ifPositionWasNotReserved() {
        final Random random = new Random();
        final ReservedPositions uut = new ReservedPositions(new TestMemory(1, 1), 1, random, 1);
        Assertions.assertThatThrownBy(() -> uut.releasePosition(0L)).isInstanceOf(IllegalArgumentException.class);
    }
}