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

    static FileSystem createThreadSafe(final File file, final long size) {
        return FileSystemFactory.INSTANCE.createThreadSafe(file, size);
    }

    static FileSystem createThreadSafe(final String fileName, final long size) {
        return FileSystemFactory.INSTANCE.createThreadSafe(fileName, size);
    }

    static FileSystem openThreadSafe(final File file) {
        return FileSystemFactory.INSTANCE.openThreadSafe(file);
    }

    static FileSystem openThreadSafe(final String fileName) {
        return FileSystemFactory.INSTANCE.openThreadSafe(fileName);
    }

    Directory root();

    long maximumSupportedFileSize();

    @Override
    default void close() {
    }

}
