package io.legado.app.help.book

import org.junit.Assert.assertEquals
import org.junit.Test

class CachedExportNamingTest {

    @Test
    fun emptyIndicesHaveNoRanges() {
        assertEquals(emptyList<IntRange>(), cachedChapterRanges(emptyList()))
        assertEquals("", formatCachedChapterRanges(emptyList()))
    }

    @Test
    fun mergesContiguousIndicesAndSortsInput() {
        assertEquals(
            listOf(0..2, 5..6),
            cachedChapterRanges(listOf(5, 1, 0, 6, 2, 1)),
        )
    }

    @Test
    fun keepsSingleChapterAsSingleRange() {
        assertEquals(listOf(3..3), cachedChapterRanges(listOf(3)))
    }

    @Test
    fun formatsChapterNumbersFromOne() {
        assertEquals("1-3,6", formatCachedChapterRanges(listOf(0..2, 5..5)))
        assertEquals("4", formatCachedChapterRanges(listOf(3..3)))
    }

    @Test
    fun collapsesTooManyRangesToKeepFileNameBounded() {
        val ranges = (0 until 10).map { (it * 2)..(it * 2) }

        assertEquals("1-19(10)", formatCachedChapterRanges(ranges, maxItems = 8))
    }
}
