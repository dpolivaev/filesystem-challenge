package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.File;

import java.util.List;

public class InMemoryDirectory implements Directory {
    private final String name;
    private final Directory parentDirectory;

    InMemoryDirectory(String name, Directory parentDirectory) {
        this.name = name;
        this.parentDirectory = parentDirectory;
    }

    @Override
    public Directory parentDirectory() {
        return parentDirectory;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public File file(String name) {
        return null;
    }

    @Override
    public File createFile(String name) {
        return null;
    }

    @Override
    public void deleteFile(String name) {

    }

    @Override
    public Directory directory(String name) {
        return null;
    }

    @Override
    public Directory createDirectory(String name) {
        return null;
    }

    @Override
    public void deleteDirectory(String name) {

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
