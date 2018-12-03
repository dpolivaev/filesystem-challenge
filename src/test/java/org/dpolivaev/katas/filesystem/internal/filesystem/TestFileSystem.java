package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.Random;

import static org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory.mockRandomWithSequence_0toN;

public class TestFileSystem {
    final public TestPages testPages;

    final public PagePool pagePool;

    final public Directory root;

    final public Directory secondRoot;

    public TestFileSystem() {
        this(1024, 1024);
    }

    public TestFileSystem(final int pagesInPool, final int poolPageSize) {
        final Random random = mockRandomWithSequence_0toN();
        testPages = new TestPages(pagesInPool, poolPageSize);
        pagePool = new PagePool(testPages, random);
        root = new PagedFileSystem(pagePool).root();
        secondRoot = new PagedFileSystem(pagePool).root();
    }
}
