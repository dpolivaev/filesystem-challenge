package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class SafePageTest {
    @Spy
    private final Page delegate = new TestPage(1, 4).filledAscendingFrom(1);

    @InjectMocks
    private SafePage uut;


    @Test
    public void setByteChecksOffset() {
        assertThatThrownBy(() -> uut.write(-1, (byte) -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.write(4, (byte) -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setByteSucceeds() {
        uut.write(0, (byte) -1);
        assertThat(uut.readByte(0)).isEqualTo((byte) -1);
    }

    @Test
    public void setFromArrayChecksOffset() {
        assertThatThrownBy(() -> uut.write(-1, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.write(4, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArrayChecksLength() {
        assertThatThrownBy(() -> uut.write(1, -1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.write(3, 2, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void setFromArraySucceeds() {
        uut.write(1, 2, new byte[]{6, 7, 8}, 1);
        verify(delegate).write(1, 2, new byte[]{6, 7, 8}, 1);
    }

    @Test
    public void setFromArrayChecksArgumentArray() {
        assertThatThrownBy(() -> uut.write(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.write(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void getByteChecksOffset() {
        assertThatThrownBy(() -> uut.readByte(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.readByte(4)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getByteSucceeds() {
        assertThat(uut.readByte(1)).isEqualTo((byte) 2);
    }

    @Test
    public void getToArrayChecksOffset() {
        assertThatThrownBy(() -> uut.read(-1, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.read(4, 0, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void getToArrayChecksLength() {
        assertThatThrownBy(() -> uut.read(1, -1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.read(3, 2, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getFromArrayChecksArgumentArray() {
        assertThatThrownBy(() -> uut.read(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.read(1, 1, new byte[]{}, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getToArraySucceeds() {
        final byte[] destination = {6, 7, 8};
        uut.read(1, 2, destination, 1);
        verify(delegate).read(1L, 2L, destination, 1L);
    }

    @Test
    public void splitChecksOffset() {
        assertThatThrownBy(() -> uut.split(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> uut.split(4)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void splitReturnsSafePage() {
        final Pair<Page, Page> pair = uut.split(2);
        assertThat(pair.first).isInstanceOf(SafePage.class);
        assertThat(pair.second).isInstanceOf(SafePage.class);
    }
}