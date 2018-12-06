package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.dpolivaev.katas.filesystem.internal.pool.ConcurrentPagePool;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.Random;

import static org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory.mockRandomWithSequenceFrom0;

public class TestFileSystem {
    final public TestPages testPages;

    final public PagePool pagePool;

    final public Directory root;

    final public Directory secondRoot;

    public static TestFileSystem create(final int pagesInPool, final int poolPageSize) {
        return new TestFileSystem(pagesInPool, poolPageSize, false);
    }

    public static TestFileSystem createConcurrent(final int pagesInPool, final int poolPageSize) {
        return new TestFileSystem(pagesInPool, poolPageSize, false);
    }

    private TestFileSystem(final int pagesInPool, final int poolPageSize, final boolean concurrent) {
        final Random random = mockRandomWithSequenceFrom0();
        this.testPages = new TestPages(pagesInPool, poolPageSize);
        this.pagePool = concurrent ? new ConcurrentPagePool(testPages, random)
                : new PagePool(testPages, random);
        this.root = new PagedFileSystem(pagePool).root();
        this.secondRoot = new PagedFileSystem(pagePool).root();
    }
}
