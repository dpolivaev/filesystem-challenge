package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.assertj.core.api.ThrowableAssert;
import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
import org.junit.Test;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PagedDirectoryTest {
    private final TestFileSystem fileSystem = TestFileSystem.create(1024, 1024);
    private final PagedDirectory uut = fileSystem.root;
    private final Directory another = fileSystem.alternativeRoot;

    @Test
    public void containsNoFilesAfterInitialization() {
        assertThat(uut.files().isEmpty()).isTrue();
    }

    @Test
    public void fileReturnsEmpty_ifChildFileDoesNotExists() {
        assertThat(uut.file("file")).isEmpty();
    }

    @Test
    public void directoryReturnsEmpty_ifChildDirectoryDoesNotExists() {
        assertThat(uut.directory("directory")).isEmpty();
    }

    @Test
    public void containsNoDirectoriesAfterInitialization() {
        assertThat(uut.directories().isEmpty()).isTrue();
    }

    @Test
    public void createsFiles() {
        final File file = uut.createFile("someTestFile");

        assertThat(file.exists()).isTrue();
        assertThat(another.file("someTestFile").get().uuid()).isEqualTo(file.uuid());
        assertThat(another.file("someTestFile")).isNotEmpty();
    }

    @Test
    public void createsMultipleFiles() {
        uut.createFile("file1");
        final File file2 = uut.createFile("file2");

        assertThat(file2.exists()).isTrue();
        assertThat(another.file("file2").get().uuid()).isEqualTo(file2.uuid());
    }


    @Test
    public void createFile_throwsException_ifFileAlreadyExists() {
        final File file1 = uut.createFile("file1");
        assertThatThrownBy(() -> uut.createFile("file1"))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("File 'file1' already exists");

        assertThat(file1.exists()).isTrue();
    }

    @Test
    public void deletesFile() {
        final File file1 = uut.createFile("file1");
        final File file2 = uut.createFile("file2");

        uut.deleteFile("file1");

        assertThat(file1.exists()).isFalse();
        assertThat(uut.file("file1")).isEmpty();
        assertThat(file2.exists()).isTrue();
        assertThat(uut.file("file2").get().uuid()).isEqualTo(file2.uuid());
    }


    @Test
    public void reusesSpaceOfDeletedFileReferences() {
        uut.createFile("file1");
        uut.createFile("file2");
        final long directoryInternalFileSize = uut.directoryInternalFileSize();
        uut.deleteFile("file1");
        uut.createFile("file3");
        assertThat(uut.directoryInternalFileSize()).isEqualTo(directoryInternalFileSize);
    }

    @Test
    public void deleteDoesNothingEmpty_ifChildFileDoesNotExists() {
        uut.deleteFile("file");
    }

    @Test
    public void deleteDoesNothingEmpty_ifChildDirectoryDoesNotExists() {
        uut.deleteDirectory("directory");
    }

    public void assertRejectBadName(final String name, final String message) {
        final ThrowableAssert.ThrowingCallable calls[] = {
                () -> uut.createFile(name),
                () -> uut.createDirectory(name),
                () -> uut.file(name),
                () -> uut.directory(name),
                () -> uut.deleteFile(name),
                () -> uut.deleteDirectory(name),
        };
        Stream.of(calls).forEach(c ->
                assertThatThrownBy(c)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(message));
    }

    @Test
    public void rejectsNullName() {
        assertRejectBadName(null, "Name must not be null");
    }

    @Test
    public void rejectsEmptyName() {
        assertRejectBadName("", "Empty name is not allowed");
    }

    @Test
    public void rejectsTooLongName() {
        final String name = String.join("", Collections.nCopies(FileDescriptorStructure.NAME_SIZE + 1, "x"));
        assertRejectBadName(name, "Name is too long");
    }

    @Test
    public void createsDirectoriesAndFiles() {
        final PagedDirectory dir1 = (PagedDirectory) uut.createDirectory("dir1");
        final PagedDirectory dir11 = (PagedDirectory) another.directory("dir1").get().createDirectory("dir11");
        final PagedFile file1 = (PagedFile) dir1.directory("dir11").get().createFile("file1");
        assertThat(file1.exists()).isTrue();
        assertThat(dir11.exists()).isTrue();
        assertThat(dir1.exists()).isTrue();
    }

    @Test
    public void deletesDirectoriesRecursively() {
        final PagedDirectory dir1 = (PagedDirectory) uut.createDirectory("dir1");
        final PagedDirectory dir2 = (PagedDirectory) uut.createDirectory("dir2");
        final PagedDirectory dir11 = (PagedDirectory) another.directory("dir1").get().createDirectory("dir11");
        final PagedFile file11_1 = (PagedFile) dir1.directory("dir11").get().createFile("file1");
        file11_1.setPosition(100).write(80);
        final PagedFile file11_2 = (PagedFile) dir1.directory("dir11").get().createFile("file2");
        file11_2.setPosition(10000).write(24);

        uut.deleteDirectory("dir1");

        assertThat(dir1.exists()).isFalse();
        assertThat(dir11.exists()).isFalse();
        assertThat(file11_1.exists()).isFalse();
        assertThat(file11_2.exists()).isFalse();
        assertThat(dir2.exists()).isTrue();
    }
}