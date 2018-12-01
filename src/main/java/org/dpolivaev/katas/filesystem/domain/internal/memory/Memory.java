package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Memory {
    long size();

    int pageSize();

    Page at(long position);


    default Memory safe() {
        return new SafeMemory(this);
    }
}

