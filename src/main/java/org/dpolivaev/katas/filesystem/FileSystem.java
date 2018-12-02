package org.dpolivaev.katas.filesystem;

import java.io.Closeable;

public interface FileSystem extends Closeable {
    Directory root();

    @Override
    default void close() {
    }

}
