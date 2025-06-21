
package com.example.receiptlogger.data

import com.example.receiptlogger.network.ReceiptService
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

interface AppContainer {
}

class DefaultAppContainer : AppContainer {
    private val receiptUrlBase = "https://mev.sfs.md"
    private val receiptUrlQrPath = "/ru/receipt-verifier/{qrCode}"


    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(receiptUrlBase)
        .build()

    private val retrofitService: ReceiptService by lazy {
        retrofit.create(ReceiptService::class.java)
    }
}
