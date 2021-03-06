package org.dpolivaev.katas.filesystem.internal.pages;

import java.util.Arrays;
import java.util.function.Supplier;

public class LazyPage implements Page {
    private Page suppliedPage;
    private final Supplier<Page> pageSupplier;
    private final long pageSize;

    public LazyPage(final Supplier<Page> pageSupplier, final long pageSize) {
        this.pageSupplier = pageSupplier;
        this.pageSize = pageSize;
    }

    private Page writingPage() {
        if (suppliedPage == null) {
            suppliedPage = pageSupplier.get();
            assert suppliedPage.size() == size();
        }
        return suppliedPage;
    }

    @Override
    public long size() {
        return pageSize;
    }

    @Override
    public void write(final long offset, final byte source) {
        if (source != 0 || suppliedPage != null)
            writingPage().write(offset, source);
    }

    @Override
    public void write(final long offset, final int length, final byte[] source, final int sourceOffset) {
        if (suppliedPage != null || containsNonZeroBytes(source, sourceOffset, sourceOffset + length))
            writingPage().write(offset, length, source, sourceOffset);
    }

    private boolean containsNonZeroBytes(final byte[] source, final int sourceOffset, final int end) {
        for (int i = sourceOffset; i < end; i++)
            if (source[i] != 0)
                return true;
        return false;
    }

    @Override
    public byte readByte(final long offset) {
        if (suppliedPage != null)
            return suppliedPage.readByte(offset);
        else
            return 0;
    }

    @Override
    public void read(final long offset, final int length, final byte[] destination, final int destinationOffset) {
        if (suppliedPage != null)
            suppliedPage.read(offset, length, destination, destinationOffset);
        else
            Arrays.fill(destination, destinationOffset, destinationOffset + length, (byte) 0);
    }

    @Override
    public void erase(final long offset, final long length) {
        if (suppliedPage != null)
            suppliedPage.erase(offset, length);
    }
}
