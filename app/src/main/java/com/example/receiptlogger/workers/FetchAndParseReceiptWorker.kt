package com.example.receiptlogger.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.receiptlogger.R
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptItem
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.network.ReceiptParser
import com.example.receiptlogger.network.ReceiptService
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.types.toMoney
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
            var receipt: Receipt = receiptRepository.getItemStream(receiptId).first()
                ?: throw RecordNotFoundException()
            val html = retrofitReceipt.getCheck(receipt.qrCodeUrl)
            val check = ReceiptParser(html).parse()

            receipt = receipt.copy(
                codFiscal = check.codFiscal,
                registrationNumber = check.registrationNumber,
                address = check.address,
                totalPrice = check.totalPrice.toMoney(),
                purchaseDate = check.purchaseDate,
                paymentMethod = check.paymentMethod,
                externalId = check.id,
            )


            receiptRepository.update(receipt)

            receiptRepository.insertItems(check.items.map {
                ReceiptItem(
                    name = it.name,
                    count = it.count,
                    itemPrice = it.itemPrice.toMoney(),
                    totalPrice = it.totalPrice.toMoney(),
                    receiptId = receipt.id
                )
            })

            receiptRepository.update(receipt.copy(
                fetchStatus = FetchStatus.Completed
            ))

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
}