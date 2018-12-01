package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PageTest {
    @Test
    public void splitsPages() {
        final TestPage uut = new TestPage(1, 3).filledAscendingFrom(1);

        final Pair<Page, Page> pair = uut.split(2);

        final Page first = pair.first;
        assertThat(first.position()).isEqualTo(1);
        assertThat(first.size()).isEqualTo(2);
        assertThat(first.readByte(0)).isEqualTo((byte) 1);
        assertThat(first.readByte(1)).isEqualTo((byte) 2);
        final Page second = pair.second;
        assertThat(second.position()).isEqualTo(1);
        assertThat(second.size()).isEqualTo(1);
        assertThat(second.readByte(0)).isEqualTo((byte) 3);
    }

}