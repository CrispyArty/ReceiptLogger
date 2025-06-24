package com.example.receiptlogger.data;

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptDao
import java.util.concurrent.Executors
import kotlin.reflect.KClass

//val migrations: Array<AutoMigration> = [
//    AutoMigration (from = 1, to = 2, spec = ReceiptDatabase.MyAutoMigration::class)
//]

@Database(entities = [Receipt::class], version = 1)
abstract class ReceiptDatabase : RoomDatabase() {

    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var Instance: ReceiptDatabase? = null

        fun getDatabase(context: Context): ReceiptDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ReceiptDatabase::class.java, "receipt_logger_database")
                    .fallbackToDestructiveMigration(true)
                    .setQueryCallback({ sqlQuery, bindArgs ->
                        Log.d("SQL", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
