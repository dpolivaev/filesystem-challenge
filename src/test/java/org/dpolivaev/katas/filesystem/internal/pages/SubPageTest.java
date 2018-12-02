package org.dpolivaev.katas.filesystem.internal.pages;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SubPageTest {
    private final TestPage testPage = new TestPage(4).filledAscendingFrom(0);
    private final SubPage uut = new SubPage(testPage, 1, 4);

    @Test
    public void splitsPagesInTheMiddle() {

        final Pair<Page, Page> pair = uut.split(2);

        final Page first = pair.first;
        assertThat(first.size()).isEqualTo(2);
        assertThat(first.readByte(0)).isEqualTo((byte) 1);
        assertThat(first.readByte(1)).isEqualTo((byte) 2);
        final Page second = pair.second;
        assertThat(second.size()).isEqualTo(1);
        assertThat(second.readByte(0)).isEqualTo((byte) 3);
    }

    @Test
    public void splitsPagesAt0() {
        final Pair<Page, Page> pair = uut.split(0);

        final Page first = pair.first;
        assertThat(first.size()).isEqualTo(0);
        final Page second = pair.second;
        assertThat(second.size()).isEqualTo(3);
        assertThat(second.readByte(0)).isEqualTo((byte) 1);
    }

    @Test
    public void splitsPagesAtTheEnd() {
        final Pair<Page, Page> pair = uut.split(3);

        final Page first = pair.first;
        assertThat(first.size()).isEqualTo(3);
        assertThat(first.readByte(2)).isEqualTo((byte) 3);
        final Page second = pair.second;
        assertThat(second.size()).isEqualTo(0);
    }

    @Test
    public void splitChecksOffset() {
        final TestPage uut = new TestPage(3).filledAscendingFrom(1);
        assertThatThrownBy(() -> uut.split(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.split(4)).isInstanceOf(IllegalArgumentException.class);
    }

}