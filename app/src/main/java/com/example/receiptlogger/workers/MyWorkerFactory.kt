package com.example.receiptlogger.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.receiptlogger.data.AppContainer

class MyWorkerFactory(private val container: AppContainer) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (workerClassName) {
            FetchAndParseReceiptWorker::class.java.name ->
                FetchAndParseReceiptWorker(
                    appContext,
                    workerParameters,
                    container.receiptRepository,
                    container.receiptRetrofitService
                )
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }

    }
}