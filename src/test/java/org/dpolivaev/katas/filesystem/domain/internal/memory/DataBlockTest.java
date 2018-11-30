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

    @Test
    public void savesString() {
        TestBlock uut = new TestBlock(1, 8);
        uut.put(1, "abc");
        assertThat(uut.getByte(0)).isEqualTo((byte)0);
        assertThat(uut.getString(1)).isEqualTo("abc");
    }

    @Test
    public void splitsBlocks() {
        TestBlock uut = new TestBlock(1, 3).filledAscendingFrom(1);

        Pair<DataBlock, DataBlock> pair = uut.split(2);

        DataBlock first = pair.first;
        assertThat(first.position()).isEqualTo(1);
        assertThat(first.size()).isEqualTo(2);
        assertThat(first.getByte(0)).isEqualTo((byte)1);
        assertThat(first.getByte(1)).isEqualTo((byte)2);
        DataBlock second = pair.second;
        assertThat(second.position()).isEqualTo(1);
        assertThat(second.size()).isEqualTo(1);
        assertThat(second.getByte(0)).isEqualTo((byte)3);
    }
 }