package com.example.receiptlogger.data.receipt

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.receiptlogger.domain.MonthCounts
import com.example.receiptlogger.types.FetchStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


@Dao
interface ReceiptDao {
    @Query("SELECT receipts.id from receipts WHERE receipts.fetch_status == :status")
    fun getIdsByStatus(status: FetchStatus = FetchStatus.Pending): Flow<List<Int>>

    @Query(
        "SELECT receipts.*, count(receipt_items.id) as itemCount " +
                "FROM receipts " +
                "LEFT JOIN receipt_items ON receipts.id = receipt_items.receipt_id " +
                "WHERE receipts.fetch_status == :fetchStatus " +
                "GROUP BY receipts.id " +
                "ORDER BY purchase_date DESC"
    )
    fun pagingSource(fetchStatus: FetchStatus): PagingSource<Int, ReceiptListItem>

    @Query("SELECT EXISTS(SELECT 1 from receipts WHERE qr_code_url = :qrCodeUrl)")
    suspend fun isExistByCode(qrCodeUrl: String): Boolean

    @Query(
        "SELECT SUM(receipts.total_price) as totalPrice, count(receipts.id) as itemCount " +
                "FROM receipts " +
                "WHERE receipts.fetch_status == :fetchStatus " +
                "AND receipts.purchase_date >= :from AND receipts.purchase_date <= :to"
    )
    suspend fun countByDateRange(
        from: LocalDateTime,
        to: LocalDateTime,
        fetchStatus: FetchStatus = FetchStatus.Completed,
    ): MonthCounts

    @Query("SELECT * from receipts WHERE id = :id")
    fun getItem(id: Int): Flow<Receipt>

    @Query(
        "SELECT * " +
                "FROM receipts " +
                "LEFT JOIN receipt_items ON receipts.id = receipt_items.receipt_id " +
                "WHERE receipts.id = :id"
    )
    fun getWithItems(id: Int): Flow<ReceiptWithItems?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Receipt): Long

    @Transaction
    @Insert
    suspend fun insertItems(items: List<ReceiptItem>): LongArray

    @Update
    suspend fun update(item: Receipt): Int

    @Delete
    suspend fun delete(item: Receipt)
}