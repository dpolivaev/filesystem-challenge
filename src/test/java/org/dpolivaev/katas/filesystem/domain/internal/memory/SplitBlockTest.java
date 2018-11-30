package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestBlock;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitBlockTest {

    @Test
    public void splitsBlocks() {
        TestBlock testBlock = new TestBlock(1, 4);
        testBlock.put(0, (byte)1);
        testBlock.put(1, (byte)2);
        testBlock.put(2, (byte)3);
        testBlock.put(3, (byte)4);

        SplitBlock uut = new SplitBlock(testBlock, 1, 3);

        Pair<DataBlock, DataBlock> pair = uut.split(1);

        DataBlock first = pair.first;
        assertThat(first.position()).isEqualTo(1);
        assertThat(first.size()).isEqualTo(1);
        assertThat(first.getByte(0)).isEqualTo((byte)2);
        DataBlock second = pair.second;
        assertThat(second.position()).isEqualTo(1);
        assertThat(second.size()).isEqualTo(1);
        assertThat(second.getByte(0)).isEqualTo((byte)3);
    }

}