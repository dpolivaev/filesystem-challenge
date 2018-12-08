package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.concurrent.locks.Lock;

public class ConcurrentPagedFile extends PagedFile {
    private final Lock lock;

    ConcurrentPagedFile(final FilePage filePage, final ConcurrentPagedDirectory parentDirectory, final Lock lock) {
        super(filePage, parentDirectory);
        this.lock = lock;
    }

    @Override
    public boolean exists() {
        lock.lock();
        try {
            return super.exists();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String name() {
        lock.lock();
        try {
            return super.name();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long size() {
        lock.lock();
        try {
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteContent() {
        lock.lock();
        try {
            super.deleteContent();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(final byte source) {
        lock.lock();
        try {
            super.write(source);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(final byte[] source, final int sourceOffset, final int length) {
        lock.lock();
        try {
            super.write(source, sourceOffset, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte readByte() {
        lock.lock();
        try {
            return super.readByte();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void read(final byte[] destination, final int destinationOffset, final int length) {
        lock.lock();
        try {
            super.read(destination, destinationOffset, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(final long source) {
        lock.lock();
        try {
            super.write(source);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(final int source) {
        lock.lock();
        try {
            super.write(source);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(final byte[] source) {
        lock.lock();
        try {
            super.write(source);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void write(final String source) {
        lock.lock();
        try {
            super.write(source);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int readInt() {
        lock.lock();
        try {
            return super.readInt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long readLong() {
        lock.lock();
        try {
            return super.readLong();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String readString() {
        lock.lock();
        try {
            return super.readString();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void read(final byte[] destination) {
        lock.lock();
        try {
            super.read(destination);
        } finally {
            lock.unlock();
        }
    }
}
