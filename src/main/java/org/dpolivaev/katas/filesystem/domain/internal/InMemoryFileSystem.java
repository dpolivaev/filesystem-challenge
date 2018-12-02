package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.FileSystem;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;

class InMemoryFileSystem implements FileSystem {

    public static final int ROOT_PAGE_NUMBER = 1;
    private final InMemoryDirectory rootDirectory;

    public InMemoryFileSystem(final PagePool pagePool) {
        final Page rootDescriptor = pagePool.containsPage(ROOT_PAGE_NUMBER) ? pagePool.at(1) : pagePool.allocate(1);
        rootDirectory = new InMemoryDirectory(pagePool, rootDescriptor, null);

    }

    @Override
    public Directory root() {
        return rootDirectory;
    }
}
