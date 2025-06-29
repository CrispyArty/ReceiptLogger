package com.example.receiptlogger.data.receipt

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.receiptlogger.data.ReceiptDatabase
import com.example.receiptlogger.domain.MonthCounts
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.workers.FetchAndParseReceiptWorker
import com.example.receiptlogger.workers.TAG_FETCH_AND_PARSE
import com.example.receiptlogger.workers.WORKER_KEY_RECEIPT_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDate
import java.time.LocalDateTime

interface ReceiptRepository {
    val outputWorkInfo: Flow<List<WorkInfo>>

    fun getItemStream(id: Int): Flow<Receipt?>

    fun pagingSource(): PagingSource<Int, ReceiptListItem>

    suspend fun countByMonth(datetime: LocalDateTime): MonthCounts

    suspend fun isExistByCode(qrCodeUrl: String): Boolean

    suspend fun insert(item: Receipt): Long

    suspend fun insertItems(items: List<ReceiptItem>): LongArray

    suspend fun delete(item: Receipt)

    suspend fun update(item: Receipt): Int

    suspend fun insertAndParse(item: Receipt)

    fun getWithItems(id: Int): Flow<ReceiptWithItems?>
}

class LocalReceiptRepository(
    context: Context,
    private val database: ReceiptDatabase,
) : ReceiptRepository {
    private val dao = database.receiptDao()

    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<List<WorkInfo>> =
        workManager.getWorkInfosByTagFlow(TAG_FETCH_AND_PARSE)
//            .mapNotNull {
//            if (it.isNotEmpty()) it.first() else null
//        }

    override fun getItemStream(id: Int): Flow<Receipt?> = dao.getItem(id)

    override fun getWithItems(id: Int): Flow<ReceiptWithItems?> = dao.getWithItems(id)

    override fun pagingSource(): PagingSource<Int, ReceiptListItem> =
        dao.pagingSource(FetchStatus.Completed)

    override suspend fun countByMonth(datetime: LocalDateTime): MonthCounts {
        val month = datetime.toLocalDate().withDayOfMonth(1)

        return dao.countByDateRange(
            from = month.atStartOfDay(),
            to = month.plusMonths(1).atStartOfDay()
        )
    }

    override suspend fun isExistByCode(qrCodeUrl: String): Boolean = dao.isExistByCode(qrCodeUrl)

    override suspend fun insert(item: Receipt) = dao.insert(item)

    override suspend fun insertItems(items: List<ReceiptItem>): LongArray = dao.insertItems(items)

    override suspend fun delete(item: Receipt) = dao.delete(item)

    override suspend fun update(item: Receipt) = dao.update(item)

    override suspend fun insertAndParse(item: Receipt) {
        val receiptId = dao.insert(item)

        if (receiptId <= 0) {
            return
        }

        val constraints = Constraints.Builder()
//            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        workManager.enqueue(
            OneTimeWorkRequestBuilder<FetchAndParseReceiptWorker>()
                .addTag(TAG_FETCH_AND_PARSE)
                .setInputData(createInputDataForWorkRequest(receiptId))
                .setConstraints(constraints)
                .build()
        )

    }

    private fun createInputDataForWorkRequest(receiptId: Long): Data {
        val builder = Data.Builder()
        builder.putInt(WORKER_KEY_RECEIPT_ID, receiptId.toInt())

        return builder.build()
    }
}