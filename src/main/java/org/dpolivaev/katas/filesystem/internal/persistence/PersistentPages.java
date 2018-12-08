package org.dpolivaev.katas.filesystem.internal.persistence;

import org.dpolivaev.katas.filesystem.IORuntimeException;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.Pages;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;

class PersistentPages implements Pages {

    private final FileChannel fileChannel;
    private long pageCount;
    private final Page descriptorPage;

    PersistentPages(final File file, final long maximalFileSize, final OpenOption... options) {
        try {
            this.fileChannel = FileChannel.open(file.toPath(), options);
            fileChannel.tryLock();
            descriptorPage = new PersistentPage(fileChannel, 0).subpage(0, FileSystemFactory.DESCRIPTOR_SIZE);
            this.pageCount = requiredPageCount(maximalFileSize);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private long requiredPageCount(final long maximalFileSize) {
        final long pageCount = maximalFileSize == 0 ? 0 : (maximalFileSize - descriptorPage.size()) / pageSize();
        assert pageCount >= 0;
        return pageCount;
    }

    void setMaximalFileSize(final long maximalFileSize) {
        pageCount = requiredPageCount(maximalFileSize);
    }

    @Override
    public long pageCount() {
        return pageCount;
    }

    @Override
    public int pageSize() {
        return PersistentPage.PAGE_SIZE;
    }

    @Override
    public Page descriptorPage() {
        return descriptorPage;
    }

    @Override
    public Page at(final long position) {
        try {
            return new PersistentPage(fileChannel, FileSystemFactory.DESCRIPTOR_SIZE + PersistentPage.PAGE_SIZE * position);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            fileChannel.close();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
