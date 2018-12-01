package org.dpolivaev.katas.filesystem.domain.internal.memory;


import java.util.function.LongFunction;

abstract public class CompositePage implements Page {
    protected final LongFunction<Page> pages;

    protected int currentPageIndex;
    protected long currentPageOffset;
    private final long pageNumber;
    protected final long pageCount;

    public CompositePage(final LongFunction<Page> pages, final long pageNumber, final long pageCount) {
        this.pageNumber = pageNumber;
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
    public void write(long offset, long length, final byte[] source, long sourceOffset) {
        findPage(offset);
        final long availableLengthOnPage = currentPage().size() - offset - currentPageOffset;
        while (availableLengthOnPage < length) {
            currentPage().write(offset, availableLengthOnPage, source, sourceOffset);
            offset = 0;
            sourceOffset += availableLengthOnPage;
            length -= availableLengthOnPage;
            currentPageIndex++;
        }
        currentPage().write(offset, length, source, sourceOffset);
    }

    @Override
    public byte readByte(final long offset) {
        findPage(offset);
        return currentPage().readByte(offset - currentPageOffset);
    }

    @Override
    public void read(long offset, long length, final byte[] destination, long destinationOffset) {
        findPage(offset);
        final long availableLengthOnPage = currentPage().size() - offset - currentPageOffset;
        while (availableLengthOnPage < length) {
            currentPage().write(offset, availableLengthOnPage, destination, destinationOffset);
            offset = 0;
            destinationOffset += availableLengthOnPage;
            length -= availableLengthOnPage;
            currentPageIndex++;
        }
        currentPage().write(offset, length, destination, destinationOffset);
    }
}
