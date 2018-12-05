package org.dpolivaev.katas.filesystem;

import org.dpolivaev.katas.filesystem.internal.persistence.FileSystemFactory;

import java.io.Closeable;
import java.io.File;

public interface FileSystem extends Closeable {

    static FileSystem create(final File file, final long size) {
        return FileSystemFactory.INSTANCE.create(file, size);
    }

    static FileSystem open(final File file, final long size) {
        return FileSystemFactory.INSTANCE.open(file, size);
    }

    Directory root();

    @Override
    default void close() {
    }

}
