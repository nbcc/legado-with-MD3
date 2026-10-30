package io.legado.app.ui.book.read

import android.content.Context
import io.legado.app.R
import io.legado.app.constant.AppLog
import io.legado.app.help.book.BookHelp
import io.legado.app.help.book.isLocal
import io.legado.app.model.ReadBook
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 阅读界面的「导出已缓存内容」入口。
 *
 * 只做可用性判断与路由选择：章节筛选、EPUB 生成和带缓存区段的文件名都在
 * `ExportBookService`，避免 UI 层复制导出实现。
 */
class CachedExportDelegate(
    private val scope: CoroutineScope,
    private val context: Context,
    private val emitEffect: (ReadBookEffect) -> Unit,
) {

    fun exportCachedEpub() {
        val book = ReadBook.book ?: return
        if (book.isLocal) {
            emitEffect(ReadBookEffect.ShowToast(context.getString(R.string.only_online_books)))
            return
        }
        scope.launch(Dispatchers.IO) {
            val cachedCount = try {
                BookHelp.countCachedContentChapters(book)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLog.put("统计已缓存章节失败", e)
                emitEffect(ReadBookEffect.ShowToast(context.getString(R.string.error)))
                return@launch
            }
            if (cachedCount <= 0) {
                emitEffect(
                    ReadBookEffect.ShowToast(
                        context.getString(R.string.no_cached_content_to_export)
                    )
                )
            } else {
                emitEffect(ReadBookEffect.OpenCachedEpubExportPicker(book.bookUrl))
            }
        }
    }
}
