package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.Pages;

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
        return pageAt(pageNumber);
    }

    public boolean isAllocated(final long pageNumber) {
        return reservations.isReserved(pageNumber - 1);
    }

    public Page pageAt(final long pageNumber) {
        assert isAllocated(pageNumber);
        return pages.at(reservationPages + pageNumber - 1);
    }

    public void release(final long pageNumber) {
        reservations.releasePosition(pageNumber - 1);
    }

    public boolean containsPage(final long pageNumber) {
        return isAllocated(pageNumber);
    }

    public void close() {
        pages.close();
    }
}
