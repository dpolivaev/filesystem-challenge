package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.FileSystem;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;

public class InMemoryFileSystem implements FileSystem {

    public static final int ROOT_PAGE_NUMBER = 1;
    private final InMemoryDirectory rootDirectory;
    private final PagePool pagePool;

    public InMemoryFileSystem(final PagePool pagePool) {
        this.pagePool = pagePool;
        final Page rootDescriptor = pagePool.containsPage(ROOT_PAGE_NUMBER) ? pagePool.at(1) : pagePool.allocate(1);
        rootDirectory = new InMemoryDirectory(pagePool, rootDescriptor, null);

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
