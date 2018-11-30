package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitMemoryTest {
    @Test
    public void splitsMemory() {
        final TestMemory uut = new TestMemory(3, 1);
        final Pair<Memory, Memory> pair = uut.split(2).first.split(1);
        final Memory first = pair.first;
        final Memory second = pair.second;

        assertThat(first.size()).isEqualTo(1L);
        assertThat(first.at(0)).isSameAs(uut.at(0));

        assertThat(second.size()).isEqualTo(1L);
        assertThat(second.at(0)).isSameAs(uut.at(1));
    }

}