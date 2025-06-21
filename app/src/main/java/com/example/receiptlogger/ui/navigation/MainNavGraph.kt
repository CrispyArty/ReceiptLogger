package com.example.receiptlogger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.receiptlogger.ui.home.HomeDestination
import com.example.receiptlogger.ui.home.HomeScreen
import com.example.receiptlogger.ui.receipt.ReceiptDetailsDestination
import com.example.receiptlogger.ui.receipt.ReceiptDetailsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
//    val viewModalTopBar: TopBarViewModal = viewModel()
//    navController

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToReceipt = {
                    navController.navigate("${ReceiptDetailsDestination.route}/1")
                },
            )
        }
        composable(
            route = ReceiptDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ReceiptDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ReceiptDetailsScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }

}
