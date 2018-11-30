package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestMemory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class SafeMemoryTest {
    @Spy
    private final Memory delegate = new TestMemory(4, 1);

    private Memory uut;

    @Before
    public void setUp() {
        uut = delegate.safe();
    }

    @Test
    public void splitChecksOffset() {
        assertThatThrownBy(() -> uut.split(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.split(4)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void atChecksOffset() {
        assertThatThrownBy(() -> uut.at(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.at(4)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void atSucceeds() {
        assertThat(uut.at(0L)).isSameAs(delegate.at(0L));
    }


    @Test
    public void splitReturnsSafeMemory() {
        final Pair<Memory, Memory> pair = uut.split(2);
        assertThat(pair.first).isInstanceOf(SafeMemory.class);
        assertThat(pair.second).isInstanceOf(SafeMemory.class);
    }

}