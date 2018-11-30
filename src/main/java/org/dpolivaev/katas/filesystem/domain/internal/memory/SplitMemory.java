package org.dpolivaev.katas.filesystem.domain.internal.memory;

class SplitMemory implements Memory {

    private final Memory source;
    private final long start;
    private final long end;

    SplitMemory(final Memory source, final long start, final long end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public long size() {
        return end - start;
    }

    @Override
    public DataBlock at(final long position) {
        return source.at(start + position);
    }

    @Override
    public Pair<Memory, Memory> split(final long position) {
        final long splitPosition = start + position;
        final Memory first = new SplitMemory(this, start, splitPosition);
        final Memory second = new SplitMemory(this, splitPosition, this.end);
        return new Pair<>(first, second);
    }
}
