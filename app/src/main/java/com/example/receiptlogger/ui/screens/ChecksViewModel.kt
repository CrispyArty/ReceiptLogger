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
package com.example.receiptlogger.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.receiptlogger.model.Check
import com.example.receiptlogger.model.CheckItem
import com.example.receiptlogger.network.CheckApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate


/**
 * UI state for the Home screen
 */
sealed interface RequestUiState {
    data class Success(val content: String) : RequestUiState
    data class Error(val error: String, val name: String) : RequestUiState
    object Loading : RequestUiState
}

class ParseErrorException : RuntimeException()

class ChecksViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var requestUiState: RequestUiState? by mutableStateOf(null)
        private set

    private val _checksUiState = MutableStateFlow(listOf<Check>())
    val checksUiState: StateFlow<List<Check>> = _checksUiState.asStateFlow()

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
//    init {
////        Log.d("Gosu", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//    }

    fun showCamera() {

    }

    fun getCheck(url: String) {
        if (url == "Error") {
            requestUiState =
                RequestUiState.Error(error = "Fail to fetch QR code", "QrCodeFetchException")
            return
        }

        viewModelScope.launch {
//            Log.d("Gosu", "${Thread.currentThread().name} - runBlocking function")

            requestUiState = RequestUiState.Loading
            requestUiState = try {
//                val qrCode = url.split("/").last()

//                Log.d("Gosu", "==========$url")
                val html = CheckApi.retrofitService.getCheck(url)

                val check = addCheckFromJsoupDoc(doc = Jsoup.parse(html))

                _checksUiState.update { currentState ->
                    currentState + listOf(check)
                }

                RequestUiState.Success(
                    "Success: Example!!"
                )
            } catch (e: IOException) {
                RequestUiState.Error(error = e.message.toString(), name = "IOException")
            } catch (e: HttpException) {
                RequestUiState.Error(error = e.message.toString(), name = "HttpException")
            } catch (e: ParseErrorException) {
                RequestUiState.Error(error = e.message.toString(), name = "ParseErrorException")
            }
        }
    }

    private fun addCheckFromJsoupDoc(doc: Document): Check {
        var checkParseGroupIndex = 0
        var isSecondLine = false
        var isDateParsed = false

        val checkParseGroups = arrayOf<String>("desc", "items", "total", "tax", "card", "date")

        var description = ""
        var fullPrice = 0.0f
        val checkItems = mutableListOf<CheckItem>()

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
                "desc" -> description += parseDescription(it)
                "items" ->
                    if (!isSecondLine) {
                        val checkItem = parseReceiptItem(it)
                        if (checkItem == null) {
                            return@forEach
                        }
                        checkItems.add(checkItem)

                        isSecondLine = true
                    } else {
                        checkItems.last().totalPrice = parseReceiptItemTotalPrice(it)

                        isSecondLine = false
                    }

                "total" -> fullPrice = parseCheckTotalPrice(it)
//                "date" -> if (!isDateParsed) {
////                    parseCheckTotalPrice(check, it.child(0).text(), it.child(1).text())
//                    isDateParsed = true
//                }
                else -> return@forEach
            }
        }
//        Log.d("Gosu", "-----===${checkItems.size}")
//        Log.d("Gosu", check.description)
        if (checkItems.isEmpty()) {
            throw ParseErrorException()
        }

        return Check(description, checkItems, fullPrice)
    }

    private fun parseDescription(row: Element) : String {
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

    private fun parseReceiptItemTotalPrice(row: Element) : Float {
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
