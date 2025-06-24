
package com.example.receiptlogger.data.receipt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "receipts",
    indices = [
        Index(value = ["qr_code_url"], unique = true)
    ]
)
data class Receipt(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "qr_code_url") val qrCodeUrl: String,

    // Unique tax code for business: Linella, Local, N1, etc..
    @ColumnInfo(name = "cod_fiscal") val codFiscal: String? = null,
    // unsure what this represent. Maybe cashier printer id?
    @ColumnInfo(name = "registration_number") val registrationNumber: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
//    val items: List<ReceiptItem> = emptyList<ReceiptItem>(),
    @ColumnInfo(name = "total_price") val totalPrice: Float? = null,
    @ColumnInfo(name = "purchase_date") val purchaseDate: String? = null
)
