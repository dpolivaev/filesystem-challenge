package org.dpolivaev.katas.filesystem.internal.pages;

public interface Pages {
    long size();

    int pageSize();

    Page at(long position);

    default void close() {
    }

}

