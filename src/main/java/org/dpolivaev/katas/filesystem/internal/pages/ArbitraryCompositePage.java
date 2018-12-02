package org.dpolivaev.katas.filesystem.internal.pages;


import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class ArbitraryCompositePage extends CompositePage {
    private final long size;

    public ArbitraryCompositePage(final IntFunction<Page> pages, final int pageCount) {
        super(pages, pageCount);
        size = IntStream.range(0, pageCount).mapToObj(this.pages).mapToLong(Page::size).sum();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    protected void findPage(final long offset) {
        if (offset < currentPageOffset) {
            currentPageIndex = 0;
            currentPageOffset = 0;
        }
        while (currentPageIndex < pageCount) {
            final long pageSize = currentPage().size();
            if (offset < currentPageOffset + pageSize) {
                return;
            } else {
                currentPageOffset += pageSize;
                currentPageIndex++;
            }
        }
        throw new IllegalArgumentException("Offset too high");
    }
}
