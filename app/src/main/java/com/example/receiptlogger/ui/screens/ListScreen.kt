package com.example.receiptlogger.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receiptlogger.model.Check
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ListScreen(
    checks: List<Check>,
    onCheckClick: (Check) -> Unit,
    onQrCodeFetch: (String) -> Unit,
    modifier: Modifier = Modifier,
    requestUiState: RequestUiState? = null,
) {
    when (requestUiState) {
        is RequestUiState.Error -> {
            Column(modifier = modifier.fillMaxSize()) {
                Text(text = "Something went wrong", color = Color.Red)
                Text(requestUiState.name, color = Color.Red)
                Text(requestUiState.error, color = Color.Red)
            }
        }

        is RequestUiState.Loading -> LoadingScreen(modifier = modifier)
        else -> List(
            checks = checks,
            onCheckClick = onCheckClick,
            onQrCodeFetch = onQrCodeFetch,
            modifier = modifier,
        )
    }

}


@Composable
fun List(
    checks: List<Check>,
    onCheckClick: (Check) -> Unit,
    onQrCodeFetch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(verticalArrangement = Arrangement.Center) {
            if (checks.isNotEmpty()) {
                LazyColumn(modifier = Modifier) {
                    items(checks) { check ->
                        CheckCard(
                            check = check,
                            onClick = { onCheckClick(check) },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            } else {
                EmptyList()
            }
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(bottom = 30.dp)
        ) {
            QRCodeButton(onQrCodeFetch = onQrCodeFetch)
//            FilledIconButton(
//                onClick = onNewClick,
//                modifier = Modifier.size(50.dp)
//            ) {
//                Icon(Icons.Rounded.Add, "")
//            }
        }
    }
}

@Composable
private fun EmptyList() {
    val coroutineScope = rememberCoroutineScope()

    var i = 0

    Text(
        text = "No checks in system",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    coroutineScope.launch {
        while (true) {
            Log.d("gosu", "EmptyList-${i++}")
            delay(500)
        }
    }
    Button(onClick = {
        coroutineScope.launch {
            while (true) {
                Log.d("gosu", "EmptyList-${i++}")
                delay(500)
            }
        }
    }) {
        Text(text = "Click")
    }

}

private fun scanOptions(): ScanOptions {
    val options = ScanOptions()
    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
    options.setPrompt("ScanQrCode")
    options.setCameraId(0)
    options.setBeepEnabled(false)
    options.setBarcodeImageEnabled(true)
    options.setOrientationLocked(true)

    return options
}

@Composable
fun QRCodeButton(onQrCodeFetch: (String) -> Unit, modifier: Modifier = Modifier) {
    val barcodeLauncher =
        rememberLauncherForActivityResult(ScanContract()) { result: ScanIntentResult ->
            onQrCodeFetch(result.contents ?: "Error")
        }

    FilledIconButton(
        onClick = {
            barcodeLauncher.launch(scanOptions())
        },
        modifier = modifier.size(50.dp)
    ) {
        Icon(Icons.Rounded.Add, "New")
    }
}

@Composable
fun CheckCard(
    check: Check,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Text("Linella")
                Text(
                    text = LocalDate.now().toString()
                )
            }

            Column(verticalArrangement = Arrangement.Center) {
                Text(text = "${check.items.size}")
            }

        }

    }
}


@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Image(
            modifier = modifier.size(200.dp),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {

    ReceiptLoggerTheme {
        ListScreen(
//            checks = listOf(),
//            requestUiState = RequestUiState.Loading,
            checks = listOf(Check("Description \nDescription \nDescription \n", listOf(), 20.0f)),
            onQrCodeFetch = {},
            onCheckClick = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
