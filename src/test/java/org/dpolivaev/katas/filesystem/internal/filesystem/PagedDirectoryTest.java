package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PagedDirectoryTest {
    private final TestFileSystem fileSystem = new TestFileSystem();
    private final Directory uut = fileSystem.root;
    private final Directory another = fileSystem.secondRoot;
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
        final File testFile = uut.createFile("someTestFile");
        another.deleteFile("someTestFile");
        assertThat(uut.file("someTestFile")).isEmpty();
    }
}