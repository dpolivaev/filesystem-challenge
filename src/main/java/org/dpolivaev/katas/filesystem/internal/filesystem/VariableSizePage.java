package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.*;
import org.dpolivaev.katas.filesystem.internal.pool.PageAllocation;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.*;

class VariableSizePage implements Page {
    private final PagePool pagePool;
    private final PageEditor editor;
    private final Page startPage;
    private final Page dataDescriptor;
    private final ArbitraryCompositePage data;

    VariableSizePage(final PagePool pagePool, final PageEditor editor, final Page startPage) {
        this.pagePool = pagePool;
        this.editor = editor;
        this.startPage = startPage;
        final Pair<Page, Page> pagePair = startPage.split(DATA_POSITION);
        this.dataDescriptor = pagePair.first;
        final List<Page> pages = createPages(pagePair.second);
        data = new ArbitraryCompositePage(pages::get, pages.size());
    }

    @Override
    public long size() {
        return data.size();
    }

    @Override
    public void write(final long offset, final byte source) {
        data.write(offset, source);
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        data.write(offset, length, source, sourceOffset);
    }

    @Override
    public byte readByte(final long offset) {
        return data.readByte(offset);
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        data.read(offset, length, destination, destinationOffset);
    }

    @Override
    public void erase() {
        data.erase();
    }

    @Override
    public void erase(final long offset, final long length) {
        data.erase(offset, length);
    }

    @Override
    public Pair<Page, Page> split(final long position) {
        return data.split(position);
    }

    @Override
    public Page subpage(final long from, final long length) {
        return data.subpage(from, length);
    }

    void destroy() {
        final Page referencesPage = dataDescriptor.subpage(PAGE_REFERENCE_POSITION, PAGE_LEVEL_COUNT * Long.BYTES);
        for (int level = 0; level < PAGE_LEVEL_COUNT; level++) {
            destroy(referencesPage, level, level);
        }
        startPage.erase(DATA_POSITION, startPage.size() - DATA_POSITION);

    }

    private void destroy(final Page page, final int index, final int level) {
        final long pageNumber = editor.on(page, index * Long.BYTES, editor::readLong);
        if (pageNumber != 0) {
            final Page referencedPage = pagePool.pageAt(pageNumber);
            pagePool.release(pageNumber);
            editor.on(page, index * Long.BYTES, () -> editor.write(0L));
            if (level > 0) {
                IntStream.range(0, (int) referencedPage.size() / Long.BYTES).forEach(i -> destroy(referencedPage, i, level - 1));
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
}
