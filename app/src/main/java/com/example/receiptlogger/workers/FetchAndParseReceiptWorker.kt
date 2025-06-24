package com.example.receiptlogger.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.example.receiptlogger.R
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptItem
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.network.ReceiptHtmlParser
import com.example.receiptlogger.network.ReceiptService
import kotlinx.coroutines.flow.first

private const val TAG = "FetchAndParseReceiptWorker"

class RecordNotFoundException : RuntimeException()

class FetchAndParseReceiptWorker(
    ctx: Context,
    params: WorkerParameters,
    private val receiptRepository: ReceiptRepository,
    private val retrofitReceipt: ReceiptService,
) : CoroutineWorker(ctx, params) {

    val receiptId: Int by lazy {
        val data = inputData.getInt(WORKER_KEY_RECEIPT_ID, 0)

        require(data != 0) {
            val errorMessage =
                applicationContext.resources.getString(R.string.worker_require_input_data_key)
            Log.e(TAG, errorMessage)
            errorMessage
        }

        data
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d("gosu", "applicationContext: ${applicationContext}")

            var receipt: Receipt = receiptRepository.getItemStream(receiptId).first()
                ?: throw RecordNotFoundException()
            Log.d("gosu", "receipt: ${receipt}")

            val html = retrofitReceipt.getCheck(receipt.qrCodeUrl)
//            Log.d("gosu", "html: ${html}")

            val check = ReceiptHtmlParser(html).parse()
            Log.d("gosu", "check: ${check}")

            receipt = receipt.copy(
                codFiscal = check.description,
                registrationNumber = check.description,
                address = check.description,
                totalPrice = check.totalPrice,
                purchaseDate = check.purchaseDate,
//                items = check.items.map { it.toReceiptItem() }
            )
            Log.d("gosu", "check.items.size: ${check.items.size}")
            Log.d("gosu", "check.items: ${check.items}")

            receiptRepository.update(receipt)

            Result.success()
        } catch (throwable: Throwable) {
            Log.e(
                TAG,
                applicationContext.resources.getString(R.string.error_parsing_receipt),
                throwable
            )
            Result.failure()
        }
    }

    fun ReceiptHtmlParser.CheckItem.toReceiptItem(): ReceiptItem =
        ReceiptItem(
            name = name,
            count = count,
            itemPrice = itemPrice,
            totalPrice = totalPrice,
        )
}