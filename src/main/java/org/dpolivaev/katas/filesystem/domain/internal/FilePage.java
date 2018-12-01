package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;

class FilePage implements Page {
    private static final int SIZE_POSITION = 0;
    private static final long NAME_POSITION = 4;
    private final PagePool filePagePool;
    private final Page dataDescriptor;
    private final PageEditor pageEditor;

    FilePage(final PagePool filePagePool, final Page dataDescriptor) {
        this.filePagePool = filePagePool;
        this.dataDescriptor = dataDescriptor;
        this.pageEditor = new PageEditor();
        pageEditor.setPage(dataDescriptor);

    }

    @Override
    public long size() {
        pageEditor.setPage(dataDescriptor);
        pageEditor.setPosition(SIZE_POSITION);
        return pageEditor.readLong();
    }

    public String fileName() {
        pageEditor.setPage(dataDescriptor);
        pageEditor.setPosition(NAME_POSITION);
        return pageEditor.readString();
    }

    @Override
    public void write(final long offset, final byte source) {
        increaseSize(offset + 1);
        selectEditPosition(offset);
        pageEditor.write(source);
    }

    private void selectEditPosition(final long offset) {
        throw new RuntimeException("Method not implemented");
    }

    private void increaseSize(final long requiredSize) {
    }

    public void truncate(final long newSize) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public void write(final long offset, final long length, final byte[] source, final long sourceOffset) {
        increaseSize(offset + length);
        throw new RuntimeException("Method not implemented");

    }

    @Override
    public byte readByte(final long offset) {
        if (offset >= size())
            throw new EndOfFileException();
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public void read(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        if (offset > size() + length)
            throw new EndOfFileException();
        throw new RuntimeException("Method not implemented");
    }

}
