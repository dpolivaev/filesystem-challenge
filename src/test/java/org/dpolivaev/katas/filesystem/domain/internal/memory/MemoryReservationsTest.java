package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Test;

public class MemoryReservationsTest {
    @Test
    public void name() {
        MemoryReservations uut = new MemoryReservations(new TestMemory(100, 1024));

    }
}