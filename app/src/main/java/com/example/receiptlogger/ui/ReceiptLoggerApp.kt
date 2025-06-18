package com.example.receiptlogger.ui


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.receiptlogger.model.Check
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.screens.CheckScreen
import com.example.receiptlogger.ui.screens.ChecksViewModel
import com.example.receiptlogger.ui.screens.ListScreen
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import org.jsoup.Jsoup

enum class ReceiptLoggerScreen(@StringRes val title: Int) {
    List(title = R.string.app_name),
    Check(title = R.string.check_screen),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptLoggerAppBar(
    currentScreen: ReceiptLoggerScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun ReceiptLoggerApp(
    viewModel: ChecksViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ReceiptLoggerScreen.valueOf(
        backStackEntry?.destination?.route ?: ReceiptLoggerScreen.List.name
    )

    val checks by viewModel.checksUiState.collectAsState()
    var currentCheck by rememberSaveable { mutableStateOf<Check?>(null) }



    Scaffold(
        topBar = {
            ReceiptLoggerAppBar(
                currentScreen = currentScreen,
//                canNavigateBack = currentScreen != ReceiptLoggerScreen.Start,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
//        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = ReceiptLoggerScreen.List.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = ReceiptLoggerScreen.List.name) {
                ListScreen(
                    checks = checks,
                    requestUiState = viewModel.requestUiState,
                    onCheckClick={
                        currentCheck = it
                        navController.navigate(ReceiptLoggerScreen.Check.name)
                     },
                    onQrCodeFetch = {viewModel.getCheck(it)},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            composable(route = ReceiptLoggerScreen.Check.name) {
                CheckScreen(
                    check = currentCheck,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartOrderPreview() {
//    val html = "<ss>s<assa>"
//    val doc = Jsoup.parse(html)
//    println("--------------------------")
//    println(doc)

    ReceiptLoggerTheme {
        ReceiptLoggerApp()
    }
}
