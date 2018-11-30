package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Memory extends Splittable<Memory>{
    long size();
    DataBlock at(long position);

    @Override
    default Pair<Memory, Memory> split(final long position) {
        final Memory first = new SplitMemory(this, 0, position);
        final Memory second = new SplitMemory(this, position, size());
            return new Pair<>(first, second);
    }

    default Memory safe() {
        return new SafeMemory(this);
    }
}

