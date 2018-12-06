package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.Pages;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentPagePool extends PagePool {
    private final ReadWriteLock readWriteLock;

    public ConcurrentPagePool(final Pages pages, final Random random) {
        super(pages, random);
        readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public PageAllocation allocate() {
        readWriteLock.writeLock().lock();
        try {
            return super.allocate();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Page allocate(final long pageNumber) {
        readWriteLock.writeLock().lock();
        try {
            return super.allocate(pageNumber);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isAllocated(final long pageNumber) {
        readWriteLock.readLock().lock();
        try {
            return super.isAllocated(pageNumber);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Page pageAt(final long pageNumber) {
        readWriteLock.readLock().lock();
        try {
            return super.pageAt(pageNumber);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void release(final long pageNumber) {
        readWriteLock.writeLock().lock();
        try {
            super.release(pageNumber);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        readWriteLock.writeLock().lock();
        try {
            super.close();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
