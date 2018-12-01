package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;

import java.util.Arrays;

public class TestBlock implements Page {

    private final int id;
    private final byte[] data;

    public TestBlock(final int id, final int size) {
        this.id = id;
        this.data = new byte[size];
    }

    public TestBlock filledAscendingFrom(final int from) {
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)((from + i) & 0xff);
        return this;
    }

    @Override
    public long position() {
        return id;
    }

    @Override
    public long size() {
        return data.length;
    }

    @Override
    public void write(final long offset, final byte source) {
        data[(int) offset] = source;
    }

    @Override
    public void write(final long offset, final long length, final byte[] source, final long sourceOffset) {
        System.arraycopy(source, (int)sourceOffset, data, (int) offset, (int)length);
    }

    @Override
    public byte readByte(final long offset) {
        return data[(int) offset];
    }

    @Override
    public void read(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        System.arraycopy(data, (int) (offset), destination, (int)destinationOffset, (int)length);
    }

    @Override
    public String toString() {
        return "TestBlock{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
