package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pages;

import java.util.Vector;

public class TestPages implements Pages {

    private final Vector<TestPage> pages;
    private final int pageSize;

    public TestPages(final int pageCount, final int pageSize) {
        this.pages = new Vector<>(pageCount);
        pages.setSize(pageCount);
        this.pageSize = pageSize;
    }

    @Override
    public long size() {
        return pages.size();
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public Page at(final long position) {
        final int index = (int) position;
        TestPage page = pages.elementAt(index);
        if (page == null) {
            page = new TestPage(index, pageSize);
            pages.setElementAt(page, index);
        }
        return page;
    }

    @Override
    public String toString() {
        return "TestPages{" +
                "size=" + size() +
                "pageSize=" + pageSize +
                ", pages=" + pages +
                '}';
    }
}

