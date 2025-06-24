/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.receiptlogger.data.receipt

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.receiptlogger.workers.FetchAndParseReceiptWorker
import com.example.receiptlogger.workers.TAG_FETCH_AND_PARSE
import com.example.receiptlogger.workers.WORKER_KEY_RECEIPT_ID
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun getAllStream(): Flow<List<Receipt>>

    fun getItemStream(id: Int): Flow<Receipt?>

    suspend fun isExistByCode(qrCodeUrl: String) : Boolean

    suspend fun insert(item: Receipt) : Long

    suspend fun delete(item: Receipt)

    suspend fun update(item: Receipt)

    suspend fun insertAndParse(item: Receipt)
}

class LocalReceiptRepository(
    context: Context,
    private val dao: ReceiptDao,
) : ReceiptRepository {
    private val workManager = WorkManager.getInstance(context)

    override fun getAllStream(): Flow<List<Receipt>> = dao.getAll()

    override fun getItemStream(id: Int): Flow<Receipt?> = dao.getItem(id)

    override suspend fun isExistByCode(qrCodeUrl: String): Boolean = dao.isExistByCode(qrCodeUrl)

    override suspend fun insert(item: Receipt) = dao.insert(item)

    override suspend fun delete(item: Receipt) = dao.delete(item)

    override suspend fun update(item: Receipt) = dao.update(item)

    override suspend fun insertAndParse(item: Receipt) {
        val receiptId = dao.insert(item)

        if (receiptId <= 0) {
            return
        }

        Log.d("gosu", "receiptId: $receiptId")
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