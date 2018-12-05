package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.mockito.Mockito.when;

public class TestRandomFactory {
    public static Random mockRandomWithSequenceFrom0() {
        return mockRandomWithSequenceFrom(0);
    }

    public static Random mockRandomWithSequenceFrom(final long start) {
        final Random random = Mockito.mock(Random.class);
        Mockito.when(random.longs(ArgumentMatchers.eq(0L), ArgumentMatchers.anyLong())).thenReturn(LongStream.iterate(start, x -> x + 1));
        return random;
    }

    public static Random mockRandomWithConstantValue(final long availablePositions, final long constantValue) {
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, availablePositions)).thenReturn(LongStream.iterate(constantValue, x -> constantValue));
        return random;
    }
}
