package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PagePoolTest {

    private Random randomReturningConstant(final int value, final long expectedMaximum) {
        final Random random = Mockito.mock(Random.class);
        when(random.longs(0L, expectedMaximum)).thenReturn(LongStream.iterate(value, x -> value));
        return random;
    }

    @Test
    public void allocatesPage1_from2() {
        final TestPages memory = new TestPages(2, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 1));

        final PageAllocation allocation = uut.allocate();

        assertThat(allocation.pageNumber).isEqualTo(1L);
        assertThat(uut.pageUnsafe(1L)).isSameAs(allocation.page);
    }

    @Test
    public void allocatesPage1_from10() {
        final TestPages memory = new TestPages(10, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 8));

        final PageAllocation page = uut.allocate();

        assertThat(page.pageNumber).isEqualTo(1L);
    }


    @Test
    public void allocatesPage1_from11() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 9));

        final PageAllocation page = uut.allocate();

        assertThat(page.pageNumber).isEqualTo(1L);
    }

    @Test
    public void allocatesPages_9_and_1_from11() {
        final TestPages memory = new TestPages(11, 1);

        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));

        assertThat(uut.allocate().pageNumber).isEqualTo(9L);
        assertThat(uut.isAllocated(9L)).isTrue();
        assertThat(uut.allocate().pageNumber).isEqualTo(1L);
        assertThat(uut.isAllocated(1L)).isTrue();
    }

    @Test
    public void releasesPage() {
        final TestPages memory = new TestPages(11, 1);
        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));

        final long pageNumber = uut.allocate().pageNumber;
        uut.release(pageNumber);

        assertThat(uut.isAllocated(pageNumber)).isFalse();
        assertThat(uut.allocate().pageNumber).isEqualTo(9L);
    }


    @Test
    public void allocatesGivenPage() {
        final TestPages memory = new TestPages(11, 1);

        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));

        final Page page = uut.allocate(5);
        assertThat(uut.containsPage(5)).isTrue();
        assertThat(uut.pageUnsafe(5)).isSameAs(page);

    }
}