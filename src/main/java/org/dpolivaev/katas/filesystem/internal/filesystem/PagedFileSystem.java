package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pool.ConcurrentPagePool;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.concurrent.locks.ReentrantLock;

public class PagedFileSystem implements FileSystem {

    public static final int ROOT_PAGE_NUMBER = 1;
    private final PagedDirectory rootDirectory;
    private final PagePool pagePool;
    private final long maximumSupportedFileSize;

    public PagedFileSystem(final PagePool pagePool) {
        this.pagePool = pagePool;
        final Page rootDescriptor = pagePool.isAllocated((long) ROOT_PAGE_NUMBER) ? pagePool.pageAt(1) : pagePool.allocate(1);
        rootDirectory = new PagedDirectory(pagePool, rootDescriptor, null);
        maximumSupportedFileSize = new FilePage(pagePool, rootDescriptor).size();
    }

    public PagedFileSystem(final ConcurrentPagePool pagePool) {
        this.pagePool = pagePool;
        final Page rootDescriptor = pagePool.isAllocated((long) ROOT_PAGE_NUMBER) ? pagePool.pageAt(1) : pagePool.allocate(1);
        rootDirectory = new ConcurrentPagedDirectory(pagePool, rootDescriptor, null, new ReentrantLock());
        maximumSupportedFileSize = new FilePage(pagePool, rootDescriptor).size();
    }

    @Override
    public PagedDirectory root() {
        return rootDirectory;
    }

    @Override
    public long maximumSupportedFileSize() {
        return maximumSupportedFileSize;
    }

    @Override
    public void close() {
        pagePool.close();
    }
}
