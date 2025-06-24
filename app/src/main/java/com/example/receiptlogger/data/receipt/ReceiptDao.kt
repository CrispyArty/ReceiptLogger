package com.example.receiptlogger.data.receipt

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Query("SELECT * from receipts ORDER BY purchase_date DESC")
    fun getAll(): Flow<List<Receipt>>

    @Query("SELECT EXISTS(SELECT 1 from receipts WHERE qr_code_url = :qrCodeUrl)")
    suspend fun isExistByCode(qrCodeUrl: String) : Boolean

    @Query("SELECT * from receipts WHERE id = :id")
    fun getItem(id: Int) : Flow<Receipt>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Receipt) : Long

    @Update
    suspend fun update(item: Receipt)

    @Delete
    suspend fun delete(item: Receipt)
}