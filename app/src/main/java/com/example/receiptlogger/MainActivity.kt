package com.example.receiptlogger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receiptlogger.ui.ReceiptLoggerScreen
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme

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

//@Preview(
//    showBackground = true,
//    device = "spec:width=1080px,height=2400px,dpi=480,navigation=buttons", showSystemUi = true
//)
//@Composable
//fun MainScreenPreview() {
//    ReceiptLoggerTheme {
//        ReceiptLoggerApp()
//    }
//}


//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(
//    showBackground = true,
//    device = "spec:width=1080px,height=2400px,dpi=480,navigation=buttons", showSystemUi = true
//)
//@Composable
//fun MainScreenPreview1() {
//
////    println("------------------------")
////    println(LocalDateTime.now())
////    println(LocalDateTime.now(ZoneOffset.UTC))
////    println("------------------------")
//
//    val currentScreen = ReceiptLoggerScreen.valueOf(
//        ReceiptLoggerScreen.List.name
//    )
//
////    val checks by viewModel.checksUiState.collectAsState()
////    var currentCheck by rememberSaveable { mutableStateOf<Check?>(null) }
//    ReceiptLoggerTheme {
//        Scaffold(
//            topBar = {
//                MainTopAppBar(
//                    title="Receipt Logger",
////                    currentScreen = currentScreen,
////                canNavigateBack = currentScreen != ReceiptLoggerScreen.Start,
//                    canNavigateBack = true,
//                    navigateUp = { }
//                )
//            }
//        ) { innerPadding ->
////        val uiState by viewModel.uiState.collectAsState()
//            Column(modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp)) {
//
//                repeat (5) {
//                    Card(
//                        Modifier.padding(vertical = 4.dp)
//                    ) {
//                        Row(Modifier.fillMaxWidth().padding(16.dp)) {
//                            Column(Modifier.weight(1f)) {
//                                Text("Linella")
//                                Text("Price: 28$")
//                            }
//
//                            Text("16")
//                        }
//                    }
//                }
//
//
//                Box() {
//                    Row(
//                        horizontalArrangement = Arrangement.End,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .align(Alignment.BottomEnd)
//                            .padding(bottom = 30.dp)
//                    ) {
//                        FloatingActionButton(
//                            onClick = {},
//                            modifier = Modifier.padding(0.dp)
//                        ) {
//                            Icon(Icons.Rounded.Add, "New")
//                        }
//                    }
//
//                }
//
//            }
//        }
//    }
//}

