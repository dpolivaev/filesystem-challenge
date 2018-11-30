package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.File;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Memory;

class InMemoryFile implements File {
    private final FileMemory fileMemory;
    private final String name;
    private final Directory parentDirectory;

    InMemoryFile(FileMemory fileMemory, String name, Directory parentDirectory) {
        this.fileMemory = fileMemory;
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
    public int size() {
        return fileMemory.size();
    }

    @Override
    public void truncate(long newSize) {
        fileMemory.truncate(newSize);
    }

    @Override
    public void write(int offset, int length, byte[] source, long sourceOffset) {
        fileMemory.write(offset, length, source, sourceOffset);
    }

    @Override
    public void read(int offset, int length, byte[] destination, long destinationOffset) {
        fileMemory.read(offset, length, destination, destinationOffset);
    }
}
