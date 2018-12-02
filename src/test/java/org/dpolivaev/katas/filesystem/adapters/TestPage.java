package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;

import java.util.Arrays;
import java.util.stream.IntStream;

public class TestPage implements Page {

    private final byte[] data;

    public TestPage(final int size) {
        this.data = new byte[size];
    }

    public TestPage filledAscendingFrom(final int from) {
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)((from + i) & 0xff);
        return this;
    }

    @Override
    public long size() {
        return data.length;
    }

    @Override
    public void write(final long offset, final byte source) {
        if (offset >= data.length)
            throw new IllegalArgumentException();
        data[(int) offset] = source;
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        System.arraycopy(source, sourceOffset, data, (int) offset, length);
    }

    @Override
    public byte readByte(final long offset) {
        return data[(int) offset];
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        System.arraycopy(data, (int) (offset), destination, destinationOffset, length);
    }

    @Override
    public void erase(final long offset, final long length) {
        Arrays.fill(data, (int) offset, (int) (offset + length), (byte) 0);
    }

    @Override
    public String toString() {
        return "TestPage{"
                + Arrays.toString(data) +
                '}';
    }

    public boolean isEmpty() {
        return IntStream.range(0, data.length).parallel().allMatch(i -> data[i] == 0);
    }
}
