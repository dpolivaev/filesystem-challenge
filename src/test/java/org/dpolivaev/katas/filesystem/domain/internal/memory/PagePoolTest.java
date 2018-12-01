package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.assertj.core.api.Assertions;
import org.dpolivaev.katas.filesystem.adapters.TestPages;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.mockito.Mockito.when;

public class PagePoolTest {

    private Random randomReturningConstant(final int value, final long expectedMaximum) {
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, expectedMaximum)).thenReturn(LongStream.iterate(value, x -> value));
        return random;
    }

    @Test
    public void reservesPage1_from2() {
        final TestPages memory = new TestPages(2, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 1));
        final Page page = uut.reserve();
        Assertions.assertThat(page.pageNumber()).isEqualTo(1L);
    }

    @Test
    public void reservesPage1_from10() {
        final TestPages memory = new TestPages(10, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 8));
        final Page page = uut.reserve();
        Assertions.assertThat(page.pageNumber()).isEqualTo(1L);
    }


    @Test
    public void reservesPage2_from11() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 9));
        final Page page = uut.reserve();
        Assertions.assertThat(page.pageNumber()).isEqualTo(2L);
    }

    @Test
    public void reservesPages_10_and_2_from11() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));
        Assertions.assertThat(uut.reserve().pageNumber()).isEqualTo(10L);
        Assertions.assertThat(uut.reserve().pageNumber()).isEqualTo(2L);
    }

    @Test
    public void releasesPage() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));
        uut.release(uut.reserve());
        Assertions.assertThat(uut.reserve().pageNumber()).isEqualTo(10L);
    }
}