package com.example.receiptlogger.network

import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class ParseErrorException(message: String) : Exception(message)

class ReceiptHtmlParser(val html: String) {

    data class Check(
        var codFiscal: String = "",
        var registrationNumber: String = "",
        var address: String = "",
        var description: String = "",
        val items: MutableList<CheckItem> = mutableListOf(),
        var totalPrice: Float = 0.0f,
        var purchaseDate: String = "",
    )

    data class CheckItem(
        var name: String,
        var count: Float,
        var itemPrice: Float,
        var totalPrice: Float = 0.0f,
    )

    val doc by lazy {
        Jsoup.parse(html)
    }

    fun parse(): Check {
        var checkParseGroupIndex = 0
        var isSecondLine = false
        var isDateParsed = false

        val checkParseGroups = arrayOf<String>("desc", "items", "total", "tax", "card", "date")

//        var description = ""
//        var fullPrice = 0.0f
//        val receiptItems = mutableListOf<ReceiptItem>()

        val check = Check()

//        Log.d("Gosu", doc.select(".font-monospace").text())

        doc.select(".font-monospace > div").forEach {
//            Log.d("Gosu", "--------------------${checkParseGroupIndex}")
//            Log.d("Gosu", it.text())

            if (checkParseGroupIndex >= checkParseGroups.size) {
                return@forEach
            }

            if (it.text().contains("````````````")) {
                checkParseGroupIndex++
                return@forEach
            }

            when (checkParseGroups[checkParseGroupIndex]) {
                "desc" -> check.description += parseDescription(it)
                "items" ->
                    if (!isSecondLine) {
                        val checkItem = parseReceiptItem(it)
                        if (checkItem == null) {
                            return@forEach
                        }
                        check.items.add(checkItem)

                        isSecondLine = true
                    } else {
                        check.items.last().totalPrice = parseReceiptItemTotalPrice(it)

                        isSecondLine = false
                    }

                "total" -> check.totalPrice = parseCheckTotalPrice(it)
//                "date" -> if (!isDateParsed) {
////                    parseCheckTotalPrice(check, it.child(0).text(), it.child(1).text())
//                    isDateParsed = true
//                }
                else -> return@forEach
            }
        }
//        Log.d("Gosu", "-----===${checkItems.size}")
//        Log.d("Gosu", check.description)
        if (check.items.isEmpty()) {
            throw ParseErrorException("Check does not have items")
        }

        return check
    }


    private fun parseDescription(row: Element): String {
//        Log.d("Gosu", "addDescription: ${desc}")

        return "${row.text()}\n"
    }

    private fun parseReceiptItem(row: Element): CheckItem? {
        val name = row.child(0).text()
        val countAndPrice = row.child(1).text()

//        Log.d("Gosu", "createItem: ${name} | ${countAndPrice}")

        val list: List<String> = countAndPrice.trim().split("x")

        if (list.size != 2 || name.isEmpty()) {
            return null
        }

        val count = list[0].trim().toFloatOrNull()
        val price = list[1].trim().toFloatOrNull()
        val nameTrimmed = name.trim()

        if (count == null || price == null || name == "") {
            return null
        }

        return CheckItem(name = nameTrimmed, count = count, itemPrice = price)
    }

    private fun parseReceiptItemTotalPrice(row: Element): Float {
//        Log.d("Gosu", "addItemTotalPrice: ${price}")
        val price = row.child(1).text()
        val parsedPrice = price.trim().split(" ")[0]

        return parsedPrice.toFloatOrNull() ?: 0.0f
    }

//    private fun addCheckDate(check: Check, date: String) {
//    LocalDate.parse("${date} ${time}")
//        check.date = ""
//    }

    private fun parseCheckTotalPrice(row: Element): Float {
        val price = row.child(1).text()

        return price.toFloatOrNull() ?: 0.0f
    }

}
