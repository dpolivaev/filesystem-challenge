package org.dpolivaev.katas.filesystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    public static final int FILE_SYSTEM_SIZE = 1024 * 1024;
    java.io.File fsFile;
    public static final int POSITION_NEAR_THE_END = FILE_SYSTEM_SIZE * 99 / 100;

    private static java.io.File createFileSystemFile() {
        try {
            final java.io.File tempFile = java.io.File.createTempFile("filesystem", ".kata");
            tempFile.delete();
            return tempFile;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    private FileSystem createFilesystem() {
        return FileSystem.create(fsFile.getPath(), FILE_SYSTEM_SIZE);
    }

    private FileSystem openFilesystem() {
        return FileSystem.open(fsFile.getPath(), FILE_SYSTEM_SIZE);
    }

    private FileSystem createConcurrentFilesystem() {
        return FileSystem.create(fsFile.getPath(), FILE_SYSTEM_SIZE);
    }

    private FileSystem openConcurrentFilesystem() {
        return FileSystem.open(fsFile.getPath(), FILE_SYSTEM_SIZE);
    }

    @Before
    public void setUp() {
        fsFile = createFileSystemFile();
    }

    @After
    public void tearDown() {
        fsFile.delete();
    }

    private void checkHugeFileContent(final File hugeFile, final String expectedString) {
        hugeFile.setPosition(POSITION_NEAR_THE_END);
        final String string = hugeFile.readString();
        assertThat(string).isEqualTo(expectedString);
    }

    private File createHugeFile(final FileSystem fileSystem, final String fileName, final String string) {
        final File hugeFile = fileSystem.root().createFile(fileName);
        assertThat(hugeFile.size()).isEqualTo(0L);
        hugeFile.setPosition(POSITION_NEAR_THE_END);
        hugeFile.write(string);
        return hugeFile;
    }

    @Test
    public void createAndUseHugeFile() {
        try (final FileSystem fileSystem = createFilesystem()) {
            final File hugeFile = createHugeFile(fileSystem, "hugeFile", "Hello world");
            checkHugeFileContent(hugeFile, "Hello world");
        }

        try (final FileSystem fileSystem = openFilesystem()) {
            final File hugeFile = fileSystem.root().file("hugeFile").get();
            checkHugeFileContent(hugeFile, "Hello world");
            fileSystem.root().deleteFile(hugeFile.name());
            final File otherFile = createHugeFile(fileSystem, "otherFile", "Other");
            checkHugeFileContent(otherFile, "Other");
            fileSystem.root().deleteFile(otherFile.name());
        }
    }

}
