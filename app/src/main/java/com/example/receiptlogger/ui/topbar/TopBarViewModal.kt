@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.receiptlogger.ui.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class TopBarScreenData(
    val title: @Composable () -> Unit = {}
)


object TopBarUiState {
    private val _data = MutableStateFlow(TopBarScreenData())
    val data: StateFlow<TopBarScreenData> = _data.asStateFlow()

    internal fun updateTitle(title: @Composable () -> Unit) {
        _data.update { current ->
            current.copy(title = title)
        }
    }
}

class TopBarViewModal : ViewModel() {
}
