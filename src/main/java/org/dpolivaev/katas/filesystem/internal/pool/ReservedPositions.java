package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.Pages;

import java.util.PrimitiveIterator;
import java.util.Random;

class ReservedPositions {
    private final Pages pages;
    private final long availablePositions;
    private final PrimitiveIterator.OfLong randomBitOffsets;
    private int bitIndex;
    private int byteIndex;
    private Page page;
    private byte bits;

    ReservedPositions(final Pages pages, final long availablePositions, final Random random) {
        assert availablePositions >= 0 && availablePositions <= pages.size() * pages.pageSize() * Byte.SIZE;
        this.pages = pages;
        this.availablePositions = availablePositions;
        this.randomBitOffsets = random.longs(0, availablePositions).iterator();
    }

    long reservePosition() {
        final long positionOffset = randomBitOffsets.nextLong();
        for (int counter = 0; counter < availablePositions; counter++) {
            long position = positionOffset + counter;
            if (position >= availablePositions)
                position -= availablePositions;
            findBit(position);
            final byte newBits = setBit(bits, bitIndex);
            if (bits != newBits) {
                page.write(byteIndex, newBits);
                return position;
            }
        }
        throw new OutOfMemoryException("No pages available");
    }

    void reservePosition(final long position) {
        findBit(position);
        final byte newBits = setBit(bits, bitIndex);
        if (bits != newBits) {
            page.write(byteIndex, newBits);
        } else
            throw new IllegalArgumentException("Position already reserved");
    }


    private void findBit(final long position) {
        bitIndex = (int) (position & 0x7);
        final long bytePosition = (position >> 3);
        final long pageIndex = bytePosition / pageSize();
        byteIndex = (int) (bytePosition % pageSize());
        page = pages.at(pageIndex);
        bits = page.readByte(byteIndex);
    }

    private int pageSize() {
        return pages.pageSize();
    }

    private byte setBit(final byte bits, final int bitIndex) {
        return (byte) (bits | (1 << bitIndex));
    }

    void releasePosition(final long position) {
        findBit(position);
        final byte newBits = unsetBit(bits, bitIndex);
        if (bits == newBits)
            throw new IllegalArgumentException("Bit was not set");
        page.write(byteIndex, newBits);
    }

    private byte unsetBit(final byte bits, final int bitIndex) {
        return (byte) (bits & ~(1 << bitIndex));
    }

    public boolean isReserved(final long position) {
        findBit(position);
        final byte newBits = unsetBit(bits, bitIndex);
        return newBits != bits;
    }
}
