package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class LockFactory {
    static private final Map<UUID, Lock> locks = Collections.synchronizedMap(new WeakHashMap<>());
    static private final Map<UUID, ReadWriteLock> readWriteLocks = Collections.synchronizedMap(new WeakHashMap<>());

    static Lock lock(final UUID uuid) {
        return locks.computeIfAbsent(uuid, key -> new ReentrantLock() {
            @Override
            public String toString() {
                return uuid.toString() + " -> " + super.toString();
            }
        });
    }


    static ReadWriteLock readWriteLock(final UUID uuid) {
        return readWriteLocks.computeIfAbsent(uuid, key -> new ReentrantReadWriteLock());
    }
}
