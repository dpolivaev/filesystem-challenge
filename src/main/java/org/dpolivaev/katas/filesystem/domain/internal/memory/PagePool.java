package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.util.Random;

public class PagePool {
    private final ReservedPositions reservations;
    private final Memory pages;
    private final long reservationPages;

    public PagePool(final Memory memory, final Random random) {
        final int pageSize = memory.pageSize();
        final long availablePages = memory.size() * (pageSize * Byte.SIZE - 1) / (pageSize * Byte.SIZE);
        final long bytesForReservations = (availablePages + Byte.SIZE - 1) / Byte.SIZE;
        this.reservationPages = (bytesForReservations + pageSize - 1) / pageSize;
        this.reservations = new ReservedPositions(memory, availablePages, random);
        this.pages = memory;
    }

    Page reserve() {
        return pages.at(reservationPages + reservations.reservePosition());
    }

    void release(final Page page) {
        reservations.releasePosition(page.position() - reservationPages);
    }

}
