package com.example.receiptlogger.data.store

import com.example.receiptlogger.data.receipt.Receipt

object StoreData {
    val list: List<Store> = listOf(
        Store(
            codFiscal = "1010600022460",
            name = "Linella"
        ),

        Store(
            codFiscal = "1002600011694",
            name = "Local"
        ),
    )

    fun findName(codFiscal: String): String {
        return list.find { it.codFiscal == codFiscal }?.name ?: codFiscal
    }

    fun findName(receipt: Receipt): String {
        return if (receipt.codFiscal != null) {
            findName(receipt.codFiscal)
        } else {
            "Not found"
        }
    }
}