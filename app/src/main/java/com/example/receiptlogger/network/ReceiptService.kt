package com.example.receiptlogger.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ReceiptService {
    @GET("/ru/receipt-verifier/{code}")
    suspend fun getCheckByCode(@Path("code") code: String) : String

    @GET
    suspend fun getCheck(@Url url: String) : String
}