package org.dpolivaev.katas.filesystem.internal.persistence;

import org.dpolivaev.katas.filesystem.internal.pages.Page;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

class PersistentPage implements Page {
    static final int PAGE_SIZE = 1024;
    private static final byte[] ZEROS = new byte[PAGE_SIZE];
    private final MappedByteBuffer byteBuffer;
    private final long page;

    public PersistentPage(final FileChannel fileChannel, final long position) throws IOException {
        page = position;
        byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position * PAGE_SIZE, PAGE_SIZE);
    }

    @Override
    public long size() {
        return PAGE_SIZE;
    }

    @Override
    public void write(final long offset, final byte source) {
        byteBuffer.position((int) offset);
        byteBuffer.put(source);
        System.out.println("write " + page + "," + offset + "," + source + " in thread " + Thread.currentThread().getName());
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        byteBuffer.position((int) offset);
        byteBuffer.put(source, sourceOffset, length);
        System.out.println("written " + page + "," + offset + "," + length + Arrays.toString(Arrays.copyOfRange(source, sourceOffset, sourceOffset + length))
                + "in thread " + Thread.currentThread().getName());
    }

    @Override
    public byte readByte(final long offset) {
        final byte b = byteBuffer.get((int) offset);
        System.out.println("read " + page + "," + offset + "," + b + " in thread " + Thread.currentThread().getName());

        return b;
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        byteBuffer.position((int) offset);
        byteBuffer.get(destination, destinationOffset, length);
        System.out.println("read " + page + "," + offset + "," + length + Arrays.toString(Arrays.copyOfRange(destination, destinationOffset, destinationOffset + length))
                + "in thread " + Thread.currentThread().getName());
    }

    @Override
    public void erase(final long offset, final long length) {
        byteBuffer.position((int) offset);
        write(offset, (int) length, ZEROS, 0);
        System.out.println("erase " + page + "," + offset + "," + length + " in thread " + Thread.currentThread().getName());
    }
}
