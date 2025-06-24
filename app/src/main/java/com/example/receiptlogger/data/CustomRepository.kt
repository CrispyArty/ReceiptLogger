package com.example.receiptlogger.data

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object CustomRepository {
    val latestNews: Flow<String> = flow {
        var index = 0

        while(true) {
            index++

            Log.d("gosu",  "flowEmit - $index")
            emit("String $index")
            delay(1000)
        }
    }

}