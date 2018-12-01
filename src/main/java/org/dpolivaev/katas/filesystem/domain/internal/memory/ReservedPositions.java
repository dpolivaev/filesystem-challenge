package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.util.PrimitiveIterator;
import java.util.Random;

class ReservedPositions {
    private final Memory memory;
    private final int blockSize;
    private final long availableBits;
    private final PrimitiveIterator.OfLong randomBitOffsets;

    ReservedPositions(final Memory memory, final int blockSize, final Random random, final long availableBits) {
        if (availableBits < 0 || availableBits > memory.size() * blockSize * Byte.SIZE)
            throw new IllegalArgumentException("Invalid availableBits");
        this.memory = memory;
        this.blockSize = blockSize;
        this.availableBits = availableBits;
        this.randomBitOffsets = random.longs(0, availableBits).iterator();
    }

    long reservePosition() {
        final long positionOffset = randomBitOffsets.nextLong();
        for (int counter = 0; counter < availableBits; counter++) {
            long position = positionOffset + counter;
            if (position >= availableBits)
                position -= availableBits;
            final int bitIndex = (int) (position & 0x7);
            final long bytePosition = (position >> 3);
            final long blockIndex = bytePosition / blockSize;
            final int byteIndex = (int) (bytePosition % blockSize);
            final DataBlock block = memory.at(blockIndex);
            final byte bits = block.getByte(byteIndex);
            final byte newBits = setBit(bits, bitIndex);
            if (bits != newBits) {
                block.set(byteIndex, newBits);
                return position;
            }
        }
        throw new OutOfMemoryException("No bits available");
    }

    private byte setBit(final byte bits, final int bitIndex) {
        return (byte) (bits | (1 << bitIndex));
    }

    void releasePosition(final long position) {
        final int bitIndex = (int) (position & 0x7);
        final long bytePosition = (position >> 3);
        final long blockIndex = bytePosition / blockSize;
        final int byteIndex = (int) (bytePosition % blockSize);
        final DataBlock block = memory.at(blockIndex);
        final byte bits = block.getByte(byteIndex);
        final byte newBits = unsetBit(bits, bitIndex);
        if (bits == newBits)
            throw new IllegalArgumentException("Bit was not set");
        block.set(byteIndex, newBits);
    }

    private byte unsetBit(final byte bits, final int bitIndex) {
        return (byte) (bits & ~(1 << bitIndex));
    }
}
