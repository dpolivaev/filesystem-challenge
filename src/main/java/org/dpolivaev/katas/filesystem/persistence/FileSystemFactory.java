package org.dpolivaev.katas.filesystem.persistence;

import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure;
import org.dpolivaev.katas.filesystem.internal.filesystem.PagedFileSystem;
import org.dpolivaev.katas.filesystem.internal.filesystem.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import static org.dpolivaev.katas.filesystem.internal.filesystem.PagedFileSystem.ROOT_PAGE_NUMBER;

public class FileSystemFactory {
    public static final String VERSION = "1.0";

    private static final String UUID_STRING = FileSystemFactory.class.getName() + " version "
            + VERSION;

    public static final UUID ROOT_UUID = UUID.fromString(UUID_STRING);

    public FileSystem create(final File file, final long size) {
        final boolean fileSystemExists = file.exists();

        final PersistentPages pages = new PersistentPages(file, size);
        final PagePool pagePool = new PagePool(pages, new Random());
        final Page rootDescriptor = fileSystemExists ? pagePool.at(ROOT_PAGE_NUMBER)
                : pagePool.allocate(ROOT_PAGE_NUMBER);
        final PageEditor editor = new PageEditor();
        editor.setPage(rootDescriptor);
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        if (fileSystemExists) {
            final UUID existingUuid = editor.readUUID();
            if (!existingUuid.equals(ROOT_UUID))
                throw new IllegalArgumentException("Unexpected file system version");
        } else
            editor.write(ROOT_UUID);
        return new PagedFileSystem(pagePool);
    }
}
