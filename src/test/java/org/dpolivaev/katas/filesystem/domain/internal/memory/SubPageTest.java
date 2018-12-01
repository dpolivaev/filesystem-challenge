package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestBlock;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubPageTest {

    @Test
    public void splitsBlocks() {
        final TestBlock testBlock = new TestBlock(1, 4).filledAscendingFrom(1);

        final SubPage uut = new SubPage(testBlock, 1, 3);

        final Pair<Page, Page> pair = uut.split(1);

        final Page first = pair.first;
        assertThat(first.position()).isEqualTo(1);
        assertThat(first.size()).isEqualTo(1);
        assertThat(first.readByte(0)).isEqualTo((byte) 2);
        final Page second = pair.second;
        assertThat(second.position()).isEqualTo(1);
        assertThat(second.size()).isEqualTo(1);
        assertThat(second.readByte(0)).isEqualTo((byte) 3);
    }

}