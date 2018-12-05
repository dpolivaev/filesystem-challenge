package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pool.ConcurrentPagePool;

import java.util.List;
import java.util.Optional;

public class ConcurrentPagedDirectory extends PagedDirectory {
    ConcurrentPagedDirectory(final ConcurrentPagePool pagePool, final Page directoryData, final Directory parentDirectory) {
        super(pagePool, directoryData, parentDirectory);
    }

    @Override
    public String name() {
        return super.name();
    }

    @Override
    public boolean exists() {
        return super.exists();
    }

    @Override
    public Optional<File> file(final String name) {
        return super.file(name);
    }

    @Override
    protected File toFile(final Page page) {
        return super.toFile(page);
    }

    @Override
    protected Directory toDirectory(final Page page) {
        return super.toDirectory(page);
    }

    @Override
    public Optional<Page> findByName(final String name, final DirectoryElements elementType) {
        return super.findByName(name, elementType);
    }

    @Override
    public File createFile(final String name) {
        return super.createFile(name);
    }

    @Override
    public void deleteFile(final String name) {
        super.deleteFile(name);
    }

    @Override
    public List<String> files() {
        return super.files();
    }

    @Override
    public List<String> elementNames(final DirectoryElements file) {
        return super.elementNames(file);
    }

    @Override
    public List<String> directories() {
        return super.directories();
    }

    @Override
    public Optional<Directory> directory(final String name) {
        return super.directory(name);
    }

    @Override
    public Directory createDirectory(final String name) {
        return super.createDirectory(name);
    }

    @Override
    public void deleteDirectory(final String name) {
        super.deleteDirectory(name);
    }
}
