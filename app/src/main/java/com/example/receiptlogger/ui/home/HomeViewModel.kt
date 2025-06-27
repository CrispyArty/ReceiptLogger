package com.example.receiptlogger.ui.home

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receiptlogger.UNSUBSCRIBE_TIMEOUT
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptListItem
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.types.UploadStatus
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

sealed interface ListStatus {
    object Loading : ListStatus
    object Success : ListStatus
}

//data class ReceiptUiListItem (
//    val uploadStatus: UploadStatus,
//    val dateTime: LocalDateTime
//)

//interface GroupedList : Map<LocalDate, List<ReceiptListItem>>
//data class GroupedList (
//    val key: LocalDate,
//    val list: List<ReceiptListItem>
//)

//var q = emptyMap<LocalDate, List<ReceiptListItem>>()

data class HomeUiState(
    val listStatus: ListStatus = ListStatus.Loading,
    val groupedList: Map<LocalDate, List<ReceiptListItem>> = emptyMap(),
)

//val f = flowOf(1,2,3)

class HomeViewModel(private val receiptRepository: ReceiptRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> = receiptRepository.getAllStream()
        .map { items ->
            items.groupBy() {
                it.receipt.purchaseDate?.toLocalDate()?.withDayOfMonth(1) ?: LocalDate.now()
            }
        }
        .map {
            Log.d("gosu", "items: ${it}")
            HomeUiState(
                groupedList = it,
                listStatus = ListStatus.Success,
            )
        }
        .shareIn()
        .stateIn(
            scope = viewModelScope,
            initialValue = HomeUiState(),
            started = WhileSubscribed(UNSUBSCRIBE_TIMEOUT)
        )
}

