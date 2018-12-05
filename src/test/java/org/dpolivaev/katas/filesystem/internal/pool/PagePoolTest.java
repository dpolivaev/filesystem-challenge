package org.dpolivaev.katas.filesystem.internal.pool;

import org.dpolivaev.katas.filesystem.internal.filesystem.TestRandomFactory;
import org.dpolivaev.katas.filesystem.internal.pages.Page;
import org.dpolivaev.katas.filesystem.internal.pages.TestPages;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class PagePoolTest {

    private Random randomReturningConstant(final int value, final long expectedMaximum) {
        return TestRandomFactory.mockRandomWithConstantValue(expectedMaximum, value);
    }

    @Test
    public void allocatesPage1_from2() {
        final TestPages memory = new TestPages(2, 8);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 1));

        final PageAllocation allocation = uut.allocate();

        assertThat(allocation.pageNumber).isEqualTo(1L);
        assertThat(uut.pageAt(1L)).isSameAs(allocation.page);
    }

    @Test
    public void allocatesPage1_from10() {
        final TestPages memory = new TestPages(10, 8);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 9));

        final PageAllocation page = uut.allocate();

        assertThat(page.pageNumber).isEqualTo(1L);
    }


    @Test
    public void allocatesPage1_from11() {
        final TestPages memory = new TestPages(11, 8);
        final PagePool uut = new PagePool(memory, randomReturningConstant(0, 10));

        final PageAllocation page = uut.allocate();

        assertThat(page.pageNumber).isEqualTo(1L);
    }

    @Test
    public void allocatesPages_9_and_1_from11() {
        final TestPages memory = new TestPages(11, 8);

        final PagePool uut = new PagePool(memory, randomReturningConstant(9, 10));

        assertThat(uut.allocate().pageNumber).isEqualTo(10L);
        assertThat(uut.isAllocated(10L)).isTrue();
        assertThat(uut.allocate().pageNumber).isEqualTo(1L);
        assertThat(uut.isAllocated(1L)).isTrue();
    }

    @Test
    public void releasesPage() {
        final TestPages memory = new TestPages(11, 8);
        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 10));

        final long pageNumber = uut.allocate().pageNumber;
        uut.release(pageNumber);

        assertThat(uut.isAllocated(pageNumber)).isFalse();
        assertThat(uut.allocate().pageNumber).isEqualTo(9L);
    }


    @Test
    public void allocatesGivenPage() {
        final TestPages memory = new TestPages(11, 8);

        final PagePool uut = new PagePool(memory, randomReturningConstant(8, 9));

        final Page page = uut.allocate(5);
        assertThat(uut.containsPage(5)).isTrue();
        assertThat(uut.pageAt(5)).isSameAs(page);

    }
}