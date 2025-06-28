package com.example.receiptlogger.data.receipt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.receiptlogger.types.Money


@Entity(
    tableName = "receipt_items"
)
data class ReceiptItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name", index = true)
    val name: String,

    @ColumnInfo(name = "count")
    val count: Double,

    @ColumnInfo(name = "itemPrice")
    val itemPrice: Money,

    @ColumnInfo(name = "totalPrice")
    val totalPrice: Money,

    @ColumnInfo(name = "receipt_id", index = true)
    val receiptId: Int,
)
