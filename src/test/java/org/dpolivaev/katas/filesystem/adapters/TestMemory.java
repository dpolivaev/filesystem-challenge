package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Memory;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pair;

import java.util.Vector;

public class TestMemory implements Memory {

    private final Vector<TestBlock> blocks;
    private final int blockSize;
    private final int start;
    private final int end;

    public TestMemory(int blockCount, int blockSize) {
        this(new Vector<>(blockCount), blockSize, 0, blockCount);
        blocks.setSize(blockCount);
    }

    private TestMemory(Vector<TestBlock> blocks, int blockSize, int start, int end) {
        this.blocks = blocks;
        this.blockSize = blockSize;
        this.start = start;
        this.end = end;
    }


    @Override
    public long blockCount() {
        return blocks.size();
    }

    @Override
    public DataBlock at(long position) {
        int index = (int) position;
        TestBlock block = blocks.elementAt(index);
        if(block == null)
            block = new TestBlock(index, blockSize);
        return block;
    }

    @Override
    public Pair<Memory, Memory> split(long position) {
        int index = (int) position;
        Memory first = new TestMemory(blocks, blockSize, start, index);
        Memory second = new TestMemory(blocks, blockSize, index, end);
        return new Pair<>(first, second);
    }
}

