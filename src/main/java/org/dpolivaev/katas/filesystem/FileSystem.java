package org.dpolivaev.katas.filesystem;

import org.dpolivaev.katas.filesystem.internal.persistence.FileSystemFactory;

import java.io.Closeable;
import java.io.File;

public interface FileSystem extends Closeable {

    static FileSystem create(final File file, final long size) {
        return FileSystemFactory.INSTANCE.create(file, size);
    }

    static FileSystem create(final String fileName, final long size) {
        return FileSystemFactory.INSTANCE.create(fileName, size);
    }

    static FileSystem createConcurrent(final File file, final long size) {
        return FileSystemFactory.INSTANCE.createConcurrent(file, size);
    }

    static FileSystem createConcurrent(final String fileName, final long size) {
        return FileSystemFactory.INSTANCE.createConcurrent(fileName, size);
    }

    static FileSystem open(final File file, final long size) {
        return FileSystemFactory.INSTANCE.open(file, size);
    }

    static FileSystem open(final String fileName, final long size) {
        return FileSystemFactory.INSTANCE.open(fileName, size);
    }

    static FileSystem openConcurrent(final File file, final long size) {
        return FileSystemFactory.INSTANCE.openConcurrent(file, size);
    }

    static FileSystem openConcurrent(final String fileName, final long size) {
        return FileSystemFactory.INSTANCE.openConcurrent(fileName, size);
    }

    Directory root();

    @Override
    default void close() {
    }

}
