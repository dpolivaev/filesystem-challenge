package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pool.ConcurrentPagePool;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;

public class ConcurrentPagedDirectory extends PagedDirectory {
    private final ReadWriteLock readWriteLock;
    private final ConcurrentPagePool pagePool;

    ConcurrentPagedDirectory(final ConcurrentPagePool pagePool, final Page directoryData, final Directory parentDirectory) {
        super(pagePool, directoryData, parentDirectory);
        this.pagePool = pagePool;
        readWriteLock = LockFactory.lock(uuid());
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
    public boolean exists() {
        readWriteLock.readLock().lock();
        try {
            return super.exists();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<File> file(final String name) {
        readWriteLock.readLock().lock();
        try {
            return super.file(name);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    protected File toFile(final Page page) {
        return new ConcurrentPagedFile(new FilePage(pagePool, page), this);
    }

    @Override
    protected Directory toDirectory(final Page page) {
        return new ConcurrentPagedDirectory(pagePool, page, this);
    }

    @Override
    public File createFile(final String name) {
        readWriteLock.writeLock().lock();
        try {
            return super.createFile(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteFile(final String name) {
        readWriteLock.writeLock().lock();
        try {
            super.deleteFile(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<String> files() {
        readWriteLock.readLock().lock();
        try {
            return super.files();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<String> directories() {
        readWriteLock.readLock().lock();
        try {
            return super.directories();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Optional<Directory> directory(final String name) {
        readWriteLock.readLock().lock();
        try {
            return super.directory(name);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Directory createDirectory(final String name) {
        readWriteLock.writeLock().lock();
        try {
            return super.createDirectory(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteDirectory(final String name) {
        readWriteLock.writeLock().lock();
        try {
            super.deleteDirectory(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
