package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.File;

class InMemoryFile implements File {
    private final FilePage filePage;
    private final Directory parentDirectory;

    InMemoryFile(final FilePage filePage, final Directory parentDirectory) {
        this.filePage = filePage;
        this.parentDirectory = parentDirectory;
    }

    @Override
    public Directory parentDirectory() {
        return parentDirectory;
    }

    @Override
    public String name() {
        return filePage.fileName();
    }

    @Override
    public long size() {
        return filePage.size();
    }

    @Override
    public void truncate(final long newSize) {
        filePage.truncate(newSize);
    }

    @Override
    public void write(final int offset, final long length, final byte[] source, final long sourceOffset) {
        filePage.write(offset, length, source, sourceOffset);
    }

    @Override
    public void read(final int offset, final long length, final byte[] destination, final long destinationOffset) {
        filePage.read(offset, length, destination, destinationOffset);
    }
}
