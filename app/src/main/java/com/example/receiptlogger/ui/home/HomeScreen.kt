package com.example.receiptlogger.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.receiptlogger.ui.navigation.NavigationDestination
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.AppViewModelProvider
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import com.example.receiptlogger.ui.topbar.ProvideAppBarTitle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptListItem
import com.example.receiptlogger.data.store.StoreData
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.UploadStatus
import com.example.receiptlogger.types.toMoney
import com.example.receiptlogger.ui.FormatHelper
import com.example.receiptlogger.ui.formatted
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlin.collections.component1
import kotlin.collections.component2

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreenMin(
    navigateToReceipt: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    ProvideAppBarTitle {
        Text(stringResource(HomeDestination.titleRes))
    }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        when (uiState.listStatus) {
            ListStatus.Loading -> Loading()
            ListStatus.Success -> ItemList(
                groupedList = uiState.groupedList,
                onItemClick = { navigateToReceipt(it.id) },
                contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium))
            )
        }
    }

}

@Composable
fun Loading(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
        )
    }


}

@Composable
fun ItemList(
    groupedList: Map<LocalDate, List<ReceiptListItem>>,
    onItemClick: (Receipt) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        groupedList.forEach { (date, records) ->
            item(key = date) {
                DateRow(
                    date = date,
                    totalPrice = records.fold(0L) { acc, item ->
                        acc + (item.receipt.totalPrice?.cents ?: 0)
                    }.toMoney()
                )
            }

            items(items = records, key = { it.receipt.id }) { receipt ->
                ItemCard(
                    receiptListItem = receipt,
                    onItemClick = onItemClick,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
                )
            }
        }
    }
}

@Composable
fun DateRow(
    date: LocalDate,
    totalPrice: Money,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.padding_small))
    ) {
        Text(
            text = date.format(FormatHelper.dateMonthFormatter),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = totalPrice.formatted,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
fun ItemCard(
    receiptListItem: ReceiptListItem,
    onItemClick: (Receipt) -> Unit,
    modifier: Modifier = Modifier,
) {
    val receipt = receiptListItem.receipt
//    val color by animateColorAsState(
//        targetValue = if (receipt.totalPrice != null) MaterialTheme.colorScheme.secondaryContainer
//        else MaterialTheme.colorScheme.surface,
//    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        colors = CardDefaults.cardColors().copy(
//            containerColor = color,
//        ),
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )

    ) {
        Box(
            Modifier.clickable(onClick = {
                onItemClick(receipt)
            })
        ) {
            Row(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            ) {
                Column(Modifier.weight(1f)) {
                    Row {
                        Text(
                            StoreData.findName(receipt),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.width(dimensionResource(R.dimen.padding_tiny)))

                        if (receipt.uploadStatus == UploadStatus.Uploaded) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Uploaded",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(dimensionResource(R.dimen.padding_large))
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                    if (receipt.fetchStatus == FetchStatus.Completed) {
                        Text("${receipt.purchaseDate!!.formatted} / ${receipt.totalPrice!!.formatted}")
                    }
                }

                Text(
                    text = receiptListItem.itemCount.toString(),
                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            if (receipt.uploadStatus == UploadStatus.Pending) {
                LinearProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    ReceiptLoggerTheme {
        Loading()
    }
}


@Preview(showBackground = true)
@Composable
fun ItemListPreview() {
    ReceiptLoggerTheme {
        ItemList(
            onItemClick = {},
            groupedList = mapOf<LocalDate, List<ReceiptListItem>>(
                LocalDate.now() to listOf(
                    ReceiptListItem(
                        receipt = Receipt(
                            id = 1,
                            qrCodeUrl = "https://example.com",
                            codFiscal = "12323423",
                            registrationNumber = "123234234",
                            address = "st. example",
                            totalPrice = 2812212.8f.toMoney(),
                            purchaseDate = LocalDateTime.now(),
                            fetchStatus = FetchStatus.Completed,
                            uploadStatus = UploadStatus.Uploaded,
                        ),
                        itemCount = 15
                    ),
                    ReceiptListItem(
                        receipt = Receipt(
                            id = 2,
                            qrCodeUrl = "https://example.com",
                            codFiscal = "12323423",
                            registrationNumber = "123234234",
                            address = "st. example",
                            totalPrice = 2812212.8f.toMoney(),
                            purchaseDate = LocalDateTime.now(),
                            fetchStatus = FetchStatus.Completed,
                            uploadStatus = UploadStatus.Uploaded,
                        ),
                        itemCount = 15
                    ),
                )
            ),
        )
    }
}


@Preview
@Composable
fun InventoryItemPreview() {
    polygon()
    ReceiptLoggerTheme {
        ItemCard(
            ReceiptListItem(
                receipt = Receipt(
                    qrCodeUrl = "https://example.com",
                    codFiscal = "12323423",
                    registrationNumber = "123234234",
                    address = "st. example",
                    totalPrice = 2812212.8f.toMoney(),
                    purchaseDate = LocalDateTime.now(),
                    fetchStatus = FetchStatus.Completed,
                    uploadStatus = UploadStatus.Uploaded,
                ),
                itemCount = 15
            ),
            onItemClick = {}
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun HomeBodyPreview() {
//    ReceiptLoggerTheme {
//        HomeScreen(navigateToReceipt = {})
//    }
//}


fun polygon() {
//
//    var f = 225.32f
//
//    println(f.toBigDecimal())
//    println(f.toDouble())
//
//    println((f.toDouble() * 100))
//
//    println(((22_532).toDouble() / 100))
//    println(((22_532L).toDouble() / 100))


    val datetime = LocalDateTime.now()

    val str = "2025-06-26T02:42:12.20";
    val compareDatetime = LocalDateTime.parse(str)

    val d1 = datetime.toLocalDate().withDayOfMonth(1)
    val d2 = compareDatetime.toLocalDate().withDayOfMonth(1)

    println("mount1: ${d1} == ${d2} : ${d1 == d2}")

    val zoneId = ZoneId.of("Europe/Chisinau")

    datetime.atOffset(zoneId.rules.getOffset(datetime))
//    Timestamp.from(datetime)
//    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    val formatter = DateTimeFormatter.ofPattern("'DATA' dd.MM.yyyy 'ORA' HH:mm:ss");

    println("datetime.parse: ${LocalDateTime.parse("DATA 12.03.2025 ORA 19:43:56", formatter)}")

    println("datetime.now: ${datetime}")
    println("datetime.now: ${datetime.atZone(zoneId)}")

//    println("test: ${datetime.toEpochSecond(zoneId.rules.getOffset(datetime))}")

    val instant = ZonedDateTime.now().toInstant()
    println("from instant: ${instant.atZone(zoneId).toLocalDateTime()}")


    println("ZonedDateTime.now: ${ZonedDateTime.now().toInstant()}")
    println("ZonedDateTime.now: ${ZonedDateTime.now()}")


    println("datetime.now: ${datetime.atOffset(zoneId.rules.getOffset(datetime))}")


    println("parse: ${LocalDateTime.parse(str)}")
    println("parses at Zone: ${LocalDateTime.parse(str).atZone(TimeZone.getDefault().toZoneId())}")

//    TimeZone.getDefault().toZoneId()
//    println("parse with Zone: ${LocalDateTime.parse(str).atZone()}")

//    println("datetime${datetime.toInstant(ZoneOffset.UTC)}")
//    println("zones: ${ZoneId.getAvailableZoneIds()}")


//    ZoneOffset.from(
//    )


    println("datetime${datetime.toInstant(zoneId.rules.getOffset(datetime)).epochSecond}")

//    Text("Gosu1")

}