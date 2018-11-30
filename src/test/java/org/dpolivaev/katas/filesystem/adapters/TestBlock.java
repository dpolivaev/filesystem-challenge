package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pair;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;

public class TestBlock implements DataBlock {

    private final int id;
    private final byte[] data;
    private final int start;
    private final int end;

    public TestBlock(int id, int size) {
        this(id, new byte[size], 0, size);
    }

    private TestBlock(int id, byte[] data, int start, int end) {
        this.id = id;
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    public long position() {
        return id;
    }

    @Override
    public long size() {
        return end - start;
    }

    @Override
    public void put(long offset, byte source) {
        int index = start + (int) offset;
        data[index] = source;
    }

    @Override
    public void put(long offset, long length, byte[] source, long sourceOffset) {
        int index = this.start + (int) offset;
        System.arraycopy(source, (int)sourceOffset, data, index, (int)length);
    }

    @Override
    public byte getByte(long offset) {
        int index = start + (int) offset;
        return data[index];
    }

    @Override
    public void get(long offset, long length, byte[] destination, long destinationOffset) {
        int index = (int) (start + offset);
        System.arraycopy(data, index, destination, (int)destinationOffset, (int)length);
    }

    @Override
    public Pair<DataBlock, DataBlock> split(long position) {
        int index = (int) position;
        DataBlock first = new TestBlock(this.id, data, this.start, this.start + index);
        DataBlock second = new TestBlock(this.id, data, this.start + index, this.end);
        return new Pair<>(first, second);
    }

    public byte[] copy(){
        byte[] copy = new byte[end - start];
        get(copy, start);
        return copy;
    }

    @Override
    public String toString() {
        return "TestBlock{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
