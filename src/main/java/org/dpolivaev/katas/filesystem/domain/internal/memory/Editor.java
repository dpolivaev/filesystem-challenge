package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.nio.charset.StandardCharsets;

public class Editor {
    private Page page = null;

    private long position = 0;

    public void setPage(final Page page) {
        this.page = page;
        this.position = 0;
    }

    public void setPosition(final long position) {
        if (position < 0)
            throw new IllegalArgumentException("Invalid position " + position);
        this.position = position;
    }

    private void ensureValidPosition() {
        if (position >= page.size())
            throw new IllegalArgumentException("Invalid position " + position);
    }

    private void ensureValidArrayRange(final byte[] source, final long offset, final long length) {
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid position " + offset);
        } else if (offset + length > source.length) {
            throw new IllegalArgumentException("Invalid length " + length);
        }
    }

    private void ensureValidLength(final long length) {
        if (length < 0 || position + length > page.size())
            throw new IllegalArgumentException("Invalid length " + length);
    }

    public void write(final byte source) {
        ensureValidPosition();
        page.write(position, source);
        position++;
    }

    public void write(final byte[] source, final long sourceOffset, final long length) {
        ensureValidPosition();
        ensureValidLength(length);
        ensureValidArrayRange(source, sourceOffset, length);
        page.write(position, length, source, sourceOffset);
        position += length;
    }

    public byte readByte() {
        ensureValidPosition();
        final byte value = page.readByte(position);
        position++;
        return value;
    }

    public void read(final byte[] destination, final long destinationOffset, final long length) {
        ensureValidPosition();
        ensureValidLength(length);
        ensureValidArrayRange(destination, destinationOffset, length);
        page.read(position, length, destination, destinationOffset);
        position += length;
    }

    public void write(final long source) {
        writeNumber(source, Long.BYTES);
    }

    public void write(final int source) {
        writeNumber(Integer.toUnsignedLong(source), Integer.BYTES);
    }

    public void writeNumber(long source, final int byteCount) {
        final long offset = this.position;
        for (int i = byteCount - 1; i >= 0; i--) {
            this.position = offset + i;
            write((byte) (source & 0xFF));
            source >>= 8;
        }
        this.position = offset + byteCount;
    }

    public void write(final byte[] source) {
        write(source, 0, source.length);
    }

    public void write(final String source) {
        final byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        write(bytes.length);
        write(bytes);
    }

    public int readInt() {
        return (int) readNumber(4);
    }

    public long readLong() {
        return readNumber(8);
    }

    public long readNumber(final int byteCount) {
        long result = 0;
        for (int i = 0; i < byteCount; i++) {
            result <<= 8;
            result |= (readByte() & 0xFF);
        }
        return result;
    }

    public String readString() {
        final int length = readInt();
        final byte[] buffer = new byte[length];
        read(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public void read(final byte[] destination) {
        read(destination, 0, destination.length);
    }
}
