package com.example.receiptlogger.domain

import com.example.receiptlogger.data.receipt.ReceiptListItem
import com.example.receiptlogger.data.store.StoreData
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.UploadStatus
import java.time.LocalDateTime

data class ReceiptListItemUiModel (
    val id: Int,
    val storeName: String,
    val address: String,
    val uploadStatus: UploadStatus,
    val purchaseDate: LocalDateTime,
    val totalPrice: Money,
    val itemCount: Int,
)

fun ReceiptListItem.toUiModel() :ReceiptListItemUiModel = ReceiptListItemUiModel(
    id = this.receipt.id,
    storeName = StoreData.findName(this.receipt),
    address = this.receipt.address ?: "",
    uploadStatus = this.receipt.uploadStatus,
    purchaseDate = this.receipt.purchaseDate ?: LocalDateTime.now(),
    totalPrice = this.receipt.totalPrice ?: Money(0),
    itemCount = this.itemCount
)