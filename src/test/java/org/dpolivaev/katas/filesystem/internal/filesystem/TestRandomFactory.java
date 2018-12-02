package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

class TestRandomFactory {
    static Random mockRandomWithSequence_0toN() {
        final Random random = Mockito.mock(Random.class);
        Mockito.when(random.longs(ArgumentMatchers.eq(0L), ArgumentMatchers.anyLong())).thenReturn(LongStream.iterate(0L, x -> x + 1));
        return random;
    }
}
