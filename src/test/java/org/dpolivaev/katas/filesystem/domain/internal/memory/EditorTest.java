package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EditorTest {
    @Test
    public void savesIntegers() {
        final Editor uut = new Editor(new TestPage(1, 18));
        uut.write(1, -1);
        uut.write(5, 1);
        uut.write(9, Integer.MIN_VALUE);
        uut.write(13, Integer.MAX_VALUE);
        assertThat(uut.readByte(0)).isEqualTo((byte) 0);
        assertThat(uut.readInt(1)).isEqualTo(-1);
        assertThat(uut.readInt(5)).isEqualTo(1);
        assertThat(uut.readInt(9)).isEqualTo(Integer.MIN_VALUE);
        assertThat(uut.readInt(13)).isEqualTo(Integer.MAX_VALUE);
        assertThat(uut.readByte(7)).isEqualTo((byte) 0);
    }

    @Test
    public void savesLongs() {
        final Editor uut = new Editor(new TestPage(1, 34));
        uut.write(1, -1L);
        uut.write(1 + 8, 1L);
        uut.write(1 + 16, Long.MIN_VALUE);
        uut.write(1 + 24, Long.MAX_VALUE);
        assertThat(uut.readByte(0)).isEqualTo((byte) 0);
        assertThat(uut.readLong(1)).isEqualTo(-1L);
        assertThat(uut.readLong(1 + 8)).isEqualTo(1L);
        assertThat(uut.readLong(1 + 16)).isEqualTo(Long.MIN_VALUE);
        assertThat(uut.readLong(1 + 24)).isEqualTo(Long.MAX_VALUE);
        assertThat(uut.readByte(1 + 32)).isEqualTo((byte) 0);
    }

    @Test
    public void savesString() {
        final Editor uut = new Editor(new TestPage(1, 8));
        uut.write(1, "abc");
        assertThat(uut.readByte(0)).isEqualTo((byte) 0);
        assertThat(uut.readString(1)).isEqualTo("abc");
    }

}