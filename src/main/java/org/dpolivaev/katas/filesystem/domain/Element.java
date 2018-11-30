package org.dpolivaev.katas.filesystem.domain;

public interface Element {
    Directory parentDirectory();
    String name();
}
