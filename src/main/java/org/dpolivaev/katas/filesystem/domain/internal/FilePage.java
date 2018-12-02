package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.internal.memory.*;

import java.util.ArrayList;
import java.util.List;

class FilePage implements Page {
    static final int SIZE_POSITION = 0;
    static final int NAME_POSITION = SIZE_POSITION + Long.BYTES;
    static final int NAME_SIZE = 32;
    static final int PAGE_REFERENCE_POSITION = NAME_POSITION + NAME_SIZE;
    public static final int PAGE_LEVEL_COUNT = 5;
    static final int DATA_POSITION = PAGE_REFERENCE_POSITION + PAGE_LEVEL_COUNT * Long.BYTES;
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
        final List<Page> pages = createPages(pagePair.second);
        data = new ArbitraryCompositePage(pages::get, pages.size());

    }

    private List<Page> createPages(final Page data) {
        final int poolPageSize = filePagePool.pageSize();
        final Page referencesPage = dataDescriptor.subpage(PAGE_REFERENCE_POSITION, 2 * Long.BYTES);
        final List<Page> pages = new ArrayList<>(PAGE_LEVEL_COUNT + 1);
        pages.add(data);
        long levelPageSize = poolPageSize;
        for (int i = 0; i < PAGE_LEVEL_COUNT; i++) {
            final int level = i;
            final long finalLevelPageSize = levelPageSize;
            pages.add(new SameSizeCompositePage(
                    index -> referencedPage(referencesPage, level, level, finalLevelPageSize),
                    1, levelPageSize));
            levelPageSize *= poolPageSize / Long.BYTES;
        }
        return pages;
    }

    private Page referencedPage(final Page referencePage, final int index, final int referenceLevel, final long levelPageSize) {
        final long pageNumber = editor(referencePage, index * Long.BYTES).readLong();
        if (referenceLevel == 0) {
            if (pageNumber != 0)
                return filePagePool.at(pageNumber);
            else {
                return new LazyPage(() -> allocatePoolPage(referencePage, index), levelPageSize);
            }
        } else {
            final Page nextLevelReferencePage;
            if (pageNumber != 0)
                nextLevelReferencePage = filePagePool.at(pageNumber);
            else
                nextLevelReferencePage = allocatePoolPage(referencePage, index);
            final int poolPageSize = filePagePool.pageSize();
            final int pageCount = poolPageSize / Long.BYTES;
            return new SameSizeCompositePage(i -> referencedPage(nextLevelReferencePage, i, referenceLevel - 1, levelPageSize),
                    pageCount, levelPageSize / (pageCount));
        }
    }

    private Page allocatePoolPage(final Page page, final int index) {
        final PageAllocation allocation = filePagePool.reserve();
        editor(page, index * Long.BYTES).write(allocation.pageNumber);
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
