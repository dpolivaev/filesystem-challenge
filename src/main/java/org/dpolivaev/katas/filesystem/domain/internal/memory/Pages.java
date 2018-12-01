package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Pages {
    long size();

    int pageSize();

    Page at(long position);

}

