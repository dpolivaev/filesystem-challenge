package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pool.ConcurrentPagePool;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class ConcurrentPagedDirectory extends PagedDirectory {
    private final Lock lock;
    private final ConcurrentPagePool pagePool;

    ConcurrentPagedDirectory(final ConcurrentPagePool pagePool, final Page directoryData, final Directory parentDirectory) {
        super(pagePool, directoryData, parentDirectory);
        this.pagePool = pagePool;
        lock = LockFactory.lock(uuid());
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
    public boolean exists() {
        lock.lock();
        try {
            return super.exists();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<File> file(final String name) {
        lock.lock();
        try {
            return super.file(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected File toFile(final FilePage page) {
        return new ConcurrentPagedFile(page, this);
    }

    @Override
    protected Directory toDirectory(final Page page) {
        return new ConcurrentPagedDirectory(pagePool, page, this);
    }

    @Override
    public File createFile(final String name) {
        lock.lock();
        try {
            return super.createFile(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteFile(final String name) {
        lock.lock();
        try {
            super.deleteFile(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> files() {
        lock.lock();
        try {
            return super.files();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> directories() {
        lock.lock();
        try {
            return super.directories();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Directory> directory(final String name) {
        lock.lock();
        try {
            return super.directory(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Directory createDirectory(final String name) {
        lock.lock();
        try {
            return super.createDirectory(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteDirectory(final String name) {
        lock.lock();
        try {
            super.deleteDirectory(name);
        } finally {
            lock.unlock();
        }
    }
}
