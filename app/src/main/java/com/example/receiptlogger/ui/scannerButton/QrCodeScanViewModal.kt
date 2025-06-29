package com.example.receiptlogger.ui.scannerButton

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


enum class CreateError {
    ALREADY_EXISTS
}


class QrCodeScanViewModal(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    var error: CreateError? by mutableStateOf(null)
        private set

    fun clearError() {
        error = null
    }

    fun onScan(qrCodeUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                if (receiptRepository.isExistByCode(qrCodeUrl)) {
                    error = CreateError.ALREADY_EXISTS

                    return@withContext
                }

                receiptRepository.insertAndParse(Receipt(
                    qrCodeUrl = qrCodeUrl,
                ))
            }

        }
    }
}