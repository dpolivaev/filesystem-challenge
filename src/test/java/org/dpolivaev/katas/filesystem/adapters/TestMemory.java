package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Memory;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pair;

import java.util.Vector;

public class TestMemory implements Memory {

    private final Vector<TestBlock> blocks;
    private final int blockSize;

    public TestMemory(int blockCount, int blockSize) {
        this.blocks = new Vector<>(blockCount);
        blocks.setSize(blockCount);
        this.blockSize = blockSize;
    }


    @Override
    public long blockCount() {
        return blocks.size();
    }

    @Override
    public DataBlock at(long position) {
        int index = (int) position;
        TestBlock block = blocks.elementAt(index);
        if(block == null) {
            block = new TestBlock(index, blockSize);
            blocks.setElementAt(block, index);
        }
        return block;
    }
}

