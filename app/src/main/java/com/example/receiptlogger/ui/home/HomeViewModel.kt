package com.example.receiptlogger.ui.home

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.TerminalSeparatorType
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.receiptlogger.UNSUBSCRIBE_TIMEOUT
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptListItem
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.domain.ReceiptListItemUiModel
import com.example.receiptlogger.domain.toUiModel
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.UploadStatus
import com.example.receiptlogger.types.toMoney
import com.example.receiptlogger.ui.mainApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

//sealed interface ListStatus {
//    object Loading : ListStatus
//    object Success : ListStatus
//}


sealed interface UiPageItem {
    data class Item(val item: ReceiptListItemUiModel) : UiPageItem
    data class MonthHeader(val dateTime: LocalDate, val totalPrice: Money? = null) : UiPageItem
}

class HomeViewModel(
    pager: Pager<Int, ReceiptListItem>,
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    val receiptPager = pager
        .flow
        .map { items ->
//            Log.d("gosupager--viewmodel", "!!!!!!!!!!!!!!!!!!!!items: ${items} / ${items}")
//            var i = 0
            items
                .map {
//                    Log.d("gosupager--viewmodel", "=====${i++}")
                    UiPageItem.Item(it.toUiModel())
                }
                .insertSeparators<UiPageItem.Item, UiPageItem>{ before, after ->
//                    Log.d("gosupager--viewmodel", "--------insertSeparators-----------")
//                    Log.d("gosupager--viewmodel", "before: ${before?.item?.receipt?.id}")
//                    Log.d("gosupager--viewmodel", "after: ${after?.item?.receipt?.id}")

//                    delay(5000)
//                    Log.d("gosupager--viewmodel", "delay: $after")

                    when {
                        after == null -> null
                        before == null -> UiPageItem.MonthHeader(
                            dateTime = after.item.purchaseDate.toLocalDate()
                                ?: LocalDate.now(),
                            totalPrice = receiptRepository.countByMonth(after.item.purchaseDate).toMoney(),
                        )
//                        shouldSeparate(before, after) -> null
                        shouldSeparate(before, after) -> UiPageItem.MonthHeader(
                            dateTime = after.item.purchaseDate.toLocalDate()
                                ?: LocalDate.now(),
                            totalPrice = receiptRepository.countByMonth(after.item.purchaseDate).toMoney(),
                        )

                        else -> null
                    }
                }
        }

        .cachedIn(viewModelScope)
//


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

//    val homeUiState: StateFlow<HomeUiState> = receiptRepository.getAllStream()
//        .map { items ->
//            items.groupBy() {
//                it.receipt.purchaseDate?.toLocalDate()?.withDayOfMonth(1) ?: LocalDate.now()
//            }
//        }
//        .map {
//            Log.d("gosu", "items: ${it}")
//            HomeUiState(
//                groupedList = it,
//                listStatus = ListStatus.Success,
//            )
//        }
//        .stateIn(
//            scope = viewModelScope,
//            initialValue = HomeUiState(),
//            started = WhileSubscribed(UNSUBSCRIBE_TIMEOUT)
//        )
}


