package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.dpolivaev.katas.filesystem.internal.persistence.FileSystemFactory;
import org.dpolivaev.katas.filesystem.internal.pool.ConcurrentPagePool;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.Random;

import static org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory.mockRandomWithSequenceTo;

public class TestFileSystem {
    public final TestPages testPages;

    public final PagePool pagePool;

    public final PagedFileSystem fileSystem;

    public final PagedDirectory root;

    public final PagedDirectory alternativeRoot;

    public static TestFileSystem create(final int pagesInPool, final int poolPageSize) {
        return new TestFileSystem(pagesInPool, poolPageSize, false, mockRandomWithSequenceTo(pagesInPool * 98 / 100));
    }

    public static TestFileSystem createConcurrent(final int pagesInPool, final int poolPageSize) {
        return new TestFileSystem(pagesInPool, poolPageSize, true, new Random());
    }

    private TestFileSystem(final int pagesInPool, final int poolPageSize, final boolean concurrent, final Random random) {
        this.testPages = new TestPages(pagesInPool, poolPageSize);
        this.pagePool = concurrent ? new ConcurrentPagePool(testPages, random)
                : new PagePool(testPages, random);
        final Page rootDescriptor = pagePool.allocate(1);
        final PageEditor editor = new PageEditor(rootDescriptor);
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        editor.write(FileSystemFactory.ROOT_UUID);
        final PagedFileSystem alternativeFileSystem;
        if (concurrent) {
            this.fileSystem = new PagedFileSystem((ConcurrentPagePool) pagePool);
            alternativeFileSystem = new PagedFileSystem((ConcurrentPagePool) pagePool);
        } else {
            this.fileSystem = new PagedFileSystem(pagePool);
            alternativeFileSystem = new PagedFileSystem(pagePool);
        }
        this.root = fileSystem.root();
        this.alternativeRoot = alternativeFileSystem.root();
    }

    public long maximumSupportedFileSize() {
        return fileSystem.maximumSupportedFileSize();
    }
}
