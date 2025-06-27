package com.example.receiptlogger

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.icu.number.NumberFormatter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.WorkManager
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptDao
import com.example.receiptlogger.data.receipt.ReceiptRepository
import com.example.receiptlogger.ui.ReceiptLoggerScreen
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import kotlinx.coroutines.flow.Flow
import java.util.Locale
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

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

