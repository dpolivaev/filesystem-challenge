package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestBlock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SafeBlockTest {
    @Spy
    private final DataBlock delegate = new TestBlock(1, 4).filledAscendingFrom(1);

    @InjectMocks
    private SafeBlock uut;


    @Test
    public void setByteChecksOffset() {
        assertThatThrownBy(() -> uut.set(-1, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.set(4, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setByteSucceeds() {
        uut.set(0, -1);
        assertThat(uut.getByte(0)).isEqualTo((byte)-1);
    }

    @Test
    public void setFromArrayChecksOffset() {
        assertThatThrownBy(() -> uut.set(-1, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.set(4, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksLength() {
        assertThatThrownBy(() -> uut.set(1, -1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.set(3, 2, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArraySucceeds() {
        uut.set(1, 2, new byte[]{6, 7, 8}, 1);
        verify(delegate).set(1, 2, new byte[]{6, 7, 8}, 1);
    }

    @Test
    public void setFromArrayChecksArgumentArray() {
        assertThatThrownBy(() -> uut.set(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.set(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void getByteChecksOffset() {
        assertThatThrownBy(() -> uut.getByte(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.getByte(4)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getByteSucceeds() {
        assertThat(uut.getByte(1)).isEqualTo((byte)2);
    }

    @Test
    public void getToArrayChecksOffset() {
        assertThatThrownBy(() -> uut.get(-1, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.get(4, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void getToArrayChecksLength() {
        assertThatThrownBy(() -> uut.get(1, -1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.get(3, 2, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getFromArrayChecksArgumentArray() {
        assertThatThrownBy(() -> uut.get(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.get(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getToArraySucceeds() {
        final byte[] destination = {6, 7, 8};
        uut.get(1, 2, destination, 1);
        verify(delegate).get(1L, 2L, destination, 1L);
    }

    @Test
    public void splitChecksOffset() {
        assertThatThrownBy(() -> uut.split(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.split(4)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void splitReturnsSafeBlock() {
        final Pair<DataBlock, DataBlock> pair = uut.split(2);
        assertThat(pair.first).isInstanceOf(SafeBlock.class);
        assertThat(pair.second).isInstanceOf(SafeBlock.class);
    }
}