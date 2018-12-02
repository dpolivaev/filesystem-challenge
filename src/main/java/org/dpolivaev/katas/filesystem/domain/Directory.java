package org.dpolivaev.katas.filesystem.domain;

import java.util.List;
import java.util.Optional;

public interface Directory extends Element {
    Optional<File> file(String name);
    File createFile(String name);
    void deleteFile(String name);

    Optional<Directory> directory(String name);
    Directory createDirectory(String name);
    void deleteDirectory(String name);

    List<String> files();

    List<String> directories();
}
