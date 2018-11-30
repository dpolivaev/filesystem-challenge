package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Memory;

import java.util.Vector;

public class TestMemory implements Memory {

    private final Vector<TestBlock> blocks;
    private final int memorySize;

    public TestMemory(final int blockCount, final int memorySize) {
        this.blocks = new Vector<>(blockCount);
        blocks.setSize(blockCount);
        this.memorySize = memorySize;
    }

    @Override
    public long size() {
        return blocks.size();
    }

    @Override
    public DataBlock at(final long position) {
        final int index = (int) position;
        TestBlock block = blocks.elementAt(index);
        if(block == null) {
            block = new TestBlock(index, memorySize);
            blocks.setElementAt(block, index);
        }
        return block;
    }
}

