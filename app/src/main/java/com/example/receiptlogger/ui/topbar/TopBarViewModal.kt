@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.receiptlogger.ui.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.types.FetchStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


data class TopBarScreenData(
    val title: @Composable () -> Unit = {},
    val fab: @Composable () -> Unit = {},
)


object TopBarUiState {
    private val _data = MutableStateFlow(TopBarScreenData())
    val data: StateFlow<TopBarScreenData> = _data.asStateFlow()

    internal fun updateTitle(title: @Composable () -> Unit) {
        _data.update { current ->
            current.copy(title = title)
        }
    }

    internal fun updateFAB(fab: @Composable () -> Unit) {
        _data.update { current ->
            current.copy(fab = fab)
        }
    }
}

sealed interface JobStatusUiState {
    object Idle : JobStatusUiState
    object Loading : JobStatusUiState
    object Complete : JobStatusUiState
}


class TopBarViewModal(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    var running: UUID? by mutableStateOf(null)

    val missingJobsReceipts: StateFlow<List<Int>> = receiptRepository
        .getIdsByStatus(FetchStatus.Pending)
        .map { listOfIds ->
            delay(2000)
            listOfIds
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000)
        )

    val jobStatusUiState: StateFlow<JobStatusUiState> = receiptRepository.outputWorkInfo
        .map { list ->
            val work =
                list.find { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }

            when (work) {
                is WorkInfo -> {
                    running = work.id
                    JobStatusUiState.Loading
                }

                else -> {
                    when (list.find { it.id == running }?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            running = null
                            JobStatusUiState.Complete
                        }

                        else -> JobStatusUiState.Idle
                    }

                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = JobStatusUiState.Idle,
            started = SharingStarted.WhileSubscribed(5_000)
        )


    fun enqueueMissing() {
        viewModelScope.launch {
            receiptRepository.queueAllPending()
        }

    }

}
