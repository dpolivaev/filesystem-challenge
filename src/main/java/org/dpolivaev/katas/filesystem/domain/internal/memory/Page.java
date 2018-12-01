package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Page {
    long position();

    long size();

    void write(long offset, byte source);

    void write(long offset, long length, byte[] source, long sourceOffset);

    byte readByte(long offset);

    void read(long offset, long length, byte[] destination, long destinationOffset);

    default Pair<Page, Page> split(final long position) {
        final Page first = new SubPage(this, 0, position);
        final Page second = new SubPage(this, position, size());
        return new Pair<>(first, second);
    }

    default Page safe() {
        return new SafePage(this);
    }
}