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

    public PageAllocation allocate() {
        final long pageNumber = reservations.reservePosition();
        return new PageAllocation(pages.at(reservationPages + pageNumber), pageNumber + 1);
    }

    public Page allocate(final long pageNumber) {
        reservations.reservePosition(pageNumber - 1);
        return pages.at(reservationPages + pageNumber - 1);
    }

    public Page at(final long pageNumber) {
        if (reservations.isReserved(pageNumber - 1))
            return pages.at(reservationPages + pageNumber - 1);
        else
            throw new IllegalArgumentException("Page not reserved");
    }

    public void release(final long pageNumber) {
        reservations.releasePosition(pageNumber - 1);
    }

    public boolean containsPage(final long pageNumber) {
        return reservations.isReserved(pageNumber - 1);
    }
}
