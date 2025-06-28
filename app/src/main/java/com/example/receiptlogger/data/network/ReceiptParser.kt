package com.example.receiptlogger.data.network

import androidx.compose.ui.text.substring
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.text.replace


class ParseErrorException(message: String) : Exception(message)

class ReceiptParser(val html: String) {

    val dateFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("'DATA'dd.MM.yyyy'ORA'HH:mm:ss")
    }

    data class Check(
        var codFiscal: String = "",
        var registrationNumber: String = "",
        var address: String = "",
        var header: String = "",
        val items: MutableList<CheckItem> = mutableListOf(),
        var totalPrice: Float = 0.0f,
        var purchaseDate: LocalDateTime = LocalDateTime.now().minusMonths(1),
        var html: String = "",
        var paymentMethod: String = "",
        var id: String = ""
    )

    data class CheckItem(
        var name: String,
        var count: Double,
        var itemPrice: Float,
        var totalPrice: Float = 0.0f,
    )

    val doc by lazy {
        Jsoup.parse(html)
    }

    fun parse(): Check {
        var checkParseGroupIndex = 0
        var isSecondLine = false
        val checkParseGroups = arrayOf<String>(
            "header",
            "items",
            "total",
            "taxes",
            "paymentMethod",
            "infoFooter",
            "footer"
        )
        var preFooterIndex = 0
        var headerIndex = 0

        val check = Check(
            html = doc.select(".font-monospace > div").text()
        )

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
                "header" -> {
                    when (headerIndex) {
                        0 -> check.header = it.text()
                        1 -> check.codFiscal = parseFieldValue(it, "COD FISCAL:")
                        2 -> check.address = it.text()
                        3 -> check.registrationNumber =
                            parseFieldValue(it, "NUMARUL DE ÎNREGISTRARE:")

                        else -> check.header += "\n${it.text()}"
                    }

                    headerIndex++
                }

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
                "paymentMethod" -> {
                    check.paymentMethod = it.child(0).text().trim()
                }
                "infoFooter" -> {
                    when (preFooterIndex) {
                        0 -> check.purchaseDate = parsePurchaseDate(it)
                    }
                    preFooterIndex++
                }
                "footer" -> {
                    check.id = it.text().trim()
                }

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

    private fun parseFieldValue(row: Element, name: String): String {
        return "${row.text().replace(name, "").trim()}\n"
    }

    private fun parseReceiptItem(row: Element): CheckItem? {
        val name = row.child(0).text()
        val countAndPrice = row.child(1).text()

//        Log.d("Gosu", "createItem: ${name} | ${countAndPrice}")

        val list: List<String> = countAndPrice.trim().split("x")

        if (list.size != 2 || name.isEmpty()) {
            return null
        }

        val count = list[0].trim().toDoubleOrNull()
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

    private fun parsePurchaseDate(row: Element): LocalDateTime {
        val str = row.text().replace("\\s".toRegex(), "")

        return LocalDateTime.parse(str, dateFormatter)
    }

    private fun parseCheckTotalPrice(row: Element): Float {
        val price = row.child(1).text()

        return price.toFloatOrNull() ?: 0.0f
    }

}
