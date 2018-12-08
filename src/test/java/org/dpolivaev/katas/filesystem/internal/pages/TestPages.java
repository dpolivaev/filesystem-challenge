package org.dpolivaev.katas.filesystem.internal.pages;

import org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure;

import java.util.Vector;

public class TestPages implements Pages {

    private final Vector<TestPage> pages;
    private final int pageSize;
    private final Page descriptorPage;

    public TestPages(final int pageCount, final int pageSize) {
        this.pages = new Vector<>(pageCount);
        pages.setSize(pageCount);
        this.pageSize = pageSize;
        descriptorPage = new TestPage(FileDescriptorStructure.DATA_POSITION);
    }

    @Override
    public long pageCount() {
        return pages.size();
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public Page descriptorPage() {
        return descriptorPage;
    }

    @Override
    public Page at(final long position) {
        final int index = (int) position;
        TestPage page = pages.elementAt(index);
        if (page == null) {
            page = new TestPage(pageSize);
            pages.setElementAt(page, index);
        }
        return page;
    }

    @Override
    public String toString() {
        return "TestPages{" +
                "size=" + pageCount() +
                "pageSize=" + pageSize +
                ", pages=" + pages +
                '}';
    }

    public boolean areEmpty() {
        return pages.stream().filter(page -> page != null).allMatch(TestPage::isEmpty);
    }
}

