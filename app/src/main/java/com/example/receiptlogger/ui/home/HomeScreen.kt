package com.example.receiptlogger.ui.home

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.example.receiptlogger.data.receipt.Receipt
import com.example.receiptlogger.data.receipt.ReceiptListItem
import com.example.receiptlogger.data.store.StoreData
import com.example.receiptlogger.domain.ReceiptListItemUiModel
import com.example.receiptlogger.types.FetchStatus
import com.example.receiptlogger.types.Money
import com.example.receiptlogger.types.UploadStatus
import com.example.receiptlogger.types.toMoney
import com.example.receiptlogger.ui.FormatHelper
import com.example.receiptlogger.ui.components.Loading
import com.example.receiptlogger.ui.formatted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
fun HomeScreen(
    navigateToReceipt: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProvideAppBarTitle {
        Text(stringResource(HomeDestination.titleRes))
    }

    HomeBody(navigateToReceipt, modifier)
}

@Composable
fun HomeBody(
    navigateToReceipt: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
//    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val lazyPager = viewModel.receiptPager.collectAsLazyPagingItems()

    ProvideAppBarTitle {
        Text(stringResource(HomeDestination.titleRes))
    }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        when (lazyPager.loadState.refresh) {
            is LoadState.Loading -> Loading()
            else -> ItemList(
                onItemClick = { navigateToReceipt(it.id) },
                contentPadding = PaddingValues(top = 8.dp),
                lazyPager = lazyPager
            )
        }
    }

}


@Composable
fun ItemList(
    lazyPager: LazyPagingItems<UiPageItem>,
    onItemClick: (ReceiptListItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        if (lazyPager.itemCount == 0) {
            item {
                Text(
                    text = "Receipts not found",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            lazyPager.itemSnapshotList.forEachIndexed { index, item ->
                when (item) {
                    is UiPageItem.Item -> item() {
                        val pageItem = lazyPager[index] as UiPageItem.Item

                        ItemCard(
                            receipt = pageItem.item,
                            onItemClick = onItemClick,
                            modifier = Modifier.padding(
                                start = dimensionResource(R.dimen.padding_medium),
                                end = dimensionResource(R.dimen.padding_medium),
                                bottom = dimensionResource(R.dimen.padding_medium)
                            )
                        )
                    }

                    is UiPageItem.MonthHeader -> stickyHeader {
                        val monthHeader = lazyPager[index] as UiPageItem.MonthHeader

                        DateRow(
                            date = monthHeader.dateTime,
                            totalPrice = monthHeader.totalPrice,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    else -> null
                }

            }

        }
    }
}


@Composable
fun DateRow(
    date: LocalDateTime,
    totalPrice: Money?,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
//            .background(Color.White)
            .padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                bottom = dimensionResource(R.dimen.padding_small),
            )
    ) {
        Text(
            text = date.format(FormatHelper.dateMonthFormatter),
            style = MaterialTheme.typography.titleMedium,
        )

        if (totalPrice != null) {
            Text(
                text = totalPrice.formatted,
                style = MaterialTheme.typography.titleMedium,
            )
        }

    }
}


@Composable
fun ItemCard(
    receipt: ReceiptListItemUiModel,
    onItemClick: (ReceiptListItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            ) {
                Row(
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        receipt.storeName,
                        style = MaterialTheme.typography.titleLarge,
                        color =
                            if (receipt.storeName == "Unknow") {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
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
                    Spacer(
                        Modifier
                            .width(dimensionResource(R.dimen.padding_small))
                            .weight(1f)
                    )

                    Text(
                        text = receipt.totalPrice.formatted,
                        style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Row {
                    Text(
                        text = receipt.address,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(
                        Modifier
                            .width(dimensionResource(R.dimen.padding_small))
                    )

                    Text(
                        receipt.purchaseDate.format(FormatHelper.dateOfTheWeekFormatter),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
//                        modifier = Modifier.weight(2f, false),
//                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

//            if (receipt.uploadStatus == UploadStatus.Pending) {
//                LinearProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary,
//                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.BottomStart),
//                )
//            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun ItemListPreview() {
    ReceiptLoggerTheme {
        val pageItems: List<UiPageItem> = listOf(
            UiPageItem.MonthHeader(
                LocalDateTime.now(),
                228.30.toMoney()
            ),
            UiPageItem.Item(
                ReceiptListItemUiModel(
                    id = 1,
                    storeName = "Linella",
                    address = "st. example",
                    totalPrice = 2812212.8f.toMoney(),
                    purchaseDate = LocalDateTime.now(),
                    uploadStatus = UploadStatus.Uploaded,
                    itemCount = 15
                )
            ),
            UiPageItem.Item(
                ReceiptListItemUiModel(
                    id = 2,
                    storeName = "Linella",
                    address = "st. example",
                    totalPrice = 2812212.8f.toMoney(),
                    purchaseDate = LocalDateTime.now(),
                    uploadStatus = UploadStatus.Uploaded,
                    itemCount = 17
                )
            ),
            UiPageItem.MonthHeader(
                LocalDateTime.now(),
                228.30.toMoney()
            ),

            UiPageItem.Item(
                ReceiptListItemUiModel(
                    id = 2,
                    storeName = "Linella",
                    address = "st. example",
                    totalPrice = 2812212.8f.toMoney(),
                    purchaseDate = LocalDateTime.now(),
                    uploadStatus = UploadStatus.Uploaded,
                    itemCount = 17
                )
            ),
        )

        val flow: Flow<PagingData<UiPageItem>> = flowOf(PagingData.from(pageItems))

        ItemList(
            onItemClick = {},
            lazyPager = flow.collectAsLazyPagingItems(),
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun EmptyItemListPreview() {
    ReceiptLoggerTheme {
        val pageItems: List<UiPageItem> = listOf()

        val flow: Flow<PagingData<UiPageItem>> = flowOf(PagingData.from(pageItems))

        ItemList(
            onItemClick = {},
            lazyPager = flow.collectAsLazyPagingItems(),
        )
    }
}


@Preview
@Composable
fun InventoryItemPreview() {
    ReceiptLoggerTheme {
        ItemCard(
            ReceiptListItemUiModel(
                id = 1,
                storeName = "2812212281221",
                address = "st. example st. example st. example st. example st. example st. example st. example ",
                totalPrice = 2812212.8f.toMoney(),
                purchaseDate = LocalDateTime.now(),
                uploadStatus = UploadStatus.Pending,
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
