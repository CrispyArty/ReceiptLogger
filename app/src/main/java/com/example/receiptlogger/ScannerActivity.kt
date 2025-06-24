package com.example.receiptlogger

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object Intents {
    const val SCAN_INTENT = "com.example.receiptlogger.SCAN"

    object Result {
        const val TEXT = "com.example.receiptlogger.RESULT_TEXT"
    }
}

class QrCodeAnalyzer(
    private var onQrCodeScanned: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888,
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (imageProxy.format !in supportedImageFormats) {
            return
        }

        val mediaImage = imageProxy.image ?: return

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
            )
//                .enableAllPotentialBarcodes() // Optional
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
//                Log.d("gosu", "barcodes: ${barcodes}")

                if (barcodes.isNotEmpty()) {
                    onQrCodeScanned(barcodes.first())
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}


class ScannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            ReceiptLoggerTheme {
//                Log.d("gosu", "intent: ${intent}")
//                Log.d("gosu", "intent: ${intent.action}")

                val returnedIntent = Intent()

                var context = LocalContext.current

                var hasCameraPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_DENIED
                    )
                }

                if (!hasCameraPermission) {
                    PermissionDialogs(
                        activity = this,
                        onChange = { hasCameraPermission = it },
                        onDismiss = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }

                if (hasCameraPermission) {
                    CameraView(onScan = { barcode ->
                        returnedIntent.putExtra(Intents.Result.TEXT, barcode.rawValue)
                        setResult(RESULT_OK, returnedIntent)
                        onBackPressedDispatcher.onBackPressed()
                    })
                }
            }
        }
    }
}


@Composable
fun CameraView(onScan: (Barcode) -> Unit, modifier: Modifier = Modifier) {
    var context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }

    Column(
        modifier = modifier
            .size(400.dp)
            .padding(16.dp)
    ) {
        AndroidView(
            factory = { androidViewContext ->
                PreviewView(androidViewContext).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_START
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode =
                        PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val cameraExecutor: ExecutorService =
                    Executors.newSingleThreadExecutor()
                val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                    ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val cameraProvider: ProcessCameraProvider =
                        cameraProviderFuture.get()

                    val barcodeAnalyzer = QrCodeAnalyzer { result ->
                        onScan(result)
                    }
                    val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, barcodeAnalyzer)
                        }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("Scanner", "CameraPreview: ${e.localizedMessage}")
                    }
                }, ContextCompat.getMainExecutor(context))
            },
            modifier = Modifier.fillMaxSize(),
        )
    }

}


@Composable
private fun PermissionDialogs(
    activity: Activity,
    onChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    LocalOnBackPressedDispatcherOwner.current
    var permissionOpenDialog by remember { mutableStateOf(false) }
    var rationalPermissionOpenDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    rationalPermissionOpenDialog = true
                } else {
                    permissionOpenDialog = true
                }
            } else {
                onChange(true)
            }
        }
    )

    if (permissionOpenDialog) {
        ShowSettingDialog(
            onOk = { permissionOpenDialog = false },
            onDismiss = {
                permissionOpenDialog = false
                onDismiss()
            }
        )
    }

    if (rationalPermissionOpenDialog) {
        ShowRationalPermissionDialog(
            onDismiss = {
                rationalPermissionOpenDialog = false
                onDismiss()
            },
            onOk = {
                rationalPermissionOpenDialog = false
                launcher.launch(Manifest.permission.CAMERA)
            })
    }

    SideEffect {
        launcher.launch(Manifest.permission.CAMERA)
    }
}

@Composable
fun ShowSettingDialog(
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.permission_final_dialog_title))
        },
        text = {
            Text(stringResource(R.string.permission_final_dialog_text))
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.permission_dialog_button_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = "package:${context.packageName}".toUri()

                    context.startActivity(intent)
                    onOk()
                },
            ) {
                Text(stringResource(R.string.permission_dialog_button_ok))
            }
        }
    )
}

@Composable
fun ShowRationalPermissionDialog(
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.permission_rational_dialog_title))
        },
        text = {
            Text(stringResource(R.string.permission_rational_dialog_text))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = onOk) {
                Text("Ok")
            }
        }
    )
}

