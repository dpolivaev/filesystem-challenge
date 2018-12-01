package org.dpolivaev.katas.filesystem.domain.internal;

import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PageEditorTest {
    private final PageEditor uut = new PageEditor();
    
    @Test
    public void savesIntegers() {
        final TestPage page = new TestPage(18);
        uut.setPage(page);
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
        uut.setPage(new TestPage(34));
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
        uut.setPage(new TestPage(8));
        uut.setPosition(1);

        uut.write("abc");

        uut.setPosition(0);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readString()).isEqualTo("abc");
    }


    @Test
    public void resetsPosition_whenPageIsSet() {
        final TestPage page = new TestPage(8);
        uut.setPage(page);
        uut.setPosition(1);

        uut.write("abc");

        uut.setPage(page);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readString()).isEqualTo("abc");
    }


    @Test
    public void setPositionChecksOffset() {
        assertThatThrownBy(() -> uut.setPosition(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setByteChecksOffset() {
        uut.setPage(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.write((byte) -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksOffset() {
        uut.setPage(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksLength() {
        uut.setPage(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 0, -1)).isInstanceOf(IllegalArgumentException.class);
        uut.setPosition(3);
        assertThatThrownBy(() -> uut.write(new byte[1], 0, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksArgumentArray() {
        uut.setPage(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.write(new byte[]{}, 0, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getByteChecksOffset() {
        uut.setPage(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.readByte()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getToArrayChecksOffset() {
        uut.setPage(new TestPage(4));
        uut.setPosition(4);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getToArrayChecksLength() {
        uut.setPage(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 0, -1)).isInstanceOf(IllegalArgumentException.class);
        uut.setPosition(3);
        assertThatThrownBy(() -> uut.read(new byte[1], 0, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getFromArrayChecksArgumentArray() {
        uut.setPage(new TestPage(4));
        uut.setPosition(1);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.read(new byte[]{}, 0, 1)).isInstanceOf(IllegalArgumentException.class);
    }
    
}