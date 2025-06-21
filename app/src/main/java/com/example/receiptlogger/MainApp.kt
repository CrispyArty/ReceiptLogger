@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.receiptlogger

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.receiptlogger.ui.navigation.MainNavGraph

@Composable
fun ReceiptLoggerApp(navController: NavHostController = rememberNavController()) {
    MainNavGraph(navController = navController)
}
