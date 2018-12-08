package org.dpolivaev.katas.filesystem.internal.pages;

public interface Pages {
    long pageCount();

    int pageSize();

    Page descriptorPage();

    Page at(long position);

    default void close() {
    }

}

