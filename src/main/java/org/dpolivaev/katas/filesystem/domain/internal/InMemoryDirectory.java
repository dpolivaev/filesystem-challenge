package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.File;

import java.util.List;

public class InMemoryDirectory implements Directory {
    private final Directory parentDirectory;
    private final FilePage directoryPage;

    InMemoryDirectory(final Directory parentDirectory, final FilePage directoryPage) {
        this.parentDirectory = parentDirectory;
        this.directoryPage = directoryPage;
    }

    @Override
    public Directory parentDirectory() {
        return parentDirectory;
    }

    @Override
    public String name() {
        return directoryPage.fileName();
    }

    @Override
    public File file(final String name) {
        return null;
    }

    @Override
    public File createFile(final String name) {
        return null;
    }

    @Override
    public void deleteFile(final String name) {

    }

    @Override
    public Directory directory(final String name) {
        return null;
    }

    @Override
    public Directory createDirectory(final String name) {
        return null;
    }

    @Override
    public void deleteDirectory(final String name) {

    }

    @Override
    public List<File> files() {
        return null;
    }

    @Override
    public List<Directory> directories() {
        return null;
    }
}
