package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockFactory {
    static private final Map<UUID, Lock> locks = Collections.synchronizedMap(new WeakHashMap<>());

    static Lock lock(final UUID uuid) {
        return locks.computeIfAbsent(uuid, key -> new ReentrantLock());
    }
}
