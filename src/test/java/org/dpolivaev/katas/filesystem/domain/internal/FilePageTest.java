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
    public void readsSizeAndNameFromDescriptor() {
        final PagePool pagePool = new PagePool(new TestPages(2, 1), new Random());
        final Page page = new TestPage(FilePage.DATA_POSITION);
        final PageEditor editor = new PageEditor();
        editor.setPage(page);
        editor.write(10L);
        editor.write("name");
        final FilePage uut = new FilePage(pagePool, page);

        Assertions.assertThat(uut.fileName()).isEqualTo("name");
        Assertions.assertThat(uut.fileSize()).isEqualTo(10L);
    }

    @Test
    public void savesByteOnInitialPage() {
        final PagePool pagePool = new PagePool(new TestPages(2, 1), new Random());
        final Page page = new TestPage(FilePage.DATA_POSITION + 1);
        final PageEditor editor = new PageEditor();
        editor.setPage(page);
        editor.write(0L);
        editor.write("name");
        final FilePage uut = new FilePage(pagePool, page);

        editor.setPage(uut);
        editor.write((byte) -1);
        editor.setPosition(0);
        Assertions.assertThat(editor.readByte()).isEqualTo((byte) -1);
        Assertions.assertThat(uut.fileSize()).isEqualTo(1L);
    }
}