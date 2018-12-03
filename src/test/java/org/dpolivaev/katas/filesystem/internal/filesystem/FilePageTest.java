package org.dpolivaev.katas.filesystem.internal.filesystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory.mockRandomWithSequence_0toN;

import java.util.Random;

import org.dpolivaev.katas.filesystem.internal.pages.TestPage;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;
import org.junit.Before;
import org.junit.Test;

public class FilePageTest {

    private PagePool pagePool;
    private TestPage firstPage;
    private PageEditor editor;
    private FilePage uut;
    private FilePage another;
    private TestPages testPages;

    @Before
    public void setUp() {
        editor = new PageEditor();
    }

    private void createFilePage(final int firstPageDataSize, final int pagesInPool, final int poolPageSize) {
        final Random random = mockRandomWithSequence_0toN();
        testPages = new TestPages(pagesInPool, poolPageSize);
        pagePool = new PagePool(testPages, random);
        firstPage = new TestPage(FilePage.DATA_POSITION + firstPageDataSize);
        editor.setPage(firstPage);
        editor.write(0L);
        editor.write("name");
        uut = new FilePage(pagePool, firstPage);
        editor.setPage(uut);
        another = new FilePage(pagePool, firstPage);
    }

    @Test
    public void calculatesMaximumSize_for16BytePages() {
        createFilePage(Long.BYTES, 1024, 2 * Long.BYTES);
        assertThat(uut.size() / Long.BYTES).isEqualTo((2 << FilePage.PAGE_LEVEL_COUNT) - 1);
    }

    @Test
    public void calculatesMaximumSize_for1024BytePages() {
        createFilePage(1024, 1024, 1024);
        final long sizeInGigabytes = uut.size() / (1024 * 1024 * 1024);
        assertThat(sizeInGigabytes).isEqualTo(258L);
    }


    @Test
    public void readsFileSizeAndNameFromDescriptor() {
        createFilePage(0, 2, Long.BYTES);
        editor.setPage(firstPage);
        editor.setPosition(FilePage.SIZE_POSITION);
        editor.write(4L);
        editor.write("name");

        assertThat(uut.name()).isEqualTo("name");
        assertThat(uut.fileSize()).isEqualTo(4L);
    }

    @Test
    public void savesByteOnInitialPage() {
        createFilePage(1, 2, 8);
        editor.write((byte) -1);

        editor.setPage(another);
        assertThat(editor.readByte()).isEqualTo((byte) -1);
        assertThat(uut.fileSize()).isEqualTo(1L);
    }

    @Test
    public void savesLongUsingPoolPage() {
        createFilePage(1, 2, Long.BYTES);

        editor.write(0x1234567887654321L);

        editor.setPage(another);
        assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        assertThat(uut.fileSize()).isEqualTo(8L);
    }


    @Test
    public void useLevel_1_pages() {
        createFilePage(0, 100, 2 * Long.BYTES);

        editor.setPosition(10);
        editor.write(0x1234567887654321L);

        editor.setPosition(10);
        assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        assertThat(uut.fileSize()).isEqualTo(18L);
    }


    @Test
    public void useLevel_2_pages() {
        createFilePage(0, 100, 2 * Long.BYTES);

        editor.setPosition(16);
        editor.write(0x1234567887654321L);

        editor.setPosition(16);
        assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        assertThat(uut.fileSize()).isEqualTo(24L);
    }

    @Test
    public void useLastLevelPages() {
        createFilePage(0, 100, 2 * Long.BYTES);

        final long position = uut.size() - Long.BYTES;
        editor.setPosition(position);
        editor.write(0x1234567887654321L);

        editor.setPosition(position);
        assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        assertThat(uut.fileSize()).isEqualTo(uut.size());
    }


    @Test
    public void truncate_releasesAllPagesToPoolAndDeletesData() {
        createFilePage(0, 100, 2 * Long.BYTES);

        final long position = uut.size() - Long.BYTES;
        editor.setPosition(position);
        editor.write(0x1234567887654321L);

        uut.truncate();

        assertThat(uut.fileSize()).isEqualTo(0L);
        assertThat(testPages.areEmpty()).isTrue();

        firstPage.erase(0, FilePage.DATA_POSITION);
        assertThat(firstPage.isEmpty()).isTrue();

    }


    @Test
    public void destroy_releasesAllPagesToPoolAndDeletesDataAndDescriptor() {
        createFilePage(0, 100, 2 * Long.BYTES);

        final long position = uut.size() - Long.BYTES;
        editor.setPosition(position);
        editor.write(0x1234567887654321L);

        uut.destroy();

        assertThat(testPages.areEmpty()).isTrue();
        assertThat(firstPage.isEmpty()).isTrue();
    }
}