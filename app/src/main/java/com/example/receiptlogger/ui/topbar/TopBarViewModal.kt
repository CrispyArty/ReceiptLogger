@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.receiptlogger.ui.topbar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.receiptlogger.data.CustomRepository
import com.example.receiptlogger.ui.home.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


data class TopBarScreenData(
    val title: @Composable () -> Unit = {}
)

class TopBarViewModal : ViewModel() {
}


object TopBarUiState {
    private val _data = MutableStateFlow(TopBarScreenData())
    val data: StateFlow<TopBarScreenData> = _data.asStateFlow()

    fun updateTitle(title: @Composable () -> Unit) {
        _data.update { current ->
            current.copy(title = title)
        }
    }
}
//
//class ScreenTopBarViewModal(private val savedStateHandle: SavedStateHandle) : ViewModel() {
////    private val _uiTopBarData = MutableStateFlow(TopBarData(title = {}))
////    val uiTopBarData: StateFlow<TopBarData> = _uiTopBarData.asStateFlow()
////
//    var title by mutableStateOf<@Composable () -> Unit>({ }, referentialEqualityPolicy())
//
//
//    val gosuState: String? = savedStateHandle["gosu_state"]
//
//    init {
//        Log.d("gosu", "modal init - savedStateHandle:${savedStateHandle}")
//        Log.d("gosu", "modal init - gosuState:${gosuState}")
//    }
//
//    fun saveGosuState(query: String) {
//        savedStateHandle["gosu_state"] = query
//    }
//
//
//    companion object {
//        val Factory = viewModelFactory {
//            initializer {
//                ScreenTopBarViewModal(this.createSavedStateHandle())
//            }
//        }
//    }
//}