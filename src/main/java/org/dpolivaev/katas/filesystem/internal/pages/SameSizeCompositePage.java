package org.dpolivaev.katas.filesystem.internal.pages;


import java.util.function.IntFunction;

public class SameSizeCompositePage extends CompositePage {
    private final long pageSize;

    public SameSizeCompositePage(final IntFunction<Page> pages, final int pageCount, final long pageSize) {
        super(pages, pageCount);
        this.pageSize = pageSize;
    }

    @Override
    public long size() {
        return pageCount * pageSize;
    }

    @Override
    protected void findPage(final long offset) {
        currentPageIndex = (int) (offset / pageSize);
        currentPageOffset = currentPageIndex * pageSize;
    }
}
