package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.EndOfFileException;
import org.dpolivaev.katas.filesystem.File;
import org.dpolivaev.katas.filesystem.IllegalStateIOException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PagedFileTest {
    private final TestFileSystem fileSystem = TestFileSystem.create(32, 256);
    private final Directory root = fileSystem.root;
    private final File uut = root.createFile("file");
    private final Directory secondRoot = fileSystem.alternativeRoot;
    private final File another = secondRoot.file("file").get();


    @Test
    public void anyOperationOnDeletedFile_throwsIllegalStateIOException() {

        root.deleteFile(uut.name());

        assertThat(uut.exists()).isFalse();

        assertThatThrownBy(() -> uut.name()).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.size()).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.getPosition()).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> another.setPosition(0)).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.write((byte) 1)).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.readByte()).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> another.write(1)).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.readInt()).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.write(1L)).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.readLong()).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.write("String")).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> another.readString()).isInstanceOf(IllegalStateIOException.class);

        final byte[] buffer16 = new byte[16];

        assertThatThrownBy(() -> uut.write(buffer16)).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.read(buffer16)).isInstanceOf(IllegalStateIOException.class);

        assertThatThrownBy(() -> another.write(buffer16, 1, 1)).isInstanceOf(IllegalStateIOException.class);
        assertThatThrownBy(() -> uut.read(buffer16, 1, 1)).isInstanceOf(IllegalStateIOException.class);

        assertThatThrownBy(() -> uut.deleteContent()).isInstanceOf(IllegalStateIOException.class);
    }

    @Test
    public void readingAfterEndOfFile_throwsEndOfFileException() {
        uut.deleteContent();

        assertThatThrownBy(() -> uut.readByte()).isInstanceOf(EndOfFileException.class);
        assertThatThrownBy(() -> uut.readInt()).isInstanceOf(EndOfFileException.class);
        assertThatThrownBy(() -> uut.readLong()).isInstanceOf(EndOfFileException.class);
        assertThatThrownBy(() -> another.readString()).isInstanceOf(EndOfFileException.class);
        final byte[] buffer16 = new byte[16];
        assertThatThrownBy(() -> uut.read(buffer16)).isInstanceOf(EndOfFileException.class);
        assertThatThrownBy(() -> uut.read(buffer16, 1, 1)).isInstanceOf(EndOfFileException.class);
    }


    @Test
    public void throwsIllegalArgumentIOException_ifFileSizeIsTooHigh() {
        final long maximumSupportedFileSize = fileSystem.maximumSupportedFileSize();
        uut.setPosition(maximumSupportedFileSize - 1);
        uut.write((byte) 1);
        assertThatThrownBy(() -> uut.write((byte) 1)).isInstanceOf(IllegalArgumentException.class);
    }
}