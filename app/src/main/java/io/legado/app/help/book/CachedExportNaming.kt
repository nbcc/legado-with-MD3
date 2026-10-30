package io.legado.app.help.book

/**
 * 把已缓存章节的 `BookChapter.index`（0 起）合并成连续闭区间。
 *
 * 同一本书可能分多次缓存不同区段，导出文件名需要带上这些区段来区分文件。
 */
internal fun cachedChapterRanges(chapterIndices: List<Int>): List<IntRange> {
    if (chapterIndices.isEmpty()) return emptyList()
    val sorted = chapterIndices.distinct().sorted()
    val ranges = ArrayList<IntRange>()
    var start = sorted.first()
    var end = start
    for (index in sorted.drop(1)) {
        if (index == end + 1) {
            end = index
        } else {
            ranges.add(start..end)
            start = index
            end = index
        }
    }
    ranges.add(start..end)
    return ranges
}

/**
 * 导出文件名里的章节范围文本，章节号从 1 开始展示。
 *
 * 区段超过 [maxItems] 个时收敛成「首章-末章(已缓存章数)」，避免极端情况下
 * 文件名超过文件系统的长度限制。
 */
internal fun formatCachedChapterRanges(
    ranges: List<IntRange>,
    maxItems: Int = 8,
): String {
    if (ranges.isEmpty()) return ""
    fun IntRange.display() = if (first == last) "${first + 1}" else "${first + 1}-${last + 1}"
    if (ranges.size <= maxItems) return ranges.joinToString(",") { it.display() }
    val count = ranges.sumOf { it.last - it.first + 1 }
    return "${ranges.first().first + 1}-${ranges.last().last + 1}($count)"
}
