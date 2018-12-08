package org.dpolivaev.katas.filesystem.internal.persistence;

import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure;
import org.dpolivaev.katas.filesystem.internal.filesystem.PagedFileSystem;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.*;
import static org.dpolivaev.katas.filesystem.internal.filesystem.PagedFileSystem.ROOT_PAGE_NUMBER;

public class FileSystemFactory {
    public static final String VERSION = "1.0";

    private static final String UUID_STRING = FileSystemFactory.class.getName() + " version "
            + VERSION;

    public static final UUID ROOT_UUID = UUID.nameUUIDFromBytes(UUID_STRING.getBytes(StandardCharsets.UTF_8));

    public static final FileSystemFactory INSTANCE = new FileSystemFactory();

    public static final long PAGE_SIZE = PersistentPage.PAGE_SIZE;

    private FileSystemFactory() {
    }

    public FileSystem create(final String fileName, final long size) {
        return create(new File(fileName), size);
    }
    public FileSystem create(final File file, final long size) {
        return create(file, size, false);
    }

    public FileSystem createConcurrent(final String fileName, final long size) {
        return createConcurrent(new File(fileName), size);
    }
    public FileSystem createConcurrent(final File file, final long size) {
        return create(file, size, true);
    }

    public FileSystem open(final File file, final long size) {
        return open(file, size, false);
    }

    public FileSystem open(final String fileName, final long size) {
        return open(new File(fileName), size);
    }

    public FileSystem openConcurrent(final File file, final long size) {
        return open(file, size, true);
    }

    public FileSystem openConcurrent(final String fileName, final long size) {
        return openConcurrent(new File(fileName), size);
    }

    private FileSystem create(final File file, final long size, final boolean threadSafe) {
        final PersistentPages pages = new PersistentPages(file, size, READ, WRITE, SPARSE, CREATE_NEW);
        final PagePool pagePool = createPagePool(pages);
        final Page rootDescriptor = pagePool.allocate(ROOT_PAGE_NUMBER);
        final PageEditor editor = new PageEditor(rootDescriptor);
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        editor.write(ROOT_UUID);
        return threadSafe ? PagedFileSystem.threadSafe(pagePool) : PagedFileSystem.singleThreaded(pagePool);
    }

    private PagePool createPagePool(final PersistentPages pages) {
        final Random random = new Random(0);
        return new PagePool(pages, random);
    }

    private FileSystem open(final File file, final long size, final boolean threadSafe) {
        if (!file.exists())
            throw new IllegalArgumentException("File not found");
        if (file.length() < 2 * PersistentPage.PAGE_SIZE)
            throw new IllegalArgumentException("File is too short");
        final PersistentPages pages = new PersistentPages(file, size, READ, StandardOpenOption.WRITE);
        final PagePool pagePool = createPagePool(pages);
        validateFileContent(pagePool);
        return threadSafe ? PagedFileSystem.threadSafe(pagePool) : PagedFileSystem.singleThreaded(pagePool);
    }

    private void validateFileContent(final PagePool pagePool) {
        final Page rootDescriptor = pagePool.pageAt(ROOT_PAGE_NUMBER);
        final PageEditor editor = new PageEditor(rootDescriptor);
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        final UUID existingUuid = editor.readUUID();
        if (!existingUuid.equals(ROOT_UUID))
            throw new IllegalArgumentException("Unexpected file system version " + existingUuid);
    }

}
