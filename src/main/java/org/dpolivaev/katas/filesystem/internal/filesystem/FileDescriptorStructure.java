package org.dpolivaev.katas.filesystem.internal.filesystem;

public interface FileDescriptorStructure {
    int UUID_POSITION = 0;

    int UUID_SIZE = 16 * Long.BYTES;

    int NAME_POSITION = UUID_POSITION + UUID_SIZE;

    int NAME_SIZE = 32;

    int SIZE_POSITION = NAME_POSITION + NAME_SIZE;

    int PAGE_REFERENCE_POSITION = SIZE_POSITION + Long.BYTES;

    int PAGE_LEVEL_COUNT = 5;

    int DATA_POSITION = PAGE_REFERENCE_POSITION + PAGE_LEVEL_COUNT * Long.BYTES;
}
