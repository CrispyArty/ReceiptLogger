package com.example.receiptlogger.data;

import android.content.Context
import android.util.Log
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase;
import com.example.receiptlogger.data.receipt.Receipt
import java.util.concurrent.Executors

//@Database(entities = arrayOf(Receipt:class), version = 1, exportSchema = false)
@Database(entities = [Receipt::class], version = 1)
abstract class ReceiptDatabase : RoomDatabase() {

//    abstract fun receiptDao(): ItemDao


    companion object {
        @Volatile
        private var Instance: ReceiptDatabase? = null

        fun getDatabase(context: Context): ReceiptDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ReceiptDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration()
                    .setQueryCallback({ sqlQuery, bindArgs ->
                        Log.d("SQL", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
