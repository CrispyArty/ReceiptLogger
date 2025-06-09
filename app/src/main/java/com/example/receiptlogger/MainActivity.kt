package com.example.receiptlogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.receiptlogger.ui.ReceiptLoggerApp
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReceiptLoggerTheme {
                ReceiptLoggerApp()
            }
        }
    }
}

@Preview(showBackground = true,
    device = "spec:width=1080px,height=2400px,dpi=480,navigation=buttons", showSystemUi = true)
@Composable
fun MainScreenPreview() {

//    println("------------------------")
//    println(LocalDateTime.now())
//    println(LocalDateTime.now(ZoneOffset.UTC))
//    println("------------------------")

    ReceiptLoggerTheme {
        ReceiptLoggerApp()
    }
}