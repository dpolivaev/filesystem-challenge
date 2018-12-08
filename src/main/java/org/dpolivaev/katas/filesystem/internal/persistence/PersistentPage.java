package org.dpolivaev.katas.filesystem.internal.persistence;

import org.dpolivaev.katas.filesystem.internal.pages.Page;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

class PersistentPage implements Page {
    static final int PAGE_SIZE = 1024;
    private static final byte[] ZEROS = new byte[PAGE_SIZE];
    private final MappedByteBuffer byteBuffer;

    public PersistentPage(final FileChannel fileChannel, final long position) throws IOException {
        byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position, PAGE_SIZE);
    }

    @Override
    public long size() {
        return PAGE_SIZE;
    }

    @Override
    public void write(final long offset, final byte source) {
        byteBuffer.position((int) offset);
        byteBuffer.put(source);
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        byteBuffer.position((int) offset);
        byteBuffer.put(source, sourceOffset, length);
    }

    @Override
    public byte readByte(final long offset) {
        return byteBuffer.get((int) offset);
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        byteBuffer.position((int) offset);
        byteBuffer.get(destination, destinationOffset, length);
    }

    @Override
    public void erase(final long offset, final long length) {
        byteBuffer.position((int) offset);
        write(offset, (int) length, ZEROS, 0);
    }
}
