package org.dpolivaev.katas.filesystem.domain.internal;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.dpolivaev.katas.filesystem.adapters.TestPages;
import org.dpolivaev.katas.filesystem.domain.internal.memory.Page;
import org.dpolivaev.katas.filesystem.domain.internal.memory.PagePool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

public class FilePageTest {

    private PagePool pagePool;
    private Page firstPage;
    private PageEditor editor;
    private FilePage uut;
    private FilePage another;

    @Before
    public void setUp() {
        editor = new PageEditor();
    }

    private void createFilePage(final int firstPageDataSize, final int pagesInPool, final int poolPageSize) {
        final Random random = mockRandomWithSequence_0toN();
        pagePool = new PagePool(new TestPages(pagesInPool, poolPageSize), random);
        firstPage = new TestPage(FilePage.DATA_POSITION + firstPageDataSize);
        editor.setPage(firstPage);
        editor.write(0L);
        editor.write("name");
        uut = new FilePage(pagePool, firstPage);
        editor.setPage(uut);
        another = new FilePage(pagePool, firstPage);
    }

    private Random mockRandomWithSequence_0toN() {
        final Random random = Mockito.mock(Random.class);
        Mockito.when(random.longs(ArgumentMatchers.eq(0L), ArgumentMatchers.anyLong())).thenReturn(LongStream.iterate(0L, x -> x + 1));
        return random;
    }

    @Test
    public void calculatesItsMaximumSize() {
 /*       createFilePage(0, 2, Long.BYTES);
        Assertions.assertThat(uut.size()).isEqualTo(8L);

        createFilePage(1, 2, Long.BYTES);
        Assertions.assertThat(uut.size()).isEqualTo(9L);

*/
        createFilePage(0, 1024, 2 * Long.BYTES);
        Assertions.assertThat(uut.size() / Long.BYTES).isEqualTo(2 + 4);
    }

    @Test
    public void readsFileSizeAndNameFromDescriptor() {
        createFilePage(0, 2, Long.BYTES);
        editor.setPage(firstPage);
        editor.write(4L);
        editor.write("name");

        Assertions.assertThat(uut.fileName()).isEqualTo("name");
        Assertions.assertThat(uut.fileSize()).isEqualTo(4L);
    }

    @Test
    public void savesByteOnInitialPage() {
        createFilePage(1, 2, 8);
        editor.write((byte) -1);

        editor.setPage(another);
        Assertions.assertThat(editor.readByte()).isEqualTo((byte) -1);
        Assertions.assertThat(uut.fileSize()).isEqualTo(1L);
    }

    @Test
    public void savesLongUsingPoolPage() {
        createFilePage(1, 2, Long.BYTES);

        editor.write(0x1234567887654321L);

        editor.setPage(another);
        Assertions.assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        Assertions.assertThat(uut.fileSize()).isEqualTo(8L);
    }


    @Test
    public void name() {
        createFilePage(1, 100, 2 * Long.BYTES);

        editor.setPosition(10);
        editor.write(0x1234567887654321L);

        editor.setPosition(10);
        Assertions.assertThat(editor.readLong()).isEqualTo(0x1234567887654321L);
        Assertions.assertThat(uut.fileSize()).isEqualTo(8L);
    }
}