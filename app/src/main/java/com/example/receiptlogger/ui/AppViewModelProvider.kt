package com.example.receiptlogger.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.receiptlogger.MainApplication
import com.example.receiptlogger.ui.home.HomeViewModel
import com.example.receiptlogger.ui.scannerButton.QrCodeScanViewModal

object AppViewModelProvider {
    val Factory = viewModelFactory {
//        initializer {
//            ReceiptDetailsViewModel(
//                savedStateHandle = this.createSavedStateHandle(),
//                itemsRepository = inventoryApplication().container.itemsRepository
//            )
//        }

        // Initializer for HomeViewModel

        initializer {
            QrCodeScanViewModal(
                receiptRepository = mainApplication().container.receiptRepository
            )
        }

        initializer {
            HomeViewModel(
                pager = Pager(
                    config = PagingConfig(
                        pageSize = 10,
                        initialLoadSize = 20,
//                        maxSize = 20
                    ),
                    pagingSourceFactory = { mainApplication().container.receiptRepository.pagingSource() }
                ),
                receiptRepository = mainApplication().container.receiptRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.mainApplication(): MainApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
