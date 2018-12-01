package org.dpolivaev.katas.filesystem.domain.internal.memory;


import java.util.function.LongFunction;
import java.util.stream.LongStream;

public class ArbitraryCompositePage extends CompositePage {
    public ArbitraryCompositePage(final LongFunction<Page> pages, final long pageCount) {
        super(pages, pageCount);
    }

    @Override
    public long size() {
        return LongStream.range(0, pageCount).mapToObj(this.pages).mapToLong(Page::size).sum();
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
