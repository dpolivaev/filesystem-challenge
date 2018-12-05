package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pool.PageAllocation;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.NAME_POSITION;
import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.NAME_SIZE;

class PagedDirectory implements Directory {
    private static final String ANY = "";

    enum DirectoryElements {FREE_SPACE, FILE, DIRECTORY, ANY}

    private final PageEditor editor;
    private final PagePool pagePool;
    private final PagedFile directoryData;
    private final Directory parentDirectory;


    PagedDirectory(final PagePool pagePool, final Page directoryData, final Directory parentDirectory) {
        this.pagePool = pagePool;
        this.directoryData = new PagedFile(new FilePage(pagePool, directoryData), this);
        this.parentDirectory = parentDirectory != null ? parentDirectory : this;
        editor = new PageEditor();
    }

    @Override
    public Directory parentDirectory() {
        return parentDirectory;
    }

    @Override
    public String name() {
        return directoryData.name();
    }

    @Override
    public boolean exists() {
        return directoryData.exists();
    }

    @Override
    public Optional<File> file(final String name) {
        return findByName(name, DirectoryElements.FILE).map(this::toFile);
    }

    private File toFile(final Page page) {
        return new PagedFile(new FilePage(pagePool, page), this);
    }

    private Directory toDirectory(final Page page) {
        return new PagedDirectory(pagePool, page, this);
    }

    public Optional<Page> findByName(final String name, final DirectoryElements elementType) {
        return descriptors(elementType).stream().filter(page -> name.equals(toName(page))).findFirst();
    }

    @Override
    public File createFile(final String name) {
        final PageAllocation allocation = allocateFirstPage(name, DirectoryElements.FILE);
        return toFile(allocation.page);
    }

    private PageAllocation allocateFirstPage(final String name, final DirectoryElements elementType) {
        checkNewElementName(name, elementType);
        final PageAllocation allocation = pagePool.allocate();
        setName(allocation.page, name);
        register(elementType, allocation.pageNumber);
        return allocation;
    }

    private void checkNewElementName(final String name, final DirectoryElements elementType) {
        if (name == null)
            throw new IllegalArgumentException("Name must not be null");
        if (PageEditor.requiredLength(name) > NAME_SIZE)
            throw new IllegalArgumentException("Name too long");
        if (name.isEmpty())
            throw new IllegalArgumentException("Empty name is not allowed");
        if (matchesAnyElement(name))
            throw new IllegalArgumentException("Name " + ANY + " is reserved");
        if (elementNames(elementType).contains(name))
            throw new IllegalArgumentException("File already exists");
    }

    private List<Page> descriptors(final DirectoryElements elementType) {
        final List<Page> pages = new ArrayList<>();
        directoryData.setPosition(0);
        for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = directoryData.getPosition()) {
            final byte element = directoryData.readByte();
            final long pageNumber = directoryData.readLong();
            if (element == elementType.ordinal()) {
                pages.add(pagePool.pageAt(pageNumber));
            }
        }
        return pages;
    }

    private void register(final DirectoryElements elementType, final long pageNumber) {
        directoryData.setPosition(0);
        for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = directoryData.getPosition()) {
            final byte element = directoryData.readByte();
            if (element == DirectoryElements.FREE_SPACE.ordinal()) {
                directoryData.setPosition(directoryData.getPosition() - Byte.BYTES);
                directoryData.write(element);
                directoryData.write(pageNumber);
                return;
            }
        }
        directoryData.write((byte) elementType.ordinal());
        directoryData.write(pageNumber);
    }

    private void deleteElement(final String name, final DirectoryElements elementType, final PagedFile directoryData) {
        directoryData.setPosition(0);
        for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = directoryData.getPosition()) {
            final byte element = directoryData.readByte();
            final long pageNumber = directoryData.readLong();
            if (elementType == DirectoryElements.ANY && element != 0 || element == elementType.ordinal()) {
                final Page page = pagePool.pageAt(pageNumber);
                if (matchesAnyElement(name) || name.equals(toName(page))) {
                    destroyElement(DirectoryElements.values()[element], pageNumber, page);
                    if (!matchesAnyElement(name))
                        return;
                }
            }
        }
    }

    private boolean matchesAnyElement(final String name) {
        return ANY.equals(name);
    }

    private void destroyElement(final DirectoryElements elementType, final long pageNumber, final Page page) {
        if (elementType == DirectoryElements.DIRECTORY) {
            destroyAllChildren(page);
        }
        new FilePage(pagePool, page).destroy();
        pagePool.release(pageNumber);
        directoryData.setPosition(directoryData.getPosition() - Byte.BYTES - Long.BYTES);
        directoryData.write((byte) 0);
        directoryData.write(0L);
    }

    private void destroyAllChildren(final Page page) {
        final FilePage filePage = new FilePage(pagePool, page);
        final PagedFile data = new PagedFile(filePage, this);
        deleteElement(ANY, DirectoryElements.ANY, data);

    }

    @Override
    public void deleteFile(final String name) {
        deleteElement(name, DirectoryElements.FILE, directoryData);
    }

    @Override
    public List<String> files() {
        return elementNames(DirectoryElements.FILE);
    }

    public List<String> elementNames(final DirectoryElements file) {
        return descriptors(file).stream().map(this::toName).collect(toList());
    }

    private String toName(final Page descriptor) {
        return editor.on(descriptor, NAME_POSITION, editor::readString);
    }

    private void setName(final Page descriptor, final String name) {
        editor.on(descriptor, NAME_POSITION, () -> editor.write(name));
    }

    @Override
    public List<String> directories() {
        return elementNames(DirectoryElements.DIRECTORY);
    }

    @Override
    public Optional<Directory> directory(final String name) {
        return findByName(name, DirectoryElements.DIRECTORY).map(this::toDirectory);
    }

    @Override
    public Directory createDirectory(final String name) {
        final PageAllocation allocation = allocateFirstPage(name, DirectoryElements.DIRECTORY);
        return toDirectory(allocation.page);
    }

    @Override
    public void deleteDirectory(final String name) {
        deleteElement(name, DirectoryElements.DIRECTORY, directoryData);
    }
}
