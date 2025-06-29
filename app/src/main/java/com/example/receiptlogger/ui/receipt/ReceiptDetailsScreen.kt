package com.example.receiptlogger.ui.receipt

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.receiptlogger.ui.navigation.NavigationDestination
import com.example.receiptlogger.R
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptItem
import com.example.receiptlogger.data.receipt.ReceiptWithItems
import com.example.receiptlogger.data.store.StoreData
import com.example.receiptlogger.types.UploadStatus
import com.example.receiptlogger.types.toMoney
import com.example.receiptlogger.ui.AppViewModelProvider
import com.example.receiptlogger.ui.components.Loading
import com.example.receiptlogger.ui.formatted
import com.example.receiptlogger.ui.formattedShort
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import com.example.receiptlogger.ui.topbar.ProvideAppBarTitle
import java.time.LocalDateTime
import androidx.core.net.toUri
import kotlinx.coroutines.launch

object ReceiptDetailsDestination : NavigationDestination {
    override val route = "item_details"
    override val titleRes = R.string.receipt_detail_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun ReceiptDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    ProvideAppBarTitle {
        Text("Details")
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        when (uiState) {
            is DetailsStatus.Loading -> Loading()
            is DetailsStatus.Success -> ReceiptDetailsBody(
                navigateBack = navigateBack,
                receiptWithItems = (uiState as DetailsStatus.Success).receiptWithItems,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
fun ReceiptDetailsBody(
    navigateBack: () -> Unit,
    receiptWithItems: ReceiptWithItems,
    modifier: Modifier = Modifier,
    viewModel: ReceiptDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val receipt = receiptWithItems.receipt
    val items = receiptWithItems.items
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
    ) {
        val contentModifier =
            Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))

        Column(
            modifier = contentModifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = StoreData.findName(receipt),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "COD FISCAL: ${receipt.codFiscal}",
                textAlign = TextAlign.Center
            )
            TextButton(onClick = {
                val gmmIntentUri =
                    "geo:0,0?q=${receipt.address}".toUri()
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                mapIntent.resolveActivity(context.packageManager)?.let {
                    context.startActivity(mapIntent)
                }
            }) {
                Text(receipt.address ?: "", textAlign = TextAlign.Center)
            }
        }

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.padding_small)
            )
        )
        Column(
            modifier = contentModifier.fillMaxWidth()
        ) {

            items.forEach {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        it.name, modifier = Modifier
                            .weight(1.0f)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "${it.count} x ${it.itemPrice.formattedShort}\n= ${it.totalPrice.formattedShort}",
                        textAlign = TextAlign.End,
                        maxLines = 2
                    )
                }

                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
            }
        }

        Row(
            modifier = contentModifier
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_small))
        ) {
            Text("Total", modifier = Modifier.weight(1f))
            Text("${receipt.totalPrice?.formatted}")
        }

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.padding_small)
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_medium)
                )
                .fillMaxWidth()
                .padding(top = dimensionResource(R.dimen.padding_small))
        ) {
            TextButton(
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    scope.launch {
                        viewModel.delete()
                        navigateBack()
                    }
                },
            ) {
                Text("Delete")
            }
            Spacer(
                Modifier
                    .width(dimensionResource(R.dimen.padding_small))
                    .weight(1f)
            )

            Text("${receipt.purchaseDate?.formatted}")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReceiptDetailsBodyPreview() {
    ReceiptLoggerTheme {
        ReceiptDetailsBody(
            navigateBack = {},
            receiptWithItems = ReceiptWithItems(
                receipt = Receipt(
                    id = 1,
                    registrationNumber = "asd123",
                    qrCodeUrl = "https://example.com",
                    codFiscal = "2812212281221",
                    address = "st. example st. example st. example st. example st. example st. example st. example ",
                    totalPrice = 2812212.8f.toMoney(),
                    purchaseDate = LocalDateTime.now(),
                    uploadStatus = UploadStatus.Pending,
                ),
                items = listOf(
                    ReceiptItem(
                        id = 1,
                        name = "BigBon",
                        count = 3.0,
                        itemPrice = 1000.toMoney(),
                        totalPrice = 3000.toMoney(),
                        receiptId = 1
                    ),
                    ReceiptItem(
                        id = 2,
                        name = "BigBon",
                        count = 3.0,
                        itemPrice = 1000.toMoney(),
                        totalPrice = 3000.toMoney(),
                        receiptId = 1
                    ),
                    ReceiptItem(
                        id = 3,
                        name = "BigBon",
                        count = 3.0,
                        itemPrice = 1000.toMoney(),
                        totalPrice = 3000.toMoney(),
                        receiptId = 1
                    )
                )
            ),
        )
    }
}


//@Composable
//fun CheckScreen(check: Check?, modifier: Modifier = Modifier) {
//    if (check == null) {
//        Text(text = "Something went wrong", color = Color.Red)
//        return
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//    ) {
//        Row(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(horizontal = 8.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(check.description, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
//        }
//        HorizontalDivider(thickness = 2.dp)
//        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//
//        check.items.forEach {
//            Row(
////                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//            ) {
//                Text(
//                    it.name, modifier = Modifier
//                        .weight(1.0f)
//                        .padding(end = 8.dp)
//                )
//                Text("${it.count.toInt()} x ${it.itemPrice}= ${it.totalPrice}")
//            }
//
//            Canvas(
//                Modifier
//                    .fillMaxWidth()
//                    .height(1.dp)
//            ) {
//                drawLine(
//                    color = Color.Gray,
//                    start = Offset(0f, 0f),
//                    end = Offset(size.width, 0f),
//                    pathEffect = pathEffect
//                )
//            }
//        }
//
//        Row(
//            horizontalArrangement = Arrangement.SpaceBetween,
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(horizontal = 8.dp, vertical = 4.dp)
//        ) {
//            Text("Total")
//            Text("${check.totalPrice}")
//        }
//    }
//}
////
////@Preview(showBackground = true)
////@Composable
////fun CheckScreenPreview() {
////
////    val check = Check(
////        "IMENSITATE S.R.L.\n" +
////                "COD FISCAL: 1002600011694\n" +
////                "mun. Chisinau str. Ion Creanga, 45/1\n" +
////                "NUMARUL DE ÎNREGISTRARE: J402005328",
////        listOf(
////            CheckItem(
////                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
////                count = 3.0f,
////                itemPrice = 7.75f,
////                totalPrice = 23.25f,
////            ),
////            CheckItem(
////                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
////                count = 3.0f,
////                itemPrice = 7.75f,
////                totalPrice = 23.25f,
////            ),
////            CheckItem(
////                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
////                count = 3.0f,
////                itemPrice = 7.75f,
////                totalPrice = 23.25f,
////            ),
////            CheckItem(
////                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
////                count = 3.0f,
////                itemPrice = 7.75f,
////                totalPrice = 23.25f,
////            ),
////            CheckItem(
////                name = "Branzica glazur.vanil/lap.cond. 26% 50g JLC",
////                count = 3.0f,
////                itemPrice = 7.75f,
////                totalPrice = 23.25f,
////            )
////        ),
////        219.53f
////    )
////    ReceiptLoggerTheme {
////        CheckScreen(eck = check)
//    }
//}
