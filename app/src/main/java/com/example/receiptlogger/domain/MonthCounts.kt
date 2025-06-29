package com.example.receiptlogger.domain

import com.example.receiptlogger.types.Money

data class MonthCounts (
    val totalPrice: Money,
    val itemCount: Int,
)