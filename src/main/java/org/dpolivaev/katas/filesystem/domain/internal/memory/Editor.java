package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.nio.charset.StandardCharsets;

public class Editor {
    public void write(final byte source) {
        page.write(offset, source);
        offset++;
    }

    public void setPage(final Page page) {
        this.page = page;
        this.offset = 0;
    }

    public void setPosition(final long position) {
        this.offset = position;
    }

    public void write(final long length, final byte[] source, final long sourceOffset) {
        page.write(offset, length, source, sourceOffset);
        offset += length;
    }

    public byte readByte() {
        final byte value = page.readByte(offset);
        offset++;
        return value;
    }

    public void read(final long length, final byte[] destination, final long destinationOffset) {
        page.read(offset, length, destination, destinationOffset);
    }

    private Page page;

    private long offset = 0;

    public Editor() {
        this.page = null;
        this.offset = 0;
    }


    public void write(final long source) {
        writeNumber(source, Long.BYTES);
    }

    public void write(final int source) {
        writeNumber(Integer.toUnsignedLong(source), Integer.BYTES);
    }

    public void writeNumber(long source, final int byteCount) {
        final long offset = this.offset;
        for (int i = byteCount - 1; i >= 0; i--) {
            this.offset = offset + i;
            write((byte) (source & 0xFF));
            source >>= 8;
        }
        this.offset = offset + byteCount;
    }

    public void write(final byte[] source) {
        write(source.length, source, 0);
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
        read(destination.length, destination, 0);
    }


}
