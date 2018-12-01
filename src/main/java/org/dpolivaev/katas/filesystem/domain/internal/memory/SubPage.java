package org.dpolivaev.katas.filesystem.domain.internal.memory;

class SubPage implements Page {
    private final Page source;
    private final long start;
    private final long end;

    SubPage(final Page source, final long start, final long end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public long size() {
        return end - start;
    }

    @Override
    public Pair<Page, Page> split(final long position) {
        if (position < 0 || position > size())
            throw new IllegalArgumentException("Invalid position " + position);
        final long splitPosition = start + position;
        final Page first = new SubPage(source, start, splitPosition);
        final Page second = new SubPage(source, splitPosition, this.end);
        return new Pair<>(first, second);
    }

    @Override
    public void write(final long offset, final byte source) {
        this.source.write(start + offset, source);
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        this.source.write(start + offset, length, source, sourceOffset);
    }

    @Override
    public byte readByte(final long offset) {
        return source.readByte(start + offset);
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        source.read(offset, length, destination, destinationOffset);
    }
}
