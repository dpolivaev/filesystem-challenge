package org.dpolivaev.katas.filesystem.domain.internal.memory;


import java.util.function.LongFunction;

public class SameSizeCompositePage extends CompositePage {
    private final long pageSize;

    public SameSizeCompositePage(final LongFunction<Page> pages, final long pageNumber, final long pageCount, final long pageSize) {
        super(pages, pageNumber, pageCount);
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
