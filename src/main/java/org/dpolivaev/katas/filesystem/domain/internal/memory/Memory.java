package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Memory {
    DataBlock at(long number);
}
