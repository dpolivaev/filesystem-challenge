package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.dpolivaev.katas.filesystem.internal.persistence.FileSystemFactory;
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

    public static TestFileSystem createThreadSafe(final int pagesInPool, final int poolPageSize) {
        return new TestFileSystem(pagesInPool, poolPageSize, true, new Random());
    }

    private TestFileSystem(final int pagesInPool, final int poolPageSize, final boolean threadSafe, final Random random) {
        this.testPages = new TestPages(pagesInPool, poolPageSize);
        this.pagePool = new PagePool(testPages, random);
        final PageEditor editor = new PageEditor(testPages.descriptorPage());
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        editor.write(FileSystemFactory.ROOT_UUID);
        final PagedFileSystem alternativeFileSystem;
        if (threadSafe) {
            this.fileSystem = PagedFileSystem.threadSafe(pagePool);
            alternativeFileSystem = PagedFileSystem.threadSafe(pagePool);
        } else {
            this.fileSystem = PagedFileSystem.singleThreaded(pagePool);
            alternativeFileSystem = PagedFileSystem.singleThreaded(pagePool);
        }
        this.root = fileSystem.root();
        this.alternativeRoot = alternativeFileSystem.root();
    }

    public long maximumSupportedFileSize() {
        return fileSystem.maximumSupportedFileSize();
    }
}
