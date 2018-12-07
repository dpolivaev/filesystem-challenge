package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Pages;

import java.util.Random;

public class ConcurrentPagePool extends PagePool {
    public ConcurrentPagePool(final Pages pages, final Random random) {
        super(pages, random);
    }
}
