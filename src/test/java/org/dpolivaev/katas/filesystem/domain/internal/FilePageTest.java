package org.dpolivaev.katas.filesystem.domain.internal;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.dpolivaev.katas.filesystem.adapters.TestPages;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;
import org.junit.Test;

import java.util.Random;

public class FilePageTest {
    @Test
    public void descriptor() {
        final PagePool pagePool = new PagePool(new TestPages(2, 1), new Random());
        final Page page = new TestPage(FilePage.DATA_POSITION);
        final PageEditor editor = new PageEditor();
        editor.setPage(page);
        editor.write(10L);
        editor.write("name");
        final FilePage uut = new FilePage(pagePool, page);
        Assertions.assertThat(uut.fileName()).isEqualTo("name");
        Assertions.assertThat(uut.size()).isEqualTo(10L);
    }

    @Test
    public void read() {
        final PagePool pagePool = new PagePool(new TestPages(2, 1), new Random());
        final Page page = new TestPage(FilePage.DATA_POSITION + 2);
        final PageEditor editor = new PageEditor();
        editor.setPage(page);
        editor.write(10L);
        editor.write("name");
        final FilePage uut = new FilePage(pagePool, page);
        Assertions.assertThat(uut.fileName()).isEqualTo("name");
        Assertions.assertThat(uut.size()).isEqualTo(10L);
    }
}