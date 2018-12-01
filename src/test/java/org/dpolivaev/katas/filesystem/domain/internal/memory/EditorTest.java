package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EditorTest {
    @Test
    public void savesIntegers() {
        final Editor uut = new Editor();
        TestPage page = new TestPage(1, 18);
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
        final Editor uut = new Editor();
        uut.setPage(new TestPage(1, 34));
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
        final Editor uut = new Editor();
        uut.setPage(new TestPage(1, 8));
        uut.setPosition(1);

        uut.write("abc");

        uut.setPosition(0);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readString()).isEqualTo("abc");
    }


    @Test
    public void resetsPosition_whenPageIsSet() {
        final Editor uut = new Editor();
        TestPage page = new TestPage(1, 8);
        uut.setPage(page);
        uut.setPosition(1);

        uut.write("abc");

        uut.setPage(page);
        assertThat(uut.readByte()).isEqualTo((byte) 0);
        assertThat(uut.readString()).isEqualTo("abc");
    }
}