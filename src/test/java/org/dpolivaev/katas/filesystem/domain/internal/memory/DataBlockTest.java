package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestBlock;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class DataBlockTest {
    @Test
    public void savesIntegers() {
        TestBlock uut = new TestBlock(1, 18);
        uut.put(1, -1);
        uut.put(5, 1);
        uut.put(9, Integer.MIN_VALUE);
        uut.put(13, Integer.MAX_VALUE);
        assertThat(uut.getByte(0)).isEqualTo((byte)0);
        assertThat(uut.getInt(1)).isEqualTo(-1);
        assertThat(uut.getInt(5)).isEqualTo(1);
        assertThat(uut.getInt(9)).isEqualTo(Integer.MIN_VALUE);
        assertThat(uut.getInt(13)).isEqualTo(Integer.MAX_VALUE);
        assertThat(uut.getByte(7)).isEqualTo((byte)0);
    }

    @Test
    public void savesLongs() {
        TestBlock uut = new TestBlock(1, 34);
        uut.put(1, -1L);
        uut.put(1 + 8, 1L);
        uut.put(1 + 16, Long.MIN_VALUE);
        uut.put(1 + 24, Long.MAX_VALUE);
        assertThat(uut.getByte(0)).isEqualTo((byte)0);
        assertThat(uut.getLong(1)).isEqualTo(-1L);
        assertThat(uut.getLong(1 + 8)).isEqualTo(1L);
        assertThat(uut.getLong(1 + 16)).isEqualTo(Long.MIN_VALUE);
        assertThat(uut.getLong(1 + 24)).isEqualTo(Long.MAX_VALUE);
        assertThat(uut.getByte(1 + 32)).isEqualTo((byte)0);
    }
}