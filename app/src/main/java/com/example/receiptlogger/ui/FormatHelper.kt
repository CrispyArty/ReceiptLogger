package com.example.receiptlogger.ui

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.icu.text.NumberFormat
import com.example.receiptlogger.types.Money
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object FormatHelper {
    val decimalSymbols by lazy {
        DecimalFormatSymbols().apply {
            setDecimalSeparator('.')
            setGroupingSeparator(',')
        }
    }

    val currency: NumberFormat by lazy {

//        java.util.Locale("en", "md","as")
//        NumberFormat.getCurrencyInstance(ULocale("en", "md"))


        DecimalFormat("#,##0.00 'MDL'", decimalSymbols)
    }

    val currencyShort: NumberFormat by lazy {
        DecimalFormat("#,##0.00", decimalSymbols)
    }

    val dateTimeFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("dd LLL yyyy, HH:MM")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())
    }

    val dateMonthFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("LLL yyyy")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())
    }
}

val Money.formatted: String
    get() = FormatHelper.currency.format(this.toNotes())

val Money.formattedShort: String
    get() = FormatHelper.currencyShort.format(this.toNotes())



val LocalDateTime.formatted: String
    get() = this.format(FormatHelper.dateTimeFormatter)
