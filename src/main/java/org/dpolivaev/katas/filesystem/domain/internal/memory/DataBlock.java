package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.nio.charset.StandardCharsets;

public interface DataBlock extends Splittable<DataBlock>{
    long position();

    long size();

    void put(long offset, byte source);

    default void put(final long offset, final long source) {
        putNumber(offset, source, Long.BYTES);
    }

    default void put(final long offset, final int source) {
        putNumber(offset, Integer.toUnsignedLong(source), Integer.BYTES);
    }

    default void putNumber(final long offset, long source, final int byteCount) {
        for (int i = byteCount - 1; i >= 0; i--) {
            put(offset + i, (byte)(source & 0xFF));
            source >>= 8;
        }
    }

    default void put(final long offset, final byte[] source) {
        put(offset, source.length, source, 0);
    }

    default void put(final long offset, final String source) {
        final byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        put(offset, bytes.length);
        put(offset + Integer.BYTES, bytes);
    }

    void put(long offset, long length, byte[] source, long sourceOffset);

    byte getByte(long offset);

    default int getInt(final long offset) {
        return (int) getNumber(offset, 4);
    }

    default long getLong(final long offset) {
        return getNumber(offset, 8);
    }

    default long getNumber(long offset, final int byteCount) {
        long result = 0;
        for (int i = 0; i < byteCount; i++) {
            result <<= 8;
            result |= (getByte(offset) & 0xFF);
            offset++;
        }
        return result;
    }

    default String getString(final int offset) {
        final int length = getInt(offset);
        final byte[] buffer = new byte[length];
        get(offset + Integer.BYTES, buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }


    void get(long offset, long length, byte[] destination, long destinationOffset);

    default void get(final long offset, final byte[] destination) {
        get(offset, destination.length, destination, 0);
    }

    @Override
    default Pair<DataBlock, DataBlock> split(final long position) {
        final DataBlock first = new SplitBlock(this, 0, position);
        final DataBlock second = new SplitBlock(this, position, size());
        return new Pair<>(first, second);
    }

    default DataBlock safe() {
        return new SafeBlock(this);
    }
}