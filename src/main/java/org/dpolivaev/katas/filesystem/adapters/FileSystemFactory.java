package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.FileSystem;
import org.dpolivaev.katas.filesystem.domain.internal.InMemoryFileSystem;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;

import java.io.File;
import java.util.Random;

public class FileSystemFactory {
    public FileSystem create(final File file, final long size) {
        final InFilePages pages = new InFilePages(file, size);
        return new InMemoryFileSystem(new PagePool(pages, new Random()));
    }
}
