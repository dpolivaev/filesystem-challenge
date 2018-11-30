package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Memory;

public class HeapMemory implements Memory {
    @Override
    public DataBlock at(long number) {
        return null;
    }
}
