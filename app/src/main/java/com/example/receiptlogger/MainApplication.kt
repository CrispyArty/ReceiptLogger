package com.example.receiptlogger

import android.app.Application
import androidx.compose.material3.Text
import com.example.receiptlogger.data.AppContainer
import com.example.receiptlogger.data.DefaultAppContainer

class MainApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
