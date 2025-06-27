package com.example.receiptlogger.ui.scannerButton

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.AppViewModelProvider
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import com.example.receiptlogger.ui.theme.backgroundDark
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QrCodeScanButton(
    onScan: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFail: () -> Unit = {},
    viewModal: QrCodeScanViewModal = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scannerLauncher =
        rememberLauncherForActivityResult(QrCodeScanContract()) { result: String? ->
            Log.d("gosu", "result: $result")
            if (result != null) viewModal.onScan(result) else onFail()
        }

    if (viewModal.error != null) {
        ErrorDialog(
            error = viewModal.error as CreateError,
            onDismissRequest = viewModal::clearError
        )
    }

    FloatingActionButton(
        onClick = {
            scannerLauncher.launch(Unit)
        },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_new_receipt)
        )
    }
}

@Composable
fun ErrorDialog(error: CreateError, onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .sizeIn(minWidth = 280.dp, maxWidth = 560.dp),
            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_small)),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {

            Text(
                text = when (error) {
                    CreateError.ALREADY_EXISTS -> stringResource(R.string.error_alert_receipt_already_exist)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Preview
@Composable
fun ErrorDialogPreview() {
    ReceiptLoggerTheme {
        ErrorDialog(
            error = CreateError.ALREADY_EXISTS,
            onDismissRequest = {}
        )
    }
}

//
//val scanOptions: ScanOptions by lazy {
//    createScanOptions()
//}
//
//fun createScanOptions(): ScanOptions {
//    val options = ScanOptions()
//    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
//    options.setPrompt("ScanQrCode")
//    options.setCameraId(0)
//    options.setBeepEnabled(false)
//    options.setBarcodeImageEnabled(true)
//    options.setOrientationLocked(true)
//
//    return options
//}
//
//
//@Composable
//fun QrCodeScanButton1(
//    onScan: (String?) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//
//    ActivityResultContracts.StartActivityForResult
//    val barcodeLauncher =
//        rememberLauncherForActivityResult(ScanContract()) { result: ScanIntentResult ->
//            Log.d("gosu", "result: ${result}")
//            Log.d("gosu", "result.contents: ${result.contents}")
//            onScan(result.contents)
//        }
//
//    FloatingActionButton(
//        onClick = {
////            viewModal.onClick()
//            barcodeLauncher.launch(scanOptions)
//        },
//        shape = MaterialTheme.shapes.medium,
//        modifier = modifier
//    ) {
//        Icon(
//            imageVector = Icons.Default.Add,
//            contentDescription = stringResource(R.string.add_new_receipt)
//        )
//    }
//}