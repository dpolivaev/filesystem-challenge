package org.dpolivaev.katas.filesystem.internal.pages;


import java.util.function.IntFunction;

abstract public class CompositePage implements Page {
    protected final IntFunction<Page> pages;

    protected int currentPageIndex;
    protected long currentPageOffset;
    protected final int pageCount;

    public CompositePage(final IntFunction<Page> pages, final int pageCount) {
        this.pageCount = pageCount;
        if (pageCount <= 0)
            throw new IllegalArgumentException("No pages");
        this.pages = pages;
    }

    @Override
    abstract public long size();

    @Override
    public void write(final long offset, final byte source) {
        findPage(offset);
        currentPage().write(offset - currentPageOffset, source);
    }

    protected Page currentPage() {
        return pages.apply(currentPageIndex);
    }

    abstract protected void findPage(final long offset);

    @Override
    public void write(long offset, int length, final byte[] source, int sourceOffset) {
        findPage(offset);
        long availableLengthOnPage = currentPage().size() - offset;
        while (availableLengthOnPage < length) {
            currentPage().write(offset, (int) availableLengthOnPage, source, sourceOffset);
            sourceOffset += availableLengthOnPage;
            length -= availableLengthOnPage;
            currentPageIndex++;
            currentPageOffset += availableLengthOnPage + offset;
            availableLengthOnPage = currentPage().size();
            offset = 0;
        }
        currentPage().write(offset, length, source, sourceOffset);
    }

    @Override
    public byte readByte(final long offset) {
        findPage(offset);
        return currentPage().readByte(offset - currentPageOffset);
    }

    @Override
    public void read(long offset, int length, final byte[] destination, int destinationOffset) {
        findPage(offset);
        long availableLengthOnPage = currentPage().size() - offset;
        while (availableLengthOnPage < length) {
            currentPage().read(offset, (int) availableLengthOnPage, destination, destinationOffset);
            destinationOffset += availableLengthOnPage;
            length -= availableLengthOnPage;
            currentPageIndex++;
            currentPageOffset += availableLengthOnPage + offset;
            availableLengthOnPage = currentPage().size();
            offset = 0;
        }
        currentPage().read(offset, length, destination, destinationOffset);
    }

    @Override
    public void erase(long offset, long length) {
        findPage(offset);
        final long availableLengthOnPage = currentPage().size() - offset - currentPageOffset;
        while (availableLengthOnPage < length) {
            currentPage().erase(offset, availableLengthOnPage);
            offset = 0;
            length -= availableLengthOnPage;
            currentPageIndex++;
        }
        currentPage().erase(offset, length);
    }
}
