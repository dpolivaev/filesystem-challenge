package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Memory extends Splittable<Memory>{
    long blockCount();
    DataBlock at(long position);

    @Override
    default  Pair<Memory, Memory> split(long position) {
            Memory first = new SplitMemory(this, 0, position);
            Memory second = new SplitMemory(this, position, blockCount());
            return new Pair<>(first, second);
    }
}

