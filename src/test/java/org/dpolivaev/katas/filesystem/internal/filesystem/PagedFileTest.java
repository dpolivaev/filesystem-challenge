package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.Directory;
import org.dpolivaev.katas.filesystem.File;
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
    public void anyOperationOnDeletedFile_throwsIllegalStateException() {

        root.deleteFile(uut.name());

        assertThat(uut.exists()).isFalse();

        assertThatThrownBy(() -> uut.name()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.size()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.getPosition()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> another.setPosition(0)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.write((byte)1)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.readByte()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> another.write(1)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.readInt()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.write(1L)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.readLong()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.write("String")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> another.readString()).isInstanceOf(IllegalStateException.class);

        final byte[] buffer16 = new byte[16];

        assertThatThrownBy(() -> uut.write(buffer16)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.read(buffer16)).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> another.write(buffer16, 1, 1)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> uut.read(buffer16, 1, 1)).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> uut.truncate()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void throwsIllegalArgumentException_ifFileSizeIsTooHigh() {
        final long maximumSupportedFileSize = fileSystem.maximumSupportedFileSize();
        uut.setPosition(maximumSupportedFileSize - 1);
        uut.write((byte) 1);
        assertThatThrownBy(() -> uut.write((byte) 1)).isInstanceOf(IllegalArgumentException.class);
    }
}