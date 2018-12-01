package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Test;

public class PagePoolTest {
    @Test
    public void name() {
        final PagePool uut = new PagePool(new TestMemory(100, 1024));

    }
}