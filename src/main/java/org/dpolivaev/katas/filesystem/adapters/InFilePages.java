package org.dpolivaev.katas.filesystem.adapters;

import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Pages;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

class InFilePages implements Pages {

    private final FileChannel fileChannel;
    private final long size;

    InFilePages(final File file, final long maximalFileSize) {
        try {
            this.fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.SPARSE, StandardOpenOption.CREATE);
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
        return FilePage.PAGE_SIZE;
    }

    @Override
    public Page at(final long position) {
        try {
            return new FilePage(fileChannel, position);
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
