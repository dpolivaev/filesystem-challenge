package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pair;

import java.util.Arrays;

public class TestBlock implements DataBlock {

    private final int id;
    private final byte[] data;

    public TestBlock(int id, int size) {
        this.id = id;
        this.data = new byte[size];
    }

    public TestBlock filledAscendingFrom(int from){
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
    public void put(long offset, byte source) {
        data[(int) offset] = source;
    }

    @Override
    public void put(long offset, long length, byte[] source, long sourceOffset) {
        System.arraycopy(source, (int)sourceOffset, data, (int) offset, (int)length);
    }

    @Override
    public byte getByte(long offset) {
        return data[(int) offset];
    }

    @Override
    public void get(long offset, long length, byte[] destination, long destinationOffset) {
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
