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
        return locks.computeIfAbsent(uuid, key -> new ReentrantLock(){
            @Override
            public void lock() {
                super.lock();
                if(getHoldCount() == 1)
                    System.out.println("locked " + uuid + " in " + getOwner().getName());
            }

            @Override
            public void unlock() {
                if(getHoldCount() == 1)
                    System.out.println("unlocked " + uuid + " in " + getOwner().getName());
                super.unlock();
            }

            @Override
            public String toString() {
                return "uuid" + uuid +
                        super.toString();
            }
        });
    }
}
