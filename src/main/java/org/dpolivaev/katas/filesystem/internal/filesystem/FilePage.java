package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.*;
import org.dpolivaev.katas.filesystem.internal.pool.PageAllocation;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.*;

class FilePage implements Page {

    private final UUID uuid;
    private final PagePool pagePool;
    private final Page descriptorAndDataPage;
    private final Page dataDescriptor;
    private final PageEditor editor;
    private final Page data;

    FilePage(final PagePool pagePool, final Page descriptorAndDataPage) {
        this.pagePool = pagePool;
        this.descriptorAndDataPage = descriptorAndDataPage;
        final Pair<Page, Page> pagePair = descriptorAndDataPage.split(DATA_POSITION);
        this.dataDescriptor = pagePair.first;
        this.editor = new PageEditor();
        UUID uuid = readUUID();
        if (uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() == 0) {
            uuid = UUID.randomUUID();
            editor.setPage(dataDescriptor);
            editor.setPosition(UUID_POSITION);
            editor.write(uuid);
        }
        this.uuid = uuid;
        final List<Page> pages = createPages(pagePair.second);
        data = new ArbitraryCompositePage(pages::get, pages.size());
    }

    private UUID readUUID() {
        return editor.on(dataDescriptor, UUID_POSITION, editor::readUUID);
    }

    void validateUuid() {
        if (!uuid.equals(readUUID()))
            throw new IllegalStateException("File UUID changed");
    }

    private List<Page> createPages(final Page data) {
        final int poolPageSize = pagePool.pageSize();
        final Page referencesPage = dataDescriptor.subpage(PAGE_REFERENCE_POSITION, PAGE_LEVEL_COUNT * Long.BYTES);
        final List<Page> pages = new ArrayList<>(PAGE_LEVEL_COUNT + 1);
        pages.add(data);
        long levelPageSize = poolPageSize;
        for (int i = 0; i < PAGE_LEVEL_COUNT; i++) {
            final int level = i;
            final long finalLevelPageSize = levelPageSize;
            pages.add(new SameSizeCompositePage(
                    index -> referencedPage(referencesPage, level, finalLevelPageSize),
                    1, finalLevelPageSize));
            levelPageSize *= poolPageSize / Long.BYTES;
        }
        return pages;
    }

    private Page referencedPage(final Page referencePage, final int index, final long levelPageSize) {
        final long pageNumber = editor.on(referencePage, index * Long.BYTES, editor::readLong);
        final int poolPageSize = pagePool.pageSize();
        final Page poolPage;
        if (pageNumber != 0) {
            poolPage = pagePool.pageAt(pageNumber);
        } else {
            poolPage = new LazyPage(() -> allocatePoolPage(referencePage, index), poolPageSize);
        }
        if (levelPageSize == poolPageSize) {
            return poolPage;
        } else {
            final int compositePageCount = poolPageSize / Long.BYTES;
            final long nextLevelPageSize = levelPageSize / compositePageCount;
            return new SameSizeCompositePage(i -> referencedPage(poolPage, i, nextLevelPageSize), compositePageCount, nextLevelPageSize);
        }
    }


    public void destroy() {
        truncate();
        descriptorAndDataPage.erase(0, DATA_POSITION);
    }

    public void truncate() {
        final Page referencesPage = dataDescriptor.subpage(PAGE_REFERENCE_POSITION, PAGE_LEVEL_COUNT * Long.BYTES);
        for (int level = 0; level < PAGE_LEVEL_COUNT; level++) {
            destroyReferencedPages(referencesPage, level, level);
        }
        setFileSize(0);
        descriptorAndDataPage.erase(DATA_POSITION, descriptorAndDataPage.size() - DATA_POSITION);
    }

    private void destroyReferencedPages(final Page page, final int index, final int level) {
        final long pageNumber = editor.on(page, index * Long.BYTES, editor::readLong);
        if (pageNumber != 0) {
            final Page referencedPage = pagePool.pageAt(pageNumber);
            pagePool.release(pageNumber);
            editor.on(page, index * Long.BYTES, () -> editor.write(0L));
            if (level > 0) {
                IntStream.range(0, (int) referencedPage.size() / Long.BYTES).forEach(i -> destroyReferencedPages(referencedPage, i, level - 1));
            } else {
                referencedPage.erase();
            }
        }
    }


    private Page allocatePoolPage(final Page page, final int index) {
        final PageAllocation allocation = pagePool.allocate();
        editor.on(page, index * Long.BYTES, () -> editor.write(allocation.pageNumber));
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
        editor.setPage(dataDescriptor);
        editor.setPosition(position);
        return editor;
    }

    private void setFileSize(final long size) {
        descriptor(SIZE_POSITION).write(size);
    }

    String name() {
        return descriptor(NAME_POSITION).readString();
    }

    void setName(final String name) {

    }

    @Override
    public void write(final long offset, final byte source) {
        increaseSize(offset + 1);
        data.write(offset, source);
    }

    private void increaseSize(final long requiredSize) {
        if (fileSize() < requiredSize) {
            setFileSize(requiredSize);
        }
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        increaseSize(offset + length);
        data.write(offset, length, source, sourceOffset);

    }

    @Override
    public byte readByte(final long offset) {
        if (offset >= size()) {
            throw new EndOfFileException();
        }
        return data.readByte(offset);
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        if (offset > size() + length) {
            throw new EndOfFileException();
        }
        data.read(offset, length, destination, destinationOffset);
    }

    @Override
    public void erase(final long offset, final long length) {
        data.erase(offset, length);
    }
}
