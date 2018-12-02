package org.dpolivaev.katas.filesystem.persistence;

import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.internal.filesystem.InMemoryFileSystem;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.io.File;
import java.util.Random;

public class FileSystemFactory {
    public FileSystem create(final File file, final long size) {
        final InFilePages pages = new InFilePages(file, size);
        return new InMemoryFileSystem(new PagePool(pages, new Random()));
    }
}
