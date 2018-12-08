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

    static FileSystem open(final File file) {
        return FileSystemFactory.INSTANCE.open(file);
    }

    static FileSystem open(final String fileName) {
        return FileSystemFactory.INSTANCE.open(fileName);
    }

    static FileSystem createConcurrent(final File file, final long size) {
        return FileSystemFactory.INSTANCE.createConcurrent(file, size);
    }

    static FileSystem createConcurrent(final String fileName, final long size) {
        return FileSystemFactory.INSTANCE.createConcurrent(fileName, size);
    }

    static FileSystem openConcurrent(final File file) {
        return FileSystemFactory.INSTANCE.openConcurrent(file);
    }

    static FileSystem openConcurrent(final String fileName) {
        return FileSystemFactory.INSTANCE.openConcurrent(fileName);
    }

    Directory root();

    long maximumSupportedFileSize();

    @Override
    default void close() {
    }

}
