package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.concurrent.locks.ReadWriteLock;

public class ConcurrentPagedFile extends PagedFile {
    private final ReadWriteLock lock;

    ConcurrentPagedFile(final FilePage filePage, final ConcurrentPagedDirectory parentDirectory) {
        super(filePage, parentDirectory);
        lock = LockFactory.readWriteLock(uuid());
    }

    @Override
    public boolean exists() {
        lock.readLock().lock();
        try {
            return super.exists();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String name() {
        lock.readLock().lock();
        try {
            return super.name();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public long size() {
        lock.readLock().lock();
        try {
            return super.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void truncate() {
        lock.writeLock().lock();
        try {
            super.truncate();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(final byte source) {
        lock.writeLock().lock();
        try {
            super.write(source);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        lock.writeLock().lock();
        try {
            super.write(source, sourceOffset, length);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public byte readByte() {
        lock.readLock().lock();
        try {
            return super.readByte();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        lock.readLock().lock();
        try {
            super.read(destination, destinationOffset, length);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(final long source) {
        lock.writeLock().lock();
        try {
            super.write(source);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(final int source) {
        lock.writeLock().lock();
        try {
            super.write(source);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(final byte[] source) {
        lock.writeLock().lock();
        try {
            super.write(source);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(final String source) {
        lock.writeLock().lock();
        try {
            super.write(source);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int readInt() {
        lock.readLock().lock();
        try {
            return super.readInt();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public long readLong() {
        lock.readLock().lock();
        try {
            return super.readLong();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String readString() {
        lock.readLock().lock();
        try {
            return super.readString();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void read(final byte[] destination) {
        lock.readLock().lock();
        try {
            super.read(destination);
        } finally {
            lock.readLock().unlock();
        }
    }
}
