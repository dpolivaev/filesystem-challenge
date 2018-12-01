package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;

class FileMemory {
    private final Page dataDescriptor;
    private final int offset;

    FileMemory(final Page dataDescriptor, final int offset) {
        this.dataDescriptor = dataDescriptor;
        this.offset = offset;
    }


    int size() {
        return 0;
    }

    void truncate(final long newSize) {

    }

    void write(final int offset, final int length, final byte[] source, final long sourceOffset) {

    }

    void read(final int offset, final int length, final byte[] destination, final long destinationOffset) {

    }

}
