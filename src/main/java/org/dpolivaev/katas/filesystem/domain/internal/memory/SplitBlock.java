package org.dpolivaev.katas.filesystem.domain.internal.memory;

class SplitBlock implements DataBlock {
    private final DataBlock source;
    private final long start;
    private final long end;

    SplitBlock(final DataBlock source, final long start, final long end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public long size() {
        return end - start;
    }

    @Override
    public Pair<DataBlock, DataBlock> split(final long position) {
        final long splitPosition = start + position;
        final DataBlock first = new SplitBlock(source, start, splitPosition);
        final DataBlock second = new SplitBlock(source, splitPosition, this.end);
        return new Pair<>(first, second);
    }

    @Override
    public long position() {
        return source.position();
    }

    @Override
    public void set(final long offset, final byte source) {
        this.source.set(start + offset, source);
    }

    @Override
    public void set(final long offset, final long length, final byte[] source, final long sourceOffset) {
        this.source.set(start + offset, length, source, sourceOffset);
    }

    @Override
    public byte getByte(final long offset) {
        return source.getByte(start + offset);
    }

    @Override
    public void get(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        source.get(offset, length, destination, destinationOffset);
    }
}
