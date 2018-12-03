package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

public class PagedFileSystem implements FileSystem {

    public static final int ROOT_PAGE_NUMBER = 1;
    private final PagedDirectory rootDirectory;
    private final PagePool pagePool;

    public PagedFileSystem(final PagePool pagePool) {
        this.pagePool = pagePool;
        final Page rootDescriptor = pagePool.containsPage(ROOT_PAGE_NUMBER) ? pagePool.at(1) : pagePool.allocate(1);
        rootDirectory = new PagedDirectory(pagePool, rootDescriptor, null);

    }

    @Override
    public Directory root() {
        return rootDirectory;
    }

    @Override
    public void close() {
        pagePool.close();
    }
}
