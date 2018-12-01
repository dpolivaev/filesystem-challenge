package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.internal.memory.ArbitraryCompositePage;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pair;

class FilePage implements Page {
    static final int SIZE_POSITION = 0;
    static final int NAME_POSITION = SIZE_POSITION + Long.BYTES;
    static final int NAME_SIZE = 32;
    static final int DIRECT_PAGE_REFERENCE_POSITION = NAME_POSITION + NAME_SIZE;
    static final int INDIRECT_PAGE_REFERENCE_POSITION_1 = DIRECT_PAGE_REFERENCE_POSITION + Long.BYTES;
    static final int DATA_POSITION = INDIRECT_PAGE_REFERENCE_POSITION_1 + Long.BYTES;
    private final PagePool filePagePool;
    private final Page dataDescriptor;
    private final PageEditor pageEditor;
    private final Page data;

    FilePage(final PagePool filePagePool, final Page dataDescriptor) {
        this.filePagePool = filePagePool;
        final Pair<Page, Page> pagePair = dataDescriptor.split(DATA_POSITION);
        this.dataDescriptor = pagePair.first;
        this.data = pagePair.second;
        this.pageEditor = new PageEditor();
        pageEditor.setPage(dataDescriptor);

    }

    @Override
    public long size() {
        return Long.MAX_VALUE;
    }

    public long fileSize() {
        pageEditor.setPage(dataDescriptor);
        pageEditor.setPosition(SIZE_POSITION);
        return pageEditor.readLong();
    }

    private void setFileSize(final long size) {
        pageEditor.setPage(dataDescriptor);
        pageEditor.setPosition(SIZE_POSITION);
        pageEditor.write(size);
    }

    public String fileName() {
        pageEditor.setPage(dataDescriptor);
        pageEditor.setPosition(NAME_POSITION);
        return pageEditor.readString();
    }

    @Override
    public void write(final long offset, final byte source) {
        increaseSize(offset + 1);
        setPosition(offset);
        pageEditor.write(source);
    }

    private void setPosition(final long offset) {
        pageEditor.setPage(new ArbitraryCompositePage(this::dataPage, 1));
        pageEditor.setPosition(offset);
    }

    private Page dataPage(final long index) {
        return data;
    }

    private void increaseSize(final long requiredSize) {
        if (fileSize() < requiredSize)
            setFileSize(requiredSize);
    }

    public void truncate(final long newSize) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        increaseSize(offset + length);
        setPosition(offset);
        pageEditor.write(source, sourceOffset, length);

    }

    @Override
    public byte readByte(final long offset) {
        if (offset >= size())
            throw new EndOfFileException();
        setPosition(offset);
        return pageEditor.readByte();
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        if (offset > size() + length)
            throw new EndOfFileException();
        setPosition(offset);
        pageEditor.write(destination, destinationOffset, length);
    }

}
