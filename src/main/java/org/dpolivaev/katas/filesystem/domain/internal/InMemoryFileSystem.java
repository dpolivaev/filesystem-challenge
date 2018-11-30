package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.domain.Directory;
import org.dpolivaev.katas.filesystem.domain.FileSystem;

public class InMemoryFileSystem implements FileSystem {
    @Override
    public Directory root() {
        return null;
    }
}
