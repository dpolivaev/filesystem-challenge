package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Page {
    long size();

    void write(long offset, byte source);

    void write(long offset, int length, byte[] source, int sourceOffset);

    byte readByte(long offset);

    void read(long offset, int length, byte[] destination, int destinationOffset);

    default void erase() {
        erase(0, size());
    }

    void erase(long offset, long length);

    default Pair<Page, Page> split(final long position) {
        if (position < 0 || position > size())
            throw new IllegalArgumentException("Invalid position " + position);
        final Page first = new SubPage(this, 0, position);
        final Page second = new SubPage(this, position, size());
        return new Pair<>(first, second);
    }

    default Page subpage(final long from, final long length) {
        if (from < 0 || from > size())
            throw new IllegalArgumentException("Invalid from " + from);
        if (length < 0 || from + length > size())
            throw new IllegalArgumentException("Invalid length " + length);
        return new SubPage(this, from, from + length);
    }
}