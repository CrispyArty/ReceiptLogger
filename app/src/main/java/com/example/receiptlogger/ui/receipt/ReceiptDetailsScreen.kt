package com.example.receiptlogger.ui.receipt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.receiptlogger.ui.navigation.NavigationDestination
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.topbar.ProvideAppBarTitle

object ReceiptDetailsDestination : NavigationDestination {
    override val route = "item_details"
    override val titleRes = R.string.receipt_detail_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun ReceiptDetailsScreenMin(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptDetailsViewModel = viewModel()
) {
    ProvideAppBarTitle {
        Text("ReceiptScreenMin")
    }

    Column(modifier = modifier) {
        Text("ReceiptDetailsScreen")
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptDetailsScreenPreview() {

}

////
////@Composable
////fun CheckScreen(check: Check?, modifier: Modifier = Modifier) {
////    if (check == null) {
////        Text(text = "Something went wrong", color = Color.Red)
////        return
////    }
////
////    Column(
////        modifier = modifier
////            .fillMaxSize()
////            .verticalScroll(rememberScrollState())
////    ) {
////        Row(
////            modifier = modifier
////                .fillMaxWidth()
////                .padding(horizontal = 8.dp),
////            horizontalArrangement = Arrangement.Center
////        ) {
////            Text(check.description, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
////        }
////        HorizontalDivider(thickness = 2.dp)
////        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
////
////        check.items.forEach {
////            Row(
//////                horizontalArrangement = Arrangement.SpaceBetween,
////                verticalAlignment = Alignment.CenterVertically,
////                modifier = modifier
////                    .fillMaxWidth()
////                    .padding(horizontal = 8.dp, vertical = 4.dp)
////            ) {
////                Text(
////                    it.name, modifier = Modifier
////                        .weight(1.0f)
////                        .padding(end = 8.dp)
////                )
////                Text("${it.count.toInt()} x ${it.itemPrice}= ${it.totalPrice}")
////            }
////
////            Canvas(
////                Modifier
////                    .fillMaxWidth()
////                    .height(1.dp)
////            ) {
////                drawLine(
////                    color = Color.Gray,
////                    start = Offset(0f, 0f),
////                    end = Offset(size.width, 0f),
////                    pathEffect = pathEffect
////                )
////            }
////        }
////
////        Row(
////            horizontalArrangement = Arrangement.SpaceBetween,
////            modifier = modifier
////                .fillMaxWidth()
////                .padding(horizontal = 8.dp, vertical = 4.dp)
////        ) {
////            Text("Total")
////            Text("${check.totalPrice}")
////        }
////    }
////}
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
