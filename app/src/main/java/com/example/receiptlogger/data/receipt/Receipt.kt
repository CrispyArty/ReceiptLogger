
package com.example.receiptlogger.data.receipt

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.UploadStatus
import java.time.LocalDateTime


@Entity(
    tableName = "receipts",
    indices = [
        Index(value = ["qr_code_url"], unique = true)
    ]
)
data class Receipt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "qr_code_url")
    val qrCodeUrl: String,

    @ColumnInfo(name = "cod_fiscal")
    val codFiscal: String? = null,  // Unique tax code for business: Linella, Local, N1, etc..

    @ColumnInfo(name = "registration_number")
    val registrationNumber: String? = null,  // unsure what this represent. Maybe cashier printer id?

    @ColumnInfo(name = "address")
    val address: String? = null,

    @ColumnInfo(name = "total_price")
    val totalPrice: Money? = null,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: String? = null,

    @ColumnInfo(name = "purchase_date")
    val purchaseDate: LocalDateTime? = null,

    @ColumnInfo(name = "external_id")
    val externalId: String? = null,

//    @ColumnInfo(name = "purchase_date")
//    val itemCounts: LocalDateTime? = null,

    @ColumnInfo(name = "fetch_status")
    val fetchStatus: FetchStatus = FetchStatus.Pending,

    @ColumnInfo(name = "upload_status")
    val uploadStatus: UploadStatus = UploadStatus.Pending,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)


data class ReceiptListItem(
    @Embedded val receipt: Receipt,

    val itemCount: Int
)