package org.dpolivaev.katas.filesystem.domain.internal.memory;

import java.util.Random;

public class PagePool {
    private final ReservedPositions reservations;
    private final Pages pages;
    private final long reservationPages;

    public PagePool(final Pages pages, final Random random) {
        final int pageSize = pages.pageSize();
        final long availablePages = pages.size() * (pageSize * Byte.SIZE - 1) / (pageSize * Byte.SIZE);
        final long bytesForReservations = (availablePages + Byte.SIZE - 1) / Byte.SIZE;
        this.reservationPages = (bytesForReservations + pageSize - 1) / pageSize;
        this.reservations = new ReservedPositions(pages, availablePages, random);
        this.pages = pages;
    }

    public int pageSize() {
        return pages.pageSize();
    }

    Page reserve() {
        return pages.at(reservationPages + reservations.reservePosition());
    }

    void release(final Page page) {
        reservations.releasePosition(page.pageNumber() - reservationPages);
    }

}
