package com.example.receiptlogger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.receiptlogger.data.receipt.ReceiptListItem
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.domain.ReceiptListItemUiModel
import com.example.receiptlogger.domain.toUiModel
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.toMoney
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime


sealed interface UiPageItem {
    data class Item(val item: ReceiptListItemUiModel) : UiPageItem
    data class MonthHeader(val dateTime: LocalDateTime, val totalPrice: Money? = null) : UiPageItem
}

class HomeViewModel(
    pager: Pager<Int, ReceiptListItem>,
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    val mapPagerFlow: suspend (value: PagingData<ReceiptListItem>) -> PagingData<UiPageItem> =
        { items ->
            items
                .map {
                    UiPageItem.Item(it.toUiModel())
                }
                .insertSeparators<UiPageItem.Item, UiPageItem> { before, after ->
                    when {
                        after == null -> null
                        before == null -> UiPageItem.MonthHeader(
                            dateTime = after.item.purchaseDate,
                            totalPrice = receiptRepository.countByMonth(after.item.purchaseDate)
                                .toMoney(),
                        )
//                        shouldSeparate(before, after) -> null
                        shouldSeparate(before, after) -> UiPageItem.MonthHeader(
                            dateTime = after.item.purchaseDate,
                            totalPrice = receiptRepository.countByMonth(after.item.purchaseDate)
                                .toMoney(),
                        )

                        else -> null
                    }
                }
        }

    val receiptPager = pager
        .flow
        .map(mapPagerFlow)
        .cachedIn(viewModelScope)

    fun shouldSeparate(before: UiPageItem, after: UiPageItem): Boolean {
        if (!(before is UiPageItem.Item && after is UiPageItem.Item)) {
            return false
        }

        val beforeDate = before.item.purchaseDate.toLocalDate()
        val afterDate = after.item.purchaseDate.toLocalDate()

        return when {
            beforeDate.year != afterDate.year -> true
            beforeDate.month != afterDate.month -> true
            else -> false
        }
    }
}


