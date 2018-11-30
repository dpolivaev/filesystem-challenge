package org.dpolivaev.katas.filesystem.domain;

public interface File extends Element {
    int size();
    void truncate(long newSize);
    void write(int offset, int length, byte[] source, long sourceOffset);
    void read(int offset, int length, byte[] destination, long destinationOffset);
}
