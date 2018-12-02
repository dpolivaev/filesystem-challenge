package org.dpolivaev.katas.filesystem.domain.internal.memory;


import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class ArbitraryCompositePage extends CompositePage {
    public ArbitraryCompositePage(final IntFunction<Page> pages, final int pageCount) {
        super(pages, pageCount);
    }

    @Override
    public long size() {
        return IntStream.range(0, pageCount).mapToObj(this.pages).mapToLong(Page::size).sum();
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
