package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.internal.memory.*;

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
        this.pageEditor = new PageEditor();
        this.pageEditor.setPage(dataDescriptor);
        final Page[] pages = createPages(pagePair.second);
        data = new ArbitraryCompositePage(index -> pages[(int) index], pages.length);

    }

    private Page[] createPages(final Page data) {
        final int poolPageSize = filePagePool.pageSize();
        final Page directReferenced = new SameSizeCompositePage(
                index -> indirectPage(0, DIRECT_PAGE_REFERENCE_POSITION, poolPageSize),
                1, poolPageSize);
        return new Page[]{data, directReferenced};
    }

    private Page indirectPage(final long index, final int referencePosition, final int pageSize) {
        final long pageNumber = descriptor(referencePosition).readLong();
        if (pageNumber != 0)
            return filePagePool.at(pageNumber);
        else
            return new LazyPage(() -> allocatePoolPage(referencePosition), pageSize);
    }

    private Page allocatePoolPage(final int referencePosition) {
        final PageAllocation allocation = filePagePool.reserve();
        descriptor(referencePosition).write(allocation.pageNumber);
        return allocation.page;
    }

    @Override
    public long size() {
        return data.size();
    }

    public long fileSize() {
        return descriptor(SIZE_POSITION).readLong();
    }

    private PageEditor descriptor(final int position) {
        return editor(dataDescriptor, position);
    }

    private PageEditor editor(final Page page, final int position) {
        pageEditor.setPage(page);
        pageEditor.setPosition(position);
        return pageEditor;
    }

    private void setFileSize(final long size) {
        descriptor(SIZE_POSITION).write(size);
    }

    public String fileName() {
        return descriptor(NAME_POSITION).readString();
    }

    @Override
    public void write(final long offset, final byte source) {
        increaseSize(offset + 1);
        setPosition(offset);
        pageEditor.write(source);
    }

    private void setPosition(final long offset) {
        pageEditor.setPage(data);
        pageEditor.setPosition(offset);
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
