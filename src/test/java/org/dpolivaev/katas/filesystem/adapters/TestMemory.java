package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Memory;

import java.util.Vector;

public class TestMemory implements Memory {

    private final Vector<TestPage> pages;
    private final int memorySize;

    public TestMemory(final int pageCount, final int memorySize) {
        this.pages = new Vector<>(pageCount);
        pages.setSize(pageCount);
        this.memorySize = memorySize;
    }

    @Override
    public long size() {
        return pages.size();
    }

    @Override
    public Page at(final long position) {
        final int index = (int) position;
        TestPage page = pages.elementAt(index);
        if(page == null) {
            page = new TestPage(index, memorySize);
            pages.setElementAt(page, index);
        }
        return page;
    }
}

