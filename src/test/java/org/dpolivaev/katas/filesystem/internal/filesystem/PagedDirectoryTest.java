package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PagedDirectoryTest {
    private final TestFileSystem fileSystem = new TestFileSystem(1024, 1024);
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
        final File file = uut.createFile("someTestFile");
        assertThat(file.exists()).isTrue();
        assertThat(another.file("someTestFile")).isNotEmpty();
    }

    @Test
    public void deletesFile() {
        final File file = uut.createFile("someTestFile");
        another.deleteFile("someTestFile");
        assertThat(file.exists()).isFalse();
        assertThat(uut.file("someTestFile")).isEmpty();
    }

    @Test
    public void createsDirectoriesAndFiles() {
        final PagedDirectory dir1 = (PagedDirectory) uut.createDirectory("dir1");
        final PagedDirectory dir11 = (PagedDirectory) another.directory("dir1").get().createDirectory("dir11");
        final PagedFile file1 = (PagedFile) dir1.directory("dir11").get().createFile("file1");
        Assertions.assertThat(file1.exists()).isTrue();
        Assertions.assertThat(dir11.exists()).isTrue();
        Assertions.assertThat(dir1.exists()).isTrue();
    }

    @Test
    public void deletesDirectoriesRecursively() {
        final PagedDirectory dir1 = (PagedDirectory) uut.createDirectory("dir1");
        final PagedDirectory dir11 = (PagedDirectory) another.directory("dir1").get().createDirectory("dir11");
        final PagedFile file1 = (PagedFile) dir1.directory("dir11").get().createFile("file1");
        uut.deleteDirectory("dir1");
        Assertions.assertThat(dir1.exists()).isFalse();
        Assertions.assertThat(dir11.exists()).isFalse();
        Assertions.assertThat(file1.exists()).isFalse();
    }
}