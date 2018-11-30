package org.dpolivaev.katas.filesystem.domain.internal.memory;

public final class SafeBlock implements DataBlock {
    private final DataBlock source;

    SafeBlock(DataBlock source) {
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
    public void put(long offset, byte source) {
        ensureValidOffset(offset);
        this.source.put(offset, source);
    }

    private void ensureValidOffset(long offset) {
        if(offset < 0 || offset >= size())
            throw new IllegalArgumentException("Invalid offset " + offset);
    }

    private void ensureValidArrayRange(byte[] source, long offset, long length) {
        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset " + offset);
        } else if (offset + length > source.length) {
            throw new IllegalArgumentException("Invalid length " + length);
        }
    }

    private void ensureValidLength(long offset, long length) {
        if(length < 0 || offset + length > size())
            throw new IllegalArgumentException("Invalid length " + length);
    }

    @Override
    public void put(long offset, long length, byte[] source, long sourceOffset) {
        ensureValidOffset(offset);
        ensureValidLength(offset, length);
        ensureValidArrayRange(source, sourceOffset, length);
        this.source.put(offset, length, source, sourceOffset);
    }

    @Override
    public byte getByte(long offset) {
        ensureValidOffset(offset);
        return source.getByte(offset);
    }

    @Override
    public void get(long offset, long length, byte[] destination, long destinationOffset) {
        ensureValidOffset(offset);
        ensureValidLength(offset, length);
        ensureValidArrayRange(destination, destinationOffset, length);
        source.get(offset, length, destination, destinationOffset);
    }

    @Override
    public Pair<DataBlock, DataBlock> split(long offset) {
        ensureValidOffset(offset);
        return source.split(offset);
    }

    @Override
    public SafeBlock safe() {
        return this;
    }
}
