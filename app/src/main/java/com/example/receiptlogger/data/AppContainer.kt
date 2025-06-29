
package com.example.receiptlogger.data

import android.content.Context
import com.example.receiptlogger.data.receipt.LocalReceiptRepository
import com.example.receiptlogger.network.ReceiptService
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

interface AppContainer {
    val receiptRepository: LocalReceiptRepository
    val receiptRetrofitService: ReceiptService
    val asdasd: ReceiptDatabase
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val receiptUrlBase = "https://mev.sfs.md"
    private val receiptUrlQrPath = "/ru/receipt-verifier/{qrCode}"
    override val asdasd = ReceiptDatabase.getDatabase(context)


    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(receiptUrlBase)
        .build()

    override val receiptRetrofitService: ReceiptService by lazy {
        retrofit.create(ReceiptService::class.java)
    }

    override val receiptRepository: LocalReceiptRepository by lazy {
        LocalReceiptRepository(context = context, database = ReceiptDatabase.getDatabase(context))
    }
}
