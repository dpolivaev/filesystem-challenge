package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.FileAlreadyExistsException;
import org.dpolivaev.katas.filesystem.IllegalArgumentIOException;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pool.PageAllocation;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.NAME_POSITION;
import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.NAME_SIZE;

class PagedDirectory implements Directory {
    private static final String ANY = "";

    enum DirectoryElements {FREE_SPACE, FILE, DIRECTORY, ANY}

    private final PageEditor editor;
    private final PagePool pagePool;
    private final File directoryData;
    private final Directory parentDirectory;


    PagedDirectory(final PagePool pagePool, final Page directoryData, final Directory parentDirectory) {
        this.pagePool = pagePool;
        this.directoryData = new PagedFile(new FilePage(pagePool, directoryData), this);
        this.parentDirectory = parentDirectory != null ? parentDirectory : this;
        editor = new PageEditor(null);
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
    public UUID uuid() {
        return directoryData.uuid();
    }

    @Override
    public boolean exists() {
        return directoryData.exists();
    }

    @Override
    public Optional<File> file(final String name) {
        checkElementName(name);
        return findByName(name, DirectoryElements.FILE).map(this::toFile);
    }

    private File toFile(final Page page) {
        final FilePage filePage = new FilePage(pagePool, page);
        return toFile(filePage);
    }

    protected File toFile(final FilePage filePage) {
        return new PagedFile(filePage, this);
    }

    protected Directory toDirectory(final Page page) {
        return new PagedDirectory(pagePool, page, this);
    }

    private Optional<Page> findByName(final String name, final DirectoryElements elementType) {
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
        checkElementName(name);
        if (elementNames(elementType).contains(name))
            throw new FileAlreadyExistsException("File '" + name + "' already exists");
    }

    private void checkElementName(final String name) {
        if (name == null)
            throw new IllegalArgumentIOException("Name must not be null");
        if (PageEditor.requiredLength(name) > NAME_SIZE)
            throw new IllegalArgumentIOException("Name is too long");
        if (name.isEmpty())
            throw new IllegalArgumentIOException("Empty name is not allowed");
    }

    private List<Page> descriptors(final DirectoryElements elementType) {
        final List<Page> pages = new ArrayList<>();
        directoryData.at(0, () -> {
            for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = directoryData.getPosition()) {
                final byte element = directoryData.readByte();
                final long pageNumber = directoryData.readLong();
                if (element == elementType.ordinal()) {
                    pages.add(pagePool.pageAt(pageNumber));
                }
            }
        });
        return pages;
    }

    private void register(final DirectoryElements elementType, final long pageNumber) {
        directoryData.at(0, () -> {
            for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = directoryData.getPosition()) {
                final byte element = directoryData.readByte();
                if (element == DirectoryElements.FREE_SPACE.ordinal()) {
                    directoryData.setPosition(directoryData.getPosition() - Byte.BYTES);
                    directoryData.write((byte) elementType.ordinal());
                    directoryData.write(pageNumber);
                    return;
                }
                directoryData.setPosition(directoryData.getPosition() + Long.BYTES);
            }
            directoryData.write((byte) elementType.ordinal());
            directoryData.write(pageNumber);
        });
    }

    private void deleteElement(final String name, final DirectoryElements elementType, final File directoryData) {
        directoryData.at(0, () -> {
            for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = directoryData.getPosition()) {
                final byte element = directoryData.readByte();
                if (elementType == DirectoryElements.ANY && element != 0 || element == elementType.ordinal()) {
                    final long pageNumber = directoryData.readLong();
                    final Page page = pagePool.pageAt(pageNumber);
                    if (matchesAnyElement(name) || name.equals(toName(page))) {
                        destroyElement(DirectoryElements.values()[element], pageNumber, page);
                        if (!matchesAnyElement(name))
                            return;
                    }
                } else
                    directoryData.setPosition(directoryData.getPosition() + Long.BYTES);
            }
        });
    }

    private boolean matchesAnyElement(final String name) {
        return ANY.equals(name);
    }

    private void destroyElement(final DirectoryElements elementType, final long pageNumber, final Page page) {
        if (elementType == DirectoryElements.DIRECTORY) {
            destroyAllChildren(page);
        }
        destroyFilePage(pageNumber, new FilePage(pagePool, page));
        directoryData.setPosition(directoryData.getPosition() - Byte.BYTES - Long.BYTES);
        directoryData.write((byte) 0);
        directoryData.write(0L);
    }

    protected void destroyFilePage(final long pageNumber, final FilePage page) {
        page.destroy();
        pagePool.release(pageNumber);
    }

    private void destroyAllChildren(final Page page) {
        final FilePage filePage = new FilePage(pagePool, page);
        final File data = toFile(filePage);
        deleteElement(ANY, DirectoryElements.ANY, data);
    }

    @Override
    public void deleteFile(final String name) {
        checkElementName(name);
        deleteElement(name, DirectoryElements.FILE, directoryData);
    }

    @Override
    public List<String> files() {
        return elementNames(DirectoryElements.FILE);
    }

    private List<String> elementNames(final DirectoryElements elementType) {
        return descriptors(elementType).stream().map(this::toName).collect(toList());
    }

    private String toName(final Page descriptor) {
        return editor.at(descriptor, NAME_POSITION, editor::readString);
    }

    private void setName(final Page descriptor, final String name) {
        editor.at(descriptor, NAME_POSITION, () -> editor.write(name));
    }

    @Override
    public List<String> directories() {
        return elementNames(DirectoryElements.DIRECTORY);
    }

    @Override
    public Optional<Directory> directory(final String name) {
        checkElementName(name);
        return findByName(name, DirectoryElements.DIRECTORY).map(this::toDirectory);
    }

    @Override
    public Directory createDirectory(final String name) {
        final PageAllocation allocation = allocateFirstPage(name, DirectoryElements.DIRECTORY);
        return toDirectory(allocation.page);
    }

    @Override
    public void deleteDirectory(final String name) {
        checkElementName(name);
        deleteElement(name, DirectoryElements.DIRECTORY, directoryData);
    }

    @Override
    public String toString() {
        return "PagedDirectory{" +
                directoryData.toString() + '(' + directories().size() + ", " + files().size() + ')' + '}';
    }

    long directoryInternalFileSize() {
        return directoryData.size();
    }
}
