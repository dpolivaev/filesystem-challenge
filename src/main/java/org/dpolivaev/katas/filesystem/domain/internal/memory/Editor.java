package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.nio.charset.StandardCharsets;

public class Editor {
    public void write(final long offset, final byte source) {
        page.write(offset, source);
    }

    public void write(final long offset, final long length, final byte[] source, final long sourceOffset) {
        page.write(offset, length, source, sourceOffset);
    }

    public byte readByte(final long offset) {
        return page.readByte(offset);
    }

    public void read(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        page.read(offset, length, destination, destinationOffset);
    }

    private final Page page;

    public Editor(final Page page) {
        this.page = page;
    }

    public void write(final long offset, final long source) {
        writeNumber(offset, source, Long.BYTES);
    }

    public void write(final long offset, final int source) {
        writeNumber(offset, Integer.toUnsignedLong(source), Integer.BYTES);
    }

    public void writeNumber(final long offset, long source, final int byteCount) {
        for (int i = byteCount - 1; i >= 0; i--) {
            write(offset + i, (byte) (source & 0xFF));
            source >>= 8;
        }
    }

    public void write(final long offset, final byte[] source) {
        write(offset, source.length, source, 0);
    }

    public void write(final long offset, final String source) {
        final byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        write(offset, bytes.length);
        write(offset + Integer.BYTES, bytes);
    }

    public int readInt(final long offset) {
        return (int) readNumber(offset, 4);
    }

    public long readLong(final long offset) {
        return readNumber(offset, 8);
    }

    public long readNumber(long offset, final int byteCount) {
        long result = 0;
        for (int i = 0; i < byteCount; i++) {
            result <<= 8;
            result |= (readByte(offset) & 0xFF);
            offset++;
        }
        return result;
    }

    public String readString(final int offset) {
        final int length = readInt(offset);
        final byte[] buffer = new byte[length];
        read(offset + Integer.BYTES, buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public void read(final long offset, final byte[] destination) {
        read(offset, destination.length, destination, 0);
    }


}
