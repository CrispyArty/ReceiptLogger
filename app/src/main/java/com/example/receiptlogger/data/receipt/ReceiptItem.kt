package com.example.receiptlogger.data.receipt

data class ReceiptItem(
    val name: String,
    val count: Float,
    val itemPrice: Float,
    var totalPrice: Float = 0.0f,
)
