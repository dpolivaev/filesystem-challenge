package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.File;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PageAllocation;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

class InMemoryDirectory implements Directory {
    enum DirectoryElements {FREE_SPACE, FILE, DIRECTORY}

    private final PageEditor editor;
    private final PagePool pagePool;
    private final InMemoryFile directoryData;
    private final Directory parentDirectory;


    InMemoryDirectory(final PagePool pagePool, final InMemoryFile directoryData, final Directory parentDirectory) {
        this.pagePool = pagePool;
        this.directoryData = directoryData;
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
    public Optional<File> file(final String name) {
        return findByName(name, DirectoryElements.FILE).map(this::toFile);
    }

    private File toFile(final Page page) {
        return new InMemoryFile(new FilePage(pagePool, page), this);
    }

    private Directory toDirectory(final Page page) {
        final InMemoryFile file = new InMemoryFile(new FilePage(pagePool, page), this);
        return new InMemoryDirectory(pagePool, file, this);
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
        if (editor.requiredLength(name) > FilePage.NAME_SIZE)
            throw new IllegalArgumentException("Name too long");
        if (elementNames(elementType).contains(name))
            throw new IllegalArgumentException("File already exists");
        final PageAllocation allocation = pagePool.allocate();
        setName(allocation.page, name);
        register(elementType, allocation.pageNumber);
        return allocation;
    }

    private List<Page> descriptors(final DirectoryElements elementType) {
        final List<Page> pages = new ArrayList<>();
        directoryData.setPosition(0);
        for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = editor.getPosition()) {
            final byte element = directoryData.readByte();
            final long pageNumber = directoryData.readLong();
            if (element == elementType.ordinal()) {
                pages.add(pagePool.at(pageNumber));
            }
        }
        return pages;
    }

    private void register(final DirectoryElements elementType, final long pageNumber) {
        directoryData.setPosition(0);
        for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = editor.getPosition()) {
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

    private void deleteElement(final String name, final DirectoryElements elementType) {
        directoryData.setPosition(0);
        for (long readDataCounter = 0; readDataCounter < directoryData.size(); readDataCounter = editor.getPosition()) {
            final byte element = directoryData.readByte();
            final long pageNumber = directoryData.readLong();
            if (element == elementType.ordinal()) {
                final Page page = pagePool.at(pageNumber);
                if (name.equals(toName(page))) {
                    new FilePage(pagePool, page).destroy();
                    pagePool.release(pageNumber);
                    directoryData.setPosition(directoryData.getPosition() - Byte.BYTES - Long.BYTES);
                    directoryData.write((byte) 0);
                    return;
                }
            }
        }
    }

    @Override
    public void deleteFile(final String name) {
        deleteElement(name, DirectoryElements.FILE);
    }

    @Override
    public List<String> files() {
        return elementNames(DirectoryElements.FILE);
    }

    public List<String> elementNames(final DirectoryElements file) {
        return descriptors(file).stream().map(this::toName).collect(toList());
    }

    private String toName(final Page descriptor) {
        return editor.on(descriptor, FilePage.NAME_POSITION, editor::readString);
    }

    private void setName(final Page descriptor, final String name) {
        editor.on(descriptor, FilePage.NAME_POSITION, () -> editor.write(name));
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
        final PageAllocation allocation = allocateFirstPage(name, DirectoryElements.FILE);
        return toDirectory(allocation.page);
    }

    @Override
    public void deleteDirectory(final String name) {
        deleteElement(name, DirectoryElements.DIRECTORY);
    }

}
