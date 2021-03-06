package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.TestPage;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dpolivaev.katas.filesystem.internal.filesystem.FileDescriptorStructure.*;
import static org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory.mockRandomWithSequenceFrom0;

public class FilePageTest {

    private PagePool pagePool;
    private TestPage firstPage;
    private PageEditor editor;
    private FilePage uut;
    private FilePage another;
    private TestPages testPages;

    @Before
    public void setUp() {
        editor = new PageEditor(null);
    }

    private void createFilePage(final int firstPageDataSize, final int pagesInPool, final int poolPageSize) {
        final Random random = mockRandomWithSequenceFrom0();
        createFilePage(firstPageDataSize, pagesInPool, poolPageSize, random);
    }

    private void createFilePage(final int firstPageDataSize, final int pagesInPool, final int poolPageSize, final Random random) {
        testPages = new TestPages(pagesInPool, poolPageSize);
        pagePool = new PagePool(testPages, random);
        firstPage = new TestPage(DATA_POSITION + firstPageDataSize);
        editor.setPage(firstPage);
        editor.setPosition(SIZE_POSITION);
        editor.write(0L);
        editor.setPosition(NAME_POSITION);
        editor.write("name");
        uut = new FilePage(pagePool, firstPage);
        editor.setPage(uut);
        another = new FilePage(pagePool, firstPage);
    }

    @Test
    public void calculatesMaximumSize_for16BytePages() {
        createFilePage(Long.BYTES, 1024, 2 * Long.BYTES);
        assertThat(uut.size() / Long.BYTES).isEqualTo((2 << PAGE_LEVEL_COUNT) - 1);
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
        editor.setPosition(SIZE_POSITION);
        editor.write(4L);
        editor.setPosition(NAME_POSITION);
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
    public void savesByteOnLevel_1_pages() {
        createFilePage(0, 100, 2 * Long.BYTES);

        editor.setPosition(16);
        editor.write((byte) 0x77);

        editor.setPosition(16);
        final byte readByte = editor.readByte();
        assertThat(readByte).isEqualTo((byte) 0x77);
        assertThat(uut.fileSize()).isEqualTo(17L);
    }


    @Test
    public void savesLongOnLevel_1_pages() {
        createFilePage(0, 100, 2 * Long.BYTES);

        editor.setPosition(10);
        editor.write(0x1234567887654321L);

        editor.setPosition(10);
        assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        assertThat(uut.fileSize()).isEqualTo(18L);
    }


    @Test
    public void savesLongOnLevel_2_pages() {
        createFilePage(0, 100, 2 * Long.BYTES);

        editor.setPosition(16);
        editor.write(0x1234567887654321L);

        editor.setPosition(16);
        assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        assertThat(uut.fileSize()).isEqualTo(24L);
    }


    @Test
    public void savesBufferLongerThanSinglePageSize() {
        createFilePage(2 * Long.BYTES, 100, 2 * Long.BYTES);
        final byte[] sourceArray = new byte[20 * Long.BYTES];
        final byte[] destinationArray = new byte[20 * Long.BYTES];
        final Random random = new Random(0);
        random.nextBytes(sourceArray);

        editor.setPosition(11);
        editor.write(sourceArray);
        editor.write(sourceArray, 15, 79);

        editor.setPosition(11);
        editor.read(destinationArray);
        assertThat(destinationArray).containsExactly(sourceArray);

        editor.read(destinationArray, 15, 79);
        assertThat(destinationArray).containsExactly(sourceArray);

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

        firstPage.erase(0, DATA_POSITION);
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


    @Test
    public void destroy_releasesRandomlyCreatedPagesToPoolAndDeletesDataAndDescriptor() {
        createFilePage(0, 100, 2 * Long.BYTES, new Random());

        final int size = (int) uut.size();
        Arrays.asList(size / 2, size / 4).forEach(position -> {
            editor.setPosition(position);
            editor.write(0x1234567887654321L);
        });

        uut.destroy();

        assertThat(testPages.areEmpty()).isTrue();
        assertThat(firstPage.isEmpty()).isTrue();
    }
}