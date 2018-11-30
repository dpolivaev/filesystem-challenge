package org.dpolivaev.katas.filesystem.domain.internal.memory;

class SplitMemory implements Memory {

    private final Memory source;
    private final long start;
    private final long end;

    SplitMemory(Memory source, long start, long end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public long blockCount() {
        return end - start;
    }

    @Override
    public DataBlock at(long position) {
        return source.at(start + position);
    }

    @Override
    public Pair<Memory, Memory> split(long position) {
        long splitPosition = start + position;
        Memory first = new SplitMemory(this, start, splitPosition);
        Memory second = new SplitMemory(this, splitPosition, this.end);
        return new Pair<>(first, second);
    }
}
