package com.example.receiptlogger

import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.receiptlogger.model.Check
import com.example.receiptlogger.ui.ReceiptLoggerApp
import com.example.receiptlogger.ui.ReceiptLoggerAppBar
import com.example.receiptlogger.ui.ReceiptLoggerScreen
import com.example.receiptlogger.ui.screens.CheckScreen
import com.example.receiptlogger.ui.screens.ListScreen
import com.example.receiptlogger.ui.screens.QRCodeButton
import com.example.receiptlogger.ui.theme.CustomShape
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d("gosu", "onCreate")
        setContent {
            ReceiptLoggerTheme {
                ReceiptLoggerApp()
            }
        }
    }
}

//@Preview(showBackground = true,
//    device = "spec:width=1080px,height=2400px,dpi=480,navigation=buttons", showSystemUi = true)
//@Composable
//fun MainScreenPreview() {
//
////    println("------------------------")
////    println(LocalDateTime.now())
////    println(LocalDateTime.now(ZoneOffset.UTC))
////    println("------------------------")
//
//    ReceiptLoggerTheme {
//        ReceiptLoggerApp()
//    }
//}


@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=2400px,dpi=480,navigation=buttons", showSystemUi = true
)
@Composable
fun MainScreenPreview() {

//    println("------------------------")
//    println(LocalDateTime.now())
//    println(LocalDateTime.now(ZoneOffset.UTC))
//    println("------------------------")

    val currentScreen = ReceiptLoggerScreen.valueOf(
        ReceiptLoggerScreen.List.name
    )

//    val checks by viewModel.checksUiState.collectAsState()
//    var currentCheck by rememberSaveable { mutableStateOf<Check?>(null) }
    ReceiptLoggerTheme {
        Scaffold(
            topBar = {
                ReceiptLoggerAppBar(
                    currentScreen = currentScreen,
//                canNavigateBack = currentScreen != ReceiptLoggerScreen.Start,
                    canNavigateBack = true,
                    navigateUp = { }
                )
            }
        ) { innerPadding ->
//        val uiState by viewModel.uiState.collectAsState()
            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)) {

                repeat (5) {
                    Card(
                        Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Linella")
                                Text("Price: 28$")
                            }

                            Text("16")
                        }
                    }
                }


                Box() {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 30.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {},
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Icon(Icons.Rounded.Add, "New")
                        }
                    }

                }

            }
        }
    }
}