package org.dpolivaev.katas.filesystem.domain.internal.memory;

import org.dpolivaev.katas.filesystem.adapters.TestPages;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        final PageAllocation allocation = uut.reserve();

        assertThat(allocation.pageNumber).isEqualTo(1L);
        assertThat(uut.at(1L)).isSameAs(allocation.page);
    }

    @Test
    public void reservesPage1_from10() {
        final TestPages memory = new TestPages(10, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 8));

        final PageAllocation page = uut.reserve();

        assertThat(page.pageNumber).isEqualTo(1L);
    }


    @Test
    public void reservesPage1_from11() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 9));

        final PageAllocation page = uut.reserve();

        assertThat(page.pageNumber).isEqualTo(1L);
    }

    @Test
    public void reservesPages_9_and_1_from11() {
        final TestPages memory = new TestPages(11, 1);

        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));

        assertThat(uut.reserve().pageNumber).isEqualTo(9L);
        assertThat(uut.reserve().pageNumber).isEqualTo(1L);
    }

    @Test
    public void releasesPage() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));

        final long pageNumber = uut.reserve().pageNumber;
        uut.release(pageNumber);

        assertThatThrownBy(() -> uut.at(pageNumber)).isInstanceOf(IllegalArgumentException.class);
        assertThat(uut.reserve().pageNumber).isEqualTo(9L);
    }
}