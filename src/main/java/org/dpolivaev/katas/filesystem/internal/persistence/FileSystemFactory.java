package org.dpolivaev.katas.filesystem.internal.persistence;

import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.IllegalArgumentIOException;
import org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure;
import org.dpolivaev.katas.filesystem.internal.filesystem.PagedFileSystem;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.*;

public class FileSystemFactory {
    public static final String VERSION = "1.0";

    private static final String UUID_STRING = FileSystemFactory.class.getName() + " version "
            + VERSION;

    public static final UUID ROOT_UUID = UUID.nameUUIDFromBytes(UUID_STRING.getBytes(StandardCharsets.UTF_8));

    public static final FileSystemFactory INSTANCE = new FileSystemFactory();

    public static final long PAGE_SIZE = PersistentPage.PAGE_SIZE;

    static final int DESCRIPTOR_SIZE = FileDescriptorStructure.DATA_POSITION;

    public static final int MINIMAL_EXTERNAL_FILE_SIZE = 3 * PersistentPage.PAGE_SIZE + FileSystemFactory.DESCRIPTOR_SIZE;

    private FileSystemFactory() {
    }

    public FileSystem create(final String fileName, final long size) {
        return create(new File(fileName), size);
    }
    public FileSystem create(final File file, final long size) {
        return create(file, size, false);
    }

    public FileSystem createThreadSafe(final String fileName, final long size) {
        return createThreadSafe(new File(fileName), size);
    }

    public FileSystem createThreadSafe(final File file, final long size) {
        return create(file, size, true);
    }

    public FileSystem open(final File file) {
        return open(file, false);
    }

    public FileSystem open(final String fileName) {
        return open(new File(fileName));
    }

    public FileSystem openThreadSafe(final File file) {
        return open(file, true);
    }

    public FileSystem openThreadSafe(final String fileName) {
        return openThreadSafe(new File(fileName));
    }

    private FileSystem create(final File file, final long size, final boolean threadSafe) {
        if (file.exists())
            throw new IllegalArgumentIOException("File already exists");
        if (size < MINIMAL_EXTERNAL_FILE_SIZE)
            throw new IllegalArgumentIOException("File size is too small");
        final PersistentPages pages = new PersistentPages(file, size, READ, WRITE, SPARSE, CREATE_NEW);
        final PageEditor editor = new PageEditor(pages.descriptorPage());
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        editor.write(ROOT_UUID);
        editor.setPosition(FileDescriptorStructure.SIZE_POSITION);
        editor.write(size);
        final PagePool pagePool = createPagePool(pages);
        return threadSafe ? PagedFileSystem.threadSafe(pagePool) : PagedFileSystem.singleThreaded(pagePool);
    }

    private PagePool createPagePool(final PersistentPages pages) {
        final Random random = new Random(0);
        return new PagePool(pages, random);
    }

    private FileSystem open(final File file, final boolean threadSafe) {
        if (!file.exists())
            throw new IllegalArgumentIOException("File not found");
        if (file.length() < MINIMAL_EXTERNAL_FILE_SIZE - PAGE_SIZE)
            throw new IllegalArgumentIOException("File is too short");
        final PersistentPages pages = new PersistentPages(file, 0, READ, StandardOpenOption.WRITE);
        final PageEditor editor = new PageEditor(pages.descriptorPage());
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        final UUID existingUuid = editor.readUUID();
        if (!existingUuid.equals(ROOT_UUID))
            throw new IllegalArgumentIOException("Unexpected file system version " + existingUuid);
        editor.setPosition(FileDescriptorStructure.SIZE_POSITION);
        final long maximalFileSize = editor.readLong();
        pages.setMaximalFileSize(maximalFileSize);
        final PagePool pagePool = createPagePool(pages);
        return threadSafe ? PagedFileSystem.threadSafe(pagePool) : PagedFileSystem.singleThreaded(pagePool);
    }

}
