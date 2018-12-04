package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.Page;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class PageEditor {
    private Page page = null;
    private long position = 0;

    public void setPage(final Page page) {
        this.page = page;
        this.position = 0;
    }

    public void setPosition(final long position) {
        if (position < 0) {
            throw new IllegalArgumentException("Invalid position " + position);
        }
        this.position = position;
    }

    public void on(final Page page, final long position, final Runnable runnable) {
        final Page oldPage = this.page;
        final long oldPosition = this.position;
        this.page = page;
        this.position = position;
        try {
            runnable.run();
        } finally {
            this.page = oldPage;
            this.position = oldPosition;
        }
    }

    public long on(final Page page, final long position, final LongSupplier supplier) {
        final Page oldPage = this.page;
        final long oldPosition = this.position;
        this.page = page;
        this.position = position;
        try {
            return supplier.getAsLong();
        } finally {
            this.page = oldPage;
            this.position = oldPosition;
        }
    }

    public <T> T on(final Page page, final long position, final Supplier<T> supplier) {
        final Page oldPage = this.page;
        final long oldPosition = this.position;
        this.page = page;
        this.position = position;
        try {
            return supplier.get();
        } finally {
            this.page = oldPage;
            this.position = oldPosition;
        }
    }

    public long getPosition() {
        return position;
    }

    private void ensureValidPosition() {
        if (position >= page.size()) {
            throw new IllegalArgumentException("Invalid position " + position + " should be less than " + page.size());
        }
    }

    private static void ensureValidArrayRange(final byte[] source, final long offset,
            final long length) {
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid position " + offset);
        } else if (offset + length > source.length) {
            throw new IllegalArgumentException("Invalid length " + length);
        }
    }

    private void ensureValidLength(final long length) {
        if (length < 0 || position + length > page.size()) {
            throw new IllegalArgumentException("Invalid length " + length);
        }
    }

    public void write(final byte source) {
        ensureValidPosition();
        page.write(position, source);
        position++;
    }

    public void write(final byte[] source, final int sourceOffset, final int length) {
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

    public void read(final byte[] destination, final int destinationOffset, final int length) {
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

    private void writeNumber(long value, final int byteCount) {
        final byte[] buffer = {0, 0, 0, 0, 0, 0, 0, 0};
        assert byteCount <= buffer.length;
        for (int i = byteCount - 1; i >= 0; i--) {
            buffer[i] = (byte) (value & 0xFF);
            value >>= Byte.SIZE;
        }
        write(buffer, 0, byteCount);
    }

    public void write(final byte[] source) {
        write(source, 0, source.length);
    }

    public void write(final String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        write(bytes.length);
        write(bytes);
    }


    public static int requiredLength(final String value) {
        return value.getBytes(StandardCharsets.UTF_8).length + Integer.BYTES;
    }

    public int readInt() {
        return (int) readNumber(Integer.BYTES);
    }

    public long readLong() {
        return readNumber(Long.BYTES);
    }

    private long readNumber(final int byteCount) {
        final byte[] buffer = {0, 0, 0, 0, 0, 0, 0, 0};
        assert byteCount <= buffer.length;
        read(buffer, 0, byteCount);
        long result = 0;
        for (int i = 0; i < byteCount; i++) {
            result <<= Byte.SIZE;
            result |= (buffer[i] & 0xFF);
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

    public UUID readUUID() {
        final long mostSignificantBits = readLong();
        final long leastSignificantBits = readLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    public void write(final UUID uuid) {
        write(uuid.getMostSignificantBits());
        write(uuid.getLeastSignificantBits());
    }
}
