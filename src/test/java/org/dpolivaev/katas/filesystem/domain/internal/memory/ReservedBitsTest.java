package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.mockito.Mockito.when;

public class ReservedBitsTest {
    @Test
    public void throwsOutOfMemoryException_ifNoBitsAreAvailable() {
        final Random random = new Random();
        final ReservedBits uut = new ReservedBits(new TestMemory(1, 1), 1, random);
        IntStream.range(0, Byte.SIZE).forEach(x -> uut.reserveBit());
        Assertions.assertThatThrownBy(() -> uut.reserveBit()).isInstanceOf(OutOfMemoryException.class);
    }

    @Test
    public void reservesBits() {
        final int memorySize = 2;
        final int blockSize = 3;
        final Random random = Mockito.mock(Random.class);
        final int blockOffset = 1;
        final int byteOffset = 2;

        when(random.longs(0L, (long) memorySize)).thenReturn(LongStream.iterate(blockOffset, x -> blockOffset));
        when(random.ints(0, blockSize)).thenReturn(IntStream.iterate(byteOffset, x -> byteOffset));

        final ReservedBits uut = new ReservedBits(new TestMemory(memorySize, blockSize), blockSize, random);
        final int bitNumber = memorySize * blockSize * Byte.SIZE;
        final ArrayList<Long> reservedBits = new ArrayList<>(bitNumber);
        for (int blockCount = 0; blockCount < memorySize; blockCount++) {
            for (int byteCount = 0; byteCount < blockSize; byteCount++) {
                for (int bitCount = 0; bitCount < Byte.SIZE; bitCount++) {
                    final long reservedBit = uut.reserveBit();
                    reservedBits.add(reservedBit);
                }
            }
        }
        Assertions.assertThat(reservedBits).containsExactly(
                40L, 41L, 42L, 43L, 44L, 45L, 46L, 47L,
                24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L,
                32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L,
                16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L,
                0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L,
                8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L
        );
    }
}