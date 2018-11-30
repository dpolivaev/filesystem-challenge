package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.internal.memory.DataBlock;

class FileMemory {
    private final DataBlock dataDescriptor;
    private final int offset;

    FileMemory(DataBlock dataDescriptor, int offset){
        this.dataDescriptor = dataDescriptor;
        this.offset = offset;
    }


    int size() {
        return 0;
    }

    void truncate(long newSize) {

    }

    void write(int offset, int length, byte[] source, long sourceOffset) {

    }

    void read(int offset, int length, byte[] destination, long destinationOffset) {

    }

}
