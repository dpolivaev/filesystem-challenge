package org.dpolivaev.katas.filesystem.domain.internal.memory;

class SafeMemory implements Memory {

    private final Memory source;

    SafeMemory(final Memory source) {
        this.source = source;
    }

    @Override
    public long size() {
        return source.size();
    }

    @Override
    public int pageSize() {
        return source.pageSize();
    }

    private void ensureValidPosition(final long position) {
        if (position < 0 || position >= size())
            throw new IllegalArgumentException("Invalid offset " + position);
    }

    @Override
    public Page at(final long position) {
        ensureValidPosition(position);
        return source.at(position);
    }

    @Override
    public Memory safe() {
        return this;
    }
}
