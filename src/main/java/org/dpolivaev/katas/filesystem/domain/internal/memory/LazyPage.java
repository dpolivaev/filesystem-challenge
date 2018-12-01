package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.util.function.Supplier;

public class LazyPage implements Page {
    private Page suppliedPage;
    private final Supplier<Page> pageSupplier;
    private final long pageSize;

    public LazyPage(final Supplier<Page> pageSupplier, final long pageSize) {
        this.pageSupplier = pageSupplier;
        this.pageSize = pageSize;
    }

    private Page suppliedPage() {
        if (suppliedPage == null)
            suppliedPage = pageSupplier.get();
        return suppliedPage;
    }

    @Override
    public long size() {
        return pageSize;
    }

    @Override
    public void write(final long offset, final byte source) {
        suppliedPage().write(offset, source);
    }

    @Override
    public void write(final long offset, final long length, final byte[] source, final long sourceOffset) {
        suppliedPage().write(offset, length, source, sourceOffset);
    }

    @Override
    public byte readByte(final long offset) {
        return suppliedPage().readByte(offset);
    }

    @Override
    public void read(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        suppliedPage().read(offset, length, destination, destinationOffset);
    }
}
