package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.concurrent.locks.ReadWriteLock;

public class ConcurrentPagedFile extends PagedFile {
    private final ReadWriteLock readWriteLock;

    ConcurrentPagedFile(final FilePage filePage, final ConcurrentPagedDirectory parentDirectory) {
        super(filePage, parentDirectory);
        readWriteLock = LockFactory.lock(uuid());
    }

    @Override
    public boolean exists() {
        readWriteLock.readLock().lock();
        try {
            return super.exists();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public String name() {
        readWriteLock.readLock().lock();
        try {
            return super.name();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long size() {
        readWriteLock.readLock().lock();
        try {
            return super.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void truncate() {
        readWriteLock.writeLock().lock();
        try {
            super.truncate();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void setPosition(final long position) {
        readWriteLock.readLock().lock();
        try {
            super.setPosition(position);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long getPosition() {
        readWriteLock.readLock().lock();
        try {
            return super.getPosition();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(final byte source) {
        readWriteLock.readLock().lock();
        try {
            super.write(source);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        readWriteLock.readLock().lock();
        try {
            super.write(source, sourceOffset, length);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public byte readByte() {
        readWriteLock.readLock().lock();
        try {
            return super.readByte();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        readWriteLock.readLock().lock();
        try {
            super.read(destination, destinationOffset, length);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(final long source) {
        readWriteLock.readLock().lock();
        try {
            super.write(source);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(final int source) {
        readWriteLock.readLock().lock();
        try {
            super.write(source);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(final byte[] source) {
        readWriteLock.readLock().lock();
        try {
            super.write(source);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void write(final String source) {
        readWriteLock.readLock().lock();
        try {
            super.write(source);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int readInt() {
        readWriteLock.readLock().lock();
        try {
            return super.readInt();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public long readLong() {
        readWriteLock.readLock().lock();
        try {
            return super.readLong();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public String readString() {
        readWriteLock.readLock().lock();
        try {
            return super.readString();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void read(final byte[] destination) {
        readWriteLock.readLock().lock();
        try {
            super.read(destination);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
