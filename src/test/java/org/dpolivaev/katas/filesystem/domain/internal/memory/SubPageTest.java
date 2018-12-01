package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubPageTest {

    @Test
    public void splitsPages() {
        final TestPage testPage = new TestPage(4).filledAscendingFrom(1);

        final SubPage uut = new SubPage(testPage, 1, 3);

        final Pair<Page, Page> pair = uut.split(1);

        final Page first = pair.first;
        assertThat(first.size()).isEqualTo(1);
        assertThat(first.readByte(0)).isEqualTo((byte) 2);
        final Page second = pair.second;
        assertThat(second.size()).isEqualTo(1);
        assertThat(second.readByte(0)).isEqualTo((byte) 3);
    }

}