
package com.example.receiptlogger.data.receipt

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val items: List<ReceiptItem>,
    val totalPrice: Float,
    val date: LocalDateTime = LocalDateTime.now()
)
