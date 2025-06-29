package com.example.receiptlogger

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.example.receiptlogger.data.AppContainer
import com.example.receiptlogger.data.DefaultAppContainer
import com.example.receiptlogger.workers.MyWorkerFactory
import java.time.ZoneId

class MainApplication : Application(), Configuration.Provider {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

//        if (BuildConfig.DEBUG) {
//            // do something for a debug build
//        }

//        this.deleteDatabase("receipt_logger_database")
//        Log.d(
//            "gosu",
//            "---onCreate writableDatabase: ${container.asdasd.openHelper.writableDatabase}"
//        )
    }


    override val workManagerConfiguration: Configuration
        get() {
            val myWorkerFactory = DelegatingWorkerFactory()
            myWorkerFactory.addFactory(MyWorkerFactory(container))

            return Configuration.Builder()
//                .setMinimumLoggingLevel(Log.INFO)
                .setWorkerFactory(myWorkerFactory)
                .build()
        }
}
