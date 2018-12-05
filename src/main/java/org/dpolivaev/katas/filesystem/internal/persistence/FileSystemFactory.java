package org.dpolivaev.katas.filesystem.internal.persistence;

import org.dpolivaev.katas.filesystem.FileSystem;
import org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure;
import org.dpolivaev.katas.filesystem.internal.filesystem.PagedFileSystem;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.io.File;
import java.io.FileNotFoundException;
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

    private FileSystemFactory() {
    }

    public FileSystem create(final File file, final long size) {
        final PersistentPages pages = new PersistentPages(file, size, READ, WRITE, SPARSE, CREATE_NEW);
        final PagePool pagePool = new PagePool(pages, new Random());
        final Page rootDescriptor = pagePool.allocate(ROOT_PAGE_NUMBER);
        final PageEditor editor = uuidEditor(rootDescriptor);
        editor.write(ROOT_UUID);
        return new PagedFileSystem(pagePool);
    }

    public FileSystem open(final File file, final long size) {
        if (!file.exists())
            throw new IORuntimeException(new FileNotFoundException());
        final PersistentPages pages = new PersistentPages(file, size, READ, StandardOpenOption.WRITE);
        final PagePool pagePool = new PagePool(pages, new Random());
        final Page rootDescriptor = pagePool.pageAt(ROOT_PAGE_NUMBER);
        final PageEditor editor = uuidEditor(rootDescriptor);
        checkUuid(editor);
        return new PagedFileSystem(pagePool);
    }

    private void checkUuid(final PageEditor editor) {
        final UUID existingUuid = editor.readUUID();
        checkUuid(existingUuid);
    }

    private void checkUuid(final UUID existingUuid) {
        if (!existingUuid.equals(ROOT_UUID))
            throw new IllegalArgumentException("Unexpected file system version " + existingUuid);
    }

    private PageEditor uuidEditor(final Page rootDescriptor) {
        final PageEditor editor = new PageEditor();
        editor.setPage(rootDescriptor);
        editor.setPosition(FileDescriptorStructure.UUID_POSITION);
        return editor;
    }
}