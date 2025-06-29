package com.example.receiptlogger.ui.receipt

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.data.receipt.ReceiptWithItems
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException



sealed interface DetailsStatus {
    object Loading : DetailsStatus
    data class Success(val receiptWithItems: ReceiptWithItems) : DetailsStatus
}

class ReceiptDetailsViewModel(
    private val receiptRepository: ReceiptRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[ReceiptDetailsDestination.itemIdArg])

    val uiState: StateFlow<DetailsStatus> =
        receiptRepository.getWithItems(itemId)
            .filterNotNull()
            .map {
                DetailsStatus.Success(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DetailsStatus.Loading
            )

    suspend fun delete() {
        if (uiState.value is DetailsStatus.Success) {
            receiptRepository.delete((uiState.value as DetailsStatus.Success).receiptWithItems.receipt)
        }
    }
}