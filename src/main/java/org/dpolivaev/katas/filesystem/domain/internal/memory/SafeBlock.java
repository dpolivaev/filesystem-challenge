package org.dpolivaev.katas.filesystem.domain.internal.memory;

public final class SafeBlock implements DataBlock {
    private final DataBlock source;

    SafeBlock(final DataBlock source) {
        this.source = source;
    }

    @Override
    public long position() {
        return source.position();
    }

    @Override
    public long size() {
        return source.size();
    }

    @Override
    public void set(final long offset, final byte source) {
        ensureValidOffset(offset);
        this.source.set(offset, source);
    }

    private void ensureValidOffset(final long offset) {
        if(offset < 0 || offset >= size())
            throw new IllegalArgumentException("Invalid offset " + offset);
    }

    private void ensureValidArrayRange(final byte[] source, final long offset, final long length) {
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset " + offset);
        } else if (offset + length > source.length) {
            throw new IllegalArgumentException("Invalid length " + length);
        }
    }

    private void ensureValidLength(final long offset, final long length) {
        if(length < 0 || offset + length > size())
            throw new IllegalArgumentException("Invalid length " + length);
    }

    @Override
    public void set(final long offset, final long length, final byte[] source, final long sourceOffset) {
        ensureValidOffset(offset);
        ensureValidLength(offset, length);
        ensureValidArrayRange(source, sourceOffset, length);
        this.source.set(offset, length, source, sourceOffset);
    }

    @Override
    public byte getByte(final long offset) {
        ensureValidOffset(offset);
        return source.getByte(offset);
    }

    @Override
    public void get(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        ensureValidOffset(offset);
        ensureValidLength(offset, length);
        ensureValidArrayRange(destination, destinationOffset, length);
        source.get(offset, length, destination, destinationOffset);
    }

    @Override
    public Pair<DataBlock, DataBlock> split(final long offset) {
        ensureValidOffset(offset);
        return source.split(offset);
    }

    @Override
    public SafeBlock safe() {
        return this;
    }
}
