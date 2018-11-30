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

    private void ensureValidPosition(final long position) {
        if (position < 0 || position >= size())
            throw new IllegalArgumentException("Invalid offset " + position);
    }

    @Override
    public DataBlock at(final long position) {
        ensureValidPosition(position);
        return source.at(position);
    }

    @Override
    public Pair<Memory, Memory> split(final long position) {
        ensureValidPosition(position);
        final Pair<Memory, Memory> pair = source.split(position);
        return new Pair<>(pair.first.safe(), pair.second.safe());
    }

    @Override
    public Memory safe() {
        return this;
    }
}
