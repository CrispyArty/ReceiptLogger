package com.example.receiptlogger.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.savedstate.SavedState
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.home.HomeDestination
import com.example.receiptlogger.ui.home.HomeScreen
import com.example.receiptlogger.ui.receipt.ReceiptDetailsDestination
import com.example.receiptlogger.ui.receipt.ReceiptDetailsScreen
import com.example.receiptlogger.ui.scannerButton.QrCodeScanButton
import com.example.receiptlogger.ui.topbar.MainTopBarWithNav
import com.example.receiptlogger.ui.topbar.ProvideAppBarTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    ProvideAppBarTitle {
        Text(stringResource(HomeDestination.titleRes))
    }

    val topAppBarState = rememberTopAppBarState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    val isHomeScreen: Boolean = backStackEntry?.destination?.route == HomeDestination.route
    val canNavigateBack =
        backStackEntry != null && backStackEntry?.destination?.route != HomeDestination.route

    DisposableEffect(topAppBarState) {
        val listener = OnDestinationChangedListener { _, _, _ ->
            scrollBehavior.state.heightOffset = 0f
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBarWithNav(
                canNavigateBack = canNavigateBack,
                scrollBehavior = scrollBehavior,
                navigateUp = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            if (isHomeScreen) {
                Row {
                    QrCodeScanButton(
                        onScan = {},
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
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
                HomeScreen(
                    navigateToReceipt = {
                        navController.navigate("${ReceiptDetailsDestination.route}/$it")
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
                ReceiptDetailsScreen(
                    navigateBack = {
                        navController.popBackStack()
                        scrollBehavior.state.heightOffset = 0f
                    },
                    modifier = paddingModifier
                )
            }
        }

    }
}


