package org.dpolivaev.katas.filesystem.domain;

public interface File extends Element {
    long size();
    void truncate(long newSize);

    void write(long offset, int length, byte[] source, int sourceOffset);

    void read(long offset, int length, byte[] destination, int destinationOffset);
}
