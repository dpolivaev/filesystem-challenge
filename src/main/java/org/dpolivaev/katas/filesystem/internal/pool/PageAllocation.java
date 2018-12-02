package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Page;

public class PageAllocation {
    public final Page page;
    public final long pageNumber;

    public PageAllocation(final Page page, final long pageNumber) {
        this.page = page;
        this.pageNumber = pageNumber;
    }
}
