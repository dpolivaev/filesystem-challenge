package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class LockFactory {
    static private final Map<UUID, ReadWriteLock> locks = Collections.synchronizedMap(new WeakHashMap<>());

    static ReadWriteLock lock(final UUID uuid) {
        return locks.computeIfAbsent(uuid, ignore -> new ReentrantReadWriteLock());
    }
}
