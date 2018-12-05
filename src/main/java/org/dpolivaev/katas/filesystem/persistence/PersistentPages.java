package org.dpolivaev.katas.filesystem.persistence;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.Pages;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;

class PersistentPages implements Pages {

    private final FileChannel fileChannel;
    private final long size;

    PersistentPages(final File file, final long maximalFileSize, final OpenOption... options) {
        try {
            this.fileChannel = FileChannel.open(file.toPath(), options);
            fileChannel.tryLock();
            this.size = maximalFileSize / pageSize();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public int pageSize() {
        return PersistentPage.PAGE_SIZE;
    }

    @Override
    public Page at(final long position) {
        try {
            return new PersistentPage(fileChannel, position);
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
