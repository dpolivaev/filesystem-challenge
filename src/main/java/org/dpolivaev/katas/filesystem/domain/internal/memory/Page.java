package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Page {
    long size();

    void write(long offset, byte source);

    void write(long offset, int length, byte[] source, int sourceOffset);

    byte readByte(long offset);

    void read(long offset, int length, byte[] destination, int destinationOffset);

    default Pair<Page, Page> split(final long position) {
        if (position < 0 || position > size())
            throw new IllegalArgumentException("Invalid position " + position);
        final Page first = new SubPage(this, 0, position);
        final Page second = new SubPage(this, position, size());
        return new Pair<>(first, second);
    }
}