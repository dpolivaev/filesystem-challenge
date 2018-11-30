package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface DataBlock extends Splittable<DataBlock>{
    long position();
    long size();
    void put(long offset, byte source);

    default void put(long offset, long source){
        putNumber(offset, source, Long.BYTES);
    }
    default void put(long offset, int source){
        putNumber(offset, Integer.toUnsignedLong(source), Integer.BYTES);
    }

    default void putNumber(long offset, long source, int byteCount) {
        for (int i = byteCount - 1; i >= 0; i--) {
            put(offset + i, (byte)(source & 0xFF));
            source >>= 8;
        }
    }

    default void put(byte[] source, long sourceOffset) {
        put(0, size(), source, sourceOffset);
    }
    void put(long offset, long length, byte[] source, long sourceOffset);
    byte getByte(long offset);
    default int getInt(long offset){
        return (int) getNumber(offset, 4);
    }
    default long getLong(long offset){
        return getNumber(offset, 8);
    }
    default long getNumber(long offset, int byteCount) {
        long result = 0;
        for (int i = 0; i < byteCount; i++) {
            result <<= 8;
            result |= (getByte(offset) & 0xFF);
            offset++;
        }
        return result;
    }
    void get(long offset, long length, byte[] destination, long destinationOffset);
    default void get(byte[] destination, long sourceOffset) {
        get(0, size(), destination, sourceOffset);
    }
}
