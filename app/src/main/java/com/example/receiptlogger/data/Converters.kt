package com.example.receiptlogger.data

import androidx.room.TypeConverter
import com.example.receiptlogger.MainApplication
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.toMoney
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.TimeZone

class Converters {

    @TypeConverter
    fun toDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            // MainApplication.receiptTimeZoneId
            val zoneId = TimeZone.getDefault().toZoneId()

            Instant.ofEpochSecond(value).atZone(zoneId).toLocalDateTime()
        }
    }

    @TypeConverter
    fun fromDateTime(datetime: LocalDateTime?): Long? {
        return datetime?.let {
            val zoneId = TimeZone.getDefault().toZoneId()
//            ZonedDateTime.from().toEpochSecond()
//            datetime.toEpochSecond(zoneId.rules.getOffset(datetime))
            datetime.atZone(zoneId).toEpochSecond()
        }
    }

//    @TypeConverter
//    fun fromZonedDateTime(datetime: ZonedDateTime?): Long? {
//        return datetime?.toEpochSecond()
//    }

    @TypeConverter
    fun toMoney(value: Long?): Money? {
        return value?.toMoney()
    }

    @TypeConverter
    fun fromMoney(money: Money?): Long? {
        return money?.cents

    }
}