package org.dpolivaev.katas.filesystem.domain.internal.memory;

public class PageAllocation {
    public final Page page;
    public final long pageNumber;

    public PageAllocation(final Page page, final long pageNumber) {
        this.page = page;
        this.pageNumber = pageNumber;
    }
}
