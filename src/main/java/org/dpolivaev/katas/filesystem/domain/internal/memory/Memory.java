package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Memory extends Splittable<Memory>{
    long blockCount();
    DataBlock at(long position);
}
