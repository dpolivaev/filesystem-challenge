package org.dpolivaev.katas.filesystem.internal.filesystem;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockFactory {
    static private final Lock lock = new ReentrantLock();

    static Lock lock(final UUID uuid) {
        return lock;
    }
}
