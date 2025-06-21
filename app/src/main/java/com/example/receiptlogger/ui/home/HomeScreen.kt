package com.example.receiptlogger.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.receiptlogger.ui.navigation.NavigationDestination
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import com.example.receiptlogger.ui.topbar.ProvideAppBarTitle

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreenMin(
    navigateToReceipt: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    ProvideAppBarTitle {
        Text(stringResource(HomeDestination.titleRes))
    }

    HomeBody(
        navigateToReceipt = navigateToReceipt,
        modifier = modifier
            .fillMaxSize(),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToReceipt: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            MainTopAppBar(
//                title = stringResource(HomeDestination.titleRes),
//                canNavigateBack = false,
//                scrollBehavior = scrollBehavior
//            )
//        },
        floatingActionButton = {
            Row {
                FloatingActionButton(
                    onClick = { },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_new_receipt)
                    )
                }
            }
        },
    ) { innerPadding ->
        HomeBody(
            navigateToReceipt = navigateToReceipt,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun HomeBody(
    navigateToReceipt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Button(onClick = navigateToReceipt) {
            Text("Receipt")
        }
        Text("HomeBody")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    ReceiptLoggerTheme {
        HomeScreen(navigateToReceipt = {})
    }
}
