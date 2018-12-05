package org.dpolivaev.katas.filesystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    java.io.File fsFile;

    private static java.io.File createFileSystemFile() {
        try {
            final java.io.File tempFile = java.io.File.createTempFile("filesystem", ".kata");
            tempFile.delete();
            return tempFile;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    private FileSystem openFilesystem() {
        return FileSystem.open(fsFile, 1024 * 1024);
    }

    private FileSystem createFilesystem() {
        return FileSystem.create(fsFile, 1024 * 1024);
    }


    @Before
    public void setUp() {
        fsFile = createFileSystemFile();
    }

    @After
    void tearDown() {
        fsFile.delete();
    }

    @Test
    public void createAndUseFile() {
        try (final FileSystem fileSystem = createFilesystem()) {
            final File ababa = fileSystem.root().createFile("ababa");
            assertThat(ababa.size()).isEqualTo(0L);
            ababa.setPosition(678 * 1024);
            ababa.write("Hello world");
            ababa.setPosition(678 * 1024);
            final String string = ababa.readString();
            assertThat(string).isEqualTo("Hello world");
        }

        try (final FileSystem fileSystem = openFilesystem()) {
            final File ababa = fileSystem.root().file("ababa").get();
            ababa.setPosition(678 * 1024);
            final String string = ababa.readString();
            assertThat(string).isEqualTo("Hello world");
        }
    }
}
