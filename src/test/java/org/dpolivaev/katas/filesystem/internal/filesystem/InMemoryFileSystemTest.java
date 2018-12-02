package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.internal.pool.PagePool;
import org.dpolivaev.katas.filesystem.persistence.TestPages;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory.mockRandomWithSequence_0toN;

public class InMemoryFileSystemTest {
    private TestPages testPages;
    private PagePool pagePool;
    private Directory uut;
    private Directory another;

    @Before
    public void createFileSystem() {
        createFileSystem(1024, 1024);
    }

    private void createFileSystem(final int pagesInPool, final int poolPageSize) {
        final Random random = mockRandomWithSequence_0toN();
        testPages = new TestPages(pagesInPool, poolPageSize);
        pagePool = new PagePool(testPages, random);
        uut = new InMemoryFileSystem(pagePool).root();
        another = new InMemoryFileSystem(pagePool).root();
    }

    @Test
    public void containsNoFilesAfterInitialization() {
        assertThat(uut.files().isEmpty()).isTrue();
    }

    @Test
    public void containsNoDirectoriesAfterInitialization() {
        assertThat(uut.directories().isEmpty()).isTrue();
    }

    @Test
    public void createsFile() {
        uut.createFile("someTestFile");
        assertThat(another.file("someTestFile")).isNotEmpty();
    }

    @Test
    public void deletesFile() {
        uut.createFile("someTestFile");
        another.deleteFile("someTestFile");
        assertThat(uut.file("someTestFile")).isEmpty();
    }
}