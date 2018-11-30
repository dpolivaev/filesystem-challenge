package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Test;

public class ReservedBitsTest {
    @Test
    public void throwsOutOfMemoryException_ifNoBitsAreAvailable() {
        ReservedBits uut = new ReservedBits(new TestMemory(0, 0));
        Assertions.assertThatThrownBy(() -> uut.reserveBit()).isInstanceOf(OutOfMemoryException.class);
    }
    @Test
    public void reservesBits() {
        ReservedBits uut = new ReservedBits(new TestMemory(1, 2));
        int bitCount = 2 * Byte.SIZE;
        for(long i = 0; i < bitCount; i++)
            Assertions.assertThat(uut.reserveBit()).isEqualTo(i);
        Assertions.assertThatThrownBy(() -> uut.reserveBit()).isInstanceOf(OutOfMemoryException.class);
    }
}