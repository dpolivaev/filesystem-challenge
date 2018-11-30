package org.dpolivaev.katas.filesystem.domain.internal.memory;

public interface Splittable<T extends Splittable<T>> {
    Pair<T, T> split(long position);
}
