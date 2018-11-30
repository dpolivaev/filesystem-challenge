package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.util.PrimitiveIterator;
import java.util.Random;

class ReservedBits {
    private static final int NOT_AVAILABLE = -1;
    private final Memory memory;
    private final int blockSize;
    private final PrimitiveIterator.OfLong randomBlockOffsets;
    private final PrimitiveIterator.OfInt randomByteOffsets;

    ReservedBits(final Memory memory, final int blockSize, final Random random) {
        this.memory = memory;
        this.blockSize = blockSize;
        this.randomBlockOffsets = random.longs(0, memory.size()).iterator();
        this.randomByteOffsets = random.ints(0, blockSize).iterator();
    }

    long reserveBit() {
        final long blockOffset = randomBlockOffsets.nextLong();
        final long blockNumber = memory.size();
        for (long blockCount = 0; blockCount < blockNumber; blockCount++) {
            final long blockIndex = getPosition(blockOffset, blockCount, blockNumber);
            final DataBlock block = memory.at(blockIndex);
            if (block.size() != blockSize)
                throw new RuntimeException("Invalid block size");
            final long positionInBlock = reserveBit(block);
            if(positionInBlock != NOT_AVAILABLE)
                return blockIndex * blockSize * Byte.SIZE + positionInBlock;
        }
        throw new OutOfMemoryException("No bits available");
    }

    private long getPosition(final long blockOffset, final long blockIndex, final long maximum) {
        final long position = blockIndex + blockOffset;
        return position % maximum;
    }

    private long reserveBit(final DataBlock block) {
        final int byteOffset = randomByteOffsets.nextInt();
        for (long byteCounter = 0; byteCounter < blockSize; byteCounter++) {
            final long byteIndex = getPosition(byteOffset, byteCounter, blockSize);
            final byte candidate = block.getByte(byteIndex);
            if(candidate != NOT_AVAILABLE) {
                return reserveAvailableBit(block, byteIndex, candidate);
            }
        }
        return NOT_AVAILABLE;
    }

    private long reserveAvailableBit(final DataBlock block, final long byteIndex, final byte candidate) {
        int modificaton = 1;
        for(int bitPosition = 0; bitPosition < Byte.SIZE; bitPosition++) {
            final int modifiedBit = (candidate | modificaton);
            if(modifiedBit != candidate) {
                block.put(byteIndex, (byte)modifiedBit);
                return byteIndex * Byte.SIZE + bitPosition;
            }
            else
                modificaton <<= 1;
        }
        throw new IllegalArgumentException("No available bit found");
    }

    void releaseBit(final long position) {
        final int bitIndex = (int) (position & 0x7);
        final long bytePosition = (position >> 3);
        final long blockIndex = bytePosition / blockSize;
        final int byteIndex = (int) (bytePosition % blockSize);

        final DataBlock block = memory.at(blockIndex);
        final byte bits = block.getByte(byteIndex);
        final byte newBits = releaseBit(bits, bitIndex);
        if (bits == newBits)
            throw new IllegalStateException("Bit was not set");
        block.put(byteIndex, newBits);

    }

    private byte releaseBit(final byte bits, final int bitIndex) {
        return (byte) (bits & ~(1 << bitIndex));
    }
}
