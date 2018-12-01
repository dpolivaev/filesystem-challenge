package org.dpolivaev.katas.filesystem.domain.internal.memory;

final class SafePage implements Page {
    private final Page source;

    SafePage(final Page source) {
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
    public void write(final long offset, final byte source) {
        ensureValidOffset(offset);
        this.source.write(offset, source);
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
    public void write(final long offset, final long length, final byte[] source, final long sourceOffset) {
        ensureValidOffset(offset);
        ensureValidLength(offset, length);
        ensureValidArrayRange(source, sourceOffset, length);
        this.source.write(offset, length, source, sourceOffset);
    }

    @Override
    public byte readByte(final long offset) {
        ensureValidOffset(offset);
        return source.readByte(offset);
    }

    @Override
    public void read(final long offset, final long length, final byte[] destination, final long destinationOffset) {
        ensureValidOffset(offset);
        ensureValidLength(offset, length);
        ensureValidArrayRange(destination, destinationOffset, length);
        source.read(offset, length, destination, destinationOffset);
    }

    @Override
    public Pair<Page, Page> split(final long offset) {
        ensureValidOffset(offset);
        final Pair<Page, Page> pair = source.split(offset);
        return new Pair<>(pair.first.safe(), pair.second.safe());
    }

    @Override
    public SafePage safe() {
        return this;
    }
}
