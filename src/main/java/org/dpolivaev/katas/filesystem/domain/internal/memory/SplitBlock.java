package org.dpolivaev.katas.filesystem.domain.internal.memory;

class SplitBlock implements DataBlock {
    private final DataBlock source;
    private final long start;
    private final long end;

    SplitBlock(DataBlock source, long start, long end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public long size() {
        return end - start;
    }

    @Override
    public Pair<DataBlock, DataBlock> split(long position) {
        long splitPosition = start + position;
        DataBlock first = new SplitBlock(source, start, splitPosition);
        DataBlock second = new SplitBlock(source, splitPosition, this.end);
        return new Pair<>(first, second);
    }

    @Override
    public long position() {
        return source.position();
    }

    @Override
    public void set(long offset, byte source) {
        this.source.set(start + offset, source);
    }

    @Override
    public void set(long offset, long length, byte[] source, long sourceOffset) {
        this.source.set(start + offset, length, source, sourceOffset);
    }

    @Override
    public byte getByte(long offset) {
        return source.getByte(start + offset);
    }

    @Override
    public void get(long offset, long length, byte[] destination, long destinationOffset) {
        source.get(offset, length, destination, destinationOffset);
    }
}
