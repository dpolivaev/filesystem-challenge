package org.dpolivaev.katas.filesystem.domain;

import java.util.List;

public interface Directory extends Element {
    File file(String name);
    File createFile(String name);
    void deleteFile(String name);
    Directory directory(String name);
    Directory createDirectory(String name);
    void deleteDirectory(String name);
    List<File> files();
    List<Directory> directories();
}
