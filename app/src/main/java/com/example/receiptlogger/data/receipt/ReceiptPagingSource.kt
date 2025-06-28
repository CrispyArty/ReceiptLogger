package com.example.receiptlogger.data.receipt

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

private const val STARTING_KEY = 0
private const val LOAD_DELAY_MILLIS = 3_000L

private val firstArticleCreatedTime = LocalDateTime.now()


//data class GroupedList (
//    val datetimeMonth: LocalDate,
//    val monthPriceCents: Int,
//    val list: List<ReceiptListItem>
//)

data class Article(
    val id: Int,
    val title: String,
    val description: String,
    val created: LocalDateTime,
)

val Article.createdText: String get() = articleDateFormatter.format(created)

private val articleDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")


class ArticlePagingSource: PagingSource<Int, Article>()  {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val startKey = params.key ?: STARTING_KEY

        val range = startKey.until(startKey + params.loadSize)

//      loadsize = 20
//      0 20 40 60
//
        if (startKey != STARTING_KEY) delay(LOAD_DELAY_MILLIS)

        return LoadResult.Page(
            data = range.map { number ->
                Article(
                    id = number,
                    title = "Article $number",
                    description = "This describes article $number",
                    created = firstArticleCreatedTime.minusDays(number.toLong())
                )
            },
            prevKey = when (startKey) {
                STARTING_KEY -> null
                else -> ensureValidKey(key = range.first - params.loadSize)
            },
            nextKey = range.last + 1
        )

        TODO("Not yet implemented")
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // In our case we grab the item closest to the anchor position
        // then return its id - (state.config.pageSize / 2) as a buffer
        val anchorPosition = state.anchorPosition ?: return null
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = article.id - (state.config.pageSize / 2))
    }

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}