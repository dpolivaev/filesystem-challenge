package org.dpolivaev.katas.filesystem;

import java.util.UUID;

public interface Element {
    Directory parentDirectory();

    String name();

    UUID uuid();

    boolean exists();
}
