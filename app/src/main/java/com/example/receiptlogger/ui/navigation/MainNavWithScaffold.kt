package com.example.receiptlogger.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.home.HomeDestination
import com.example.receiptlogger.ui.home.HomeScreen
import com.example.receiptlogger.ui.home.HomeScreenMin
import com.example.receiptlogger.ui.receipt.ReceiptDetailsDestination
import com.example.receiptlogger.ui.receipt.ReceiptDetailsScreen
import com.example.receiptlogger.ui.receipt.ReceiptDetailsScreenMin
import com.example.receiptlogger.ui.topbar.MainTopBarWithNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavWithScaffold(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
//    val viewModalTopBar: TopBarViewModal = viewModel()
//    navController
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBarWithNav(
                navController = navController,
                scrollBehavior = scrollBehavior,
                navigateUp = { navController.popBackStack() },
            )
        },
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
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = modifier
        ) {
            val paddingModifier = modifier.padding(innerPadding)
            composable(route = HomeDestination.route) {
                HomeScreenMin(
                    navigateToReceipt = {
                        navController.navigate("${ReceiptDetailsDestination.route}/1")
                    },
                    modifier = paddingModifier
                )
            }
            composable(
                route = ReceiptDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(ReceiptDetailsDestination.itemIdArg) {
                    type = NavType.IntType
                })
            ) {
                ReceiptDetailsScreenMin(
                    navigateBack = { navController.popBackStack() },
                    modifier = paddingModifier
                )
            }
        }

    }
}
