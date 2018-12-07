package org.dpolivaev.katas.filesystem.internal.filesystem;

import org.dpolivaev.katas.filesystem.internal.pages.PageEditor;
import org.dpolivaev.katas.filesystem.internal.pages.TestPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PageEditorTest {

    @Test
    public void savesIntegers() {
        final TestPage page = new TestPage(18);
        final PageEditor uut = new PageEditor(page);
        uut.setPosition(1);
        uut.write(-1);
        uut.write(1);
        uut.write(Integer.MIN_VALUE);
        uut.write(Integer.MAX_VALUE);

        uut.setPosition(0);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readInt()).isEqualTo(-1);
        assertThat(uut.readInt()).isEqualTo(1);
        assertThat(uut.readInt()).isEqualTo(Integer.MIN_VALUE);
        assertThat(uut.readInt()).isEqualTo(Integer.MAX_VALUE);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
    }

    @Test
    public void savesLongs() {
        final PageEditor uut = new PageEditor(new TestPage(34));
        uut.setPosition(1);

        uut.write(-1L);
        uut.write(1L);
        uut.write(Long.MIN_VALUE);
        uut.write(Long.MAX_VALUE);

        uut.setPosition(0);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readLong()).isEqualTo(-1L);
        assertThat(uut.readLong()).isEqualTo(1L);
        assertThat(uut.readLong()).isEqualTo(Long.MIN_VALUE);
        assertThat(uut.readLong()).isEqualTo(Long.MAX_VALUE);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
    }

    @Test
    public void savesString() {
        final PageEditor uut = new PageEditor(new TestPage(8));
        uut.setPosition(1);

        uut.write("abc");

        uut.setPosition(0);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readString()).isEqualTo("abc");
    }


    @Test
    public void resetsPosition_whenPageIsSet() {
        final TestPage page = new TestPage(8);
        final PageEditor uut = new PageEditor(null);
        uut.setPage(page);
        uut.write("abc");

        uut.setPage(page);
        assertThat(uut.readString()).isEqualTo("abc");
    }


    @Test
    public void setPositionChecksOffset() {
        final PageEditor uut = new PageEditor(new TestPage(8));
        assertThatThrownBy(() -> uut.setPosition(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setByteChecksOffset() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.write((byte) -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksOffset() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksLength() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 0, -1)).isInstanceOf(IllegalArgumentException.class);
        uut.setPosition(3);
        assertThatThrownBy(() -> uut.write(new byte[1], 0, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksArgumentArray() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 0, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getByteChecksOffset() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.readByte()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getToArrayChecksOffset() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getToArrayChecksLength() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 0, -1)).isInstanceOf(IllegalArgumentException.class);
        uut.setPosition(3);
        assertThatThrownBy(() -> uut.read(new byte[1], 0, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getFromArrayChecksArgumentArray() {
        final PageEditor uut = new PageEditor(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 0, 1)).isInstanceOf(IllegalArgumentException.class);
    }

}