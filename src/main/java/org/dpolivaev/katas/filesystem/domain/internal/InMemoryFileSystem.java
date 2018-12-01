package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.FileSystem;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;

public class InMemoryFileSystem implements FileSystem {
    private final Page fileSystemDescriptor;
    private final PagePool fileDescriptorPool;
    private final PagePool filePagePool;

    public InMemoryFileSystem(final Page fileSystemDescriptor, final PagePool fileDescriptorPool, final PagePool filePagePool) {
        this.fileSystemDescriptor = fileSystemDescriptor;
        this.fileDescriptorPool = fileDescriptorPool;
        this.filePagePool = filePagePool;
    }

    @Override
    public Directory root() {
        return null;
    }
}
