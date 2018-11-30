package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface DataBlock {
    int size();
    void put(long offset, int length, byte source);
    void put(long offset, int length, int source);
    void put(long offset, int length, byte[] source, long sourceOffset);
    default void put(byte[] source, long sourceOffset) {
        put(0, size(), source, sourceOffset);
    }
    byte getByte(long offset);
    int getInt(long offset);
    void get(long offset, int length, byte[] destination, long destinationOffset);
    default void get(byte[] source, long sourceOffset) {
        get(0, size(), source, sourceOffset);
    }
}
