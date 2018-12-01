package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.util.PrimitiveIterator;
import java.util.Random;

class ReservedPositions {
    private final Memory memory;
    private final long availablePositions;
    private final PrimitiveIterator.OfLong randomBitOffsets;

    ReservedPositions(final Memory memory, final long availablePositions, final Random random) {
        if (availablePositions < 0 || availablePositions > memory.size() * memory.pageSize() * Byte.SIZE)
            throw new IllegalArgumentException("Invalid availablePositions");
        this.memory = memory;
        this.availablePositions = availablePositions;
        this.randomBitOffsets = random.longs(0, availablePositions).iterator();
    }

    long reservePosition() {
        final long positionOffset = randomBitOffsets.nextLong();
        for (int counter = 0; counter < availablePositions; counter++) {
            long position = positionOffset + counter;
            if (position >= availablePositions)
                position -= availablePositions;
            final int bitIndex = (int) (position & 0x7);
            final long bytePosition = (position >> 3);
            final long pageIndex = bytePosition / pageSize();
            final int byteIndex = (int) (bytePosition % pageSize());
            final Page page = memory.at(pageIndex);
            final byte bits = page.readByte(byteIndex);
            final byte newBits = setBit(bits, bitIndex);
            if (bits != newBits) {
                page.write(byteIndex, newBits);
                return position;
            }
        }
        throw new OutOfMemoryException("No bits available");
    }

    private int pageSize() {
        return memory.pageSize();
    }

    private byte setBit(final byte bits, final int bitIndex) {
        return (byte) (bits | (1 << bitIndex));
    }

    void releasePosition(final long position) {
        final int bitIndex = (int) (position & 0x7);
        final long bytePosition = (position >> 3);
        final long pageIndex = bytePosition / pageSize();
        final int byteIndex = (int) (bytePosition % pageSize());
        final Page page = memory.at(pageIndex);
        final byte bits = page.readByte(byteIndex);
        final byte newBits = unsetBit(bits, bitIndex);
        if (bits == newBits)
            throw new IllegalArgumentException("Bit was not set");
        page.write(byteIndex, newBits);
    }

    private byte unsetBit(final byte bits, final int bitIndex) {
        return (byte) (bits & ~(1 << bitIndex));
    }
}
