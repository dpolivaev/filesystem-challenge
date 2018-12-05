package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.Pages;

import java.util.PrimitiveIterator;
import java.util.Random;

class ReservedPositions {
    private final PageEditor editor;
    private final Pages pages;
    private final long availablePositions;
    private final PrimitiveIterator.OfLong randomBitOffsets;
    private int bitIndex;
    private int longIndex;
    private Page page;
    private long bits;

    ReservedPositions(final Pages pages, final long availablePositions, final Random random) {
        assert availablePositions >= 0 && availablePositions <= pages.size() * pages.pageSize() * Byte.SIZE;
        this.pages = pages;
        this.availablePositions = availablePositions;
        this.randomBitOffsets = random.longs(0, availablePositions).iterator();
        editor = new PageEditor();
    }

    long reservePosition() {
        final long positionOffset = randomBitOffsets.nextLong();
        for (int counter = 0; counter < availablePositions; counter++) {
            long position = positionOffset + counter;
            if (position >= availablePositions)
                position -= availablePositions;
            findBit(position);
            final long newBits = setBit(bits, bitIndex);
            if (bits != newBits) {
                updateReservedBitMap(newBits);
                return position;
            }
        }
        throw new OutOfMemoryException("No pages available");
    }

    private void updateReservedBitMap(final long newBits) {
        editor.on(page, longIndex, () -> editor.write(newBits));
    }

    void reservePosition(final long position) {
        findBit(position);
        final long newBits = setBit(bits, bitIndex);
        if (bits != newBits) {
            updateReservedBitMap(newBits);
        } else
            throw new IllegalArgumentException("Position already reserved");
    }


    private void findBit(final long bitPosition) {
        bitIndex = (int) (bitPosition & (Long.SIZE - 1));
        final long longBytePosition = (bitPosition / Long.SIZE) * Long.BYTES;
        final long pageIndex = longBytePosition / pageSize();
        longIndex = (int) (longBytePosition % pageSize());
        page = pages.at(pageIndex);
        bits = readReservedBits();
    }

    private long readReservedBits() {
        return editor.on(page, longIndex, editor::readLong);
    }

    private int pageSize() {
        return pages.pageSize();
    }

    private long setBit(final long bits, final int bitIndex) {
        return bits | (1L << bitIndex);
    }

    void releasePosition(final long position) {
        findBit(position);
        final long newBits = unsetBit(bits, bitIndex);
        if (bits == newBits)
            throw new IllegalArgumentException("Bit was not set");
        updateReservedBitMap(newBits);
    }

    private long unsetBit(final long bits, final int bitIndex) {
        return bits & ~(1L << bitIndex);
    }

    boolean isReserved(final long position) {
        findBit(position);
        final long newBits = unsetBit(bits, bitIndex);
        return newBits != bits;
    }
}
