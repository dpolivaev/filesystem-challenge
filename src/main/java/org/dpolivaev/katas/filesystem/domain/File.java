package org.dpolivaev.katas.filesystem.domain;

public interface File extends Element {
    long size();
    void truncate(long newSize);

    void write(int offset, long length, byte[] source, long sourceOffset);

    void read(int offset, long length, byte[] destination, long destinationOffset);
}
