@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.receiptlogger.ui.topbar

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.receiptlogger.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import com.example.receiptlogger.ui.home.HomeDestination
import com.example.receiptlogger.ui.receipt.ReceiptDetailsDestination
import kotlinx.coroutines.delay

@Composable
fun MainTopBarWithNav(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
) {
//    LaunchedEffect(Unit) {
//        Log.d("gosu", "3!!!!!!!!!!!!!!LaunchedEffect")
//    }


//    Log.d("gosu", "~~~~~~~~~~~~~~~~~MainTopBarWithNav~~~~~~~~~~~~~~~~~~")
    val backStackEntry by navController.currentBackStackEntryAsState()
//    navController.previousBackStackEntry
//    navController.getBackStackEntry(
//        ReceiptDetailsDestination.routeWithArgs
//    )

//    val screenBackStackEntry = remember(backStackEntry) {
//    }
//    Log.d("gosu", "--111111111111 screenBackStackEntry: $screenBackStackEntry")

    val screenTopBarViewModal: ScreenTopBarViewModal? = backStackEntry?.let { entry ->
        Log.d("gosu", "--init modal MainTopBarWithNav: $entry")

        val viewModelEntry: ScreenTopBarViewModal = viewModel(
            viewModelStoreOwner = entry,
            factory = ScreenTopBarViewModal.Factory
//            initializer = {ScreenTopBarViewModal()}
        )
        return@let viewModelEntry
    }

    MainTopBarBody(
        canNavigateBack = backStackEntry != null && backStackEntry?.destination?.route != HomeDestination.route,
        navigateUp = navigateUp,
        modifier = Modifier,
        scrollBehavior = scrollBehavior,
        screenViewModal = screenTopBarViewModal,
    )

}

//@Composable
//fun MainTopBar(
//    title: String,
//    canNavigateBack: Boolean,
//    modifier: Modifier = Modifier,
//    scrollBehavior: TopAppBarScrollBehavior? = null,
//    navigateUp: () -> Unit = {},
//    viewModal: TopBarViewModal = viewModel()
//) {
////    Log.d("gosu", "time - ${viewModal.timeNano}")
//
////    SideEffect {  }
////    LaunchedEffect(Unit) { }
//
//    MainTopBarBody()
//}


@Composable
private fun MainTopBarBody(
    navigateUp: () -> Unit,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    viewModal: TopBarViewModal = viewModel(),
    screenViewModal: ScreenTopBarViewModal? = null,
) {
    Log.d("gosu", "ScreenViewModal: ${screenViewModal}")
    Log.d("gosu", "Title: ${screenViewModal?.title}")

    CenterAlignedTopAppBar(
        title = screenViewModal?.title ?: {
            Log.d("gosu", "default title")
            Text("default titleasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd")
//            Text(stringResource(R.string.app_name))
        },
        actions = {
            Text("action")
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
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
fun ProvideAppBarTitle(title: @Composable () -> Unit) {

//    val viewModelStoreOwner = LocalViewModelStoreOwner.current
//    (viewModelStoreOwner as? NavBackStackEntry)?.let { owner ->
//        val viewModel: TopAppBarViewModel = viewModel(
//            viewModelStoreOwner = owner,
//            initializer = { TopAppBarViewModel() },
//        )
//        LaunchedEffect(title) {
//            viewModel.title = title
//        }
//    }

    LocalViewModelStoreOwner.current?.let { owner ->

        Log.d("gosu", "--init modal ProvideAppBarTitle: $owner")
        val viewModel: ScreenTopBarViewModal = viewModel(
            viewModelStoreOwner = owner,
            factory = ScreenTopBarViewModal.Factory
//            initializer = { ScreenTopBarViewModal() },
        )
        LaunchedEffect(title) {
            viewModel.title = title
            if ((owner as NavBackStackEntry).destination.route == ReceiptDetailsDestination.routeWithArgs) {
                Log.d("gosu", "~~~~~~~~~~~~~~~~~~~~~State save~~~~~~~~~~~~~~~~~~~~~~~~~~")
                viewModel.saveGosuState("Hi gosu!")
            }
        }
    }

}

//@Composable
//fun ProvideAppBarNavigationIcon(navigationIcon: @Composable () -> Unit) {
//
//    val viewModelStoreOwner = LocalViewModelStoreOwner.current
//    (viewModelStoreOwner as? NavBackStackEntry)?.let { owner ->
//        val viewModel: TopAppBarViewModel = viewModel(
//            viewModelStoreOwner = owner,
//            initializer = { TopAppBarViewModel() },
//        )
//        LaunchedEffect(navigationIcon) {
//            viewModel.navigationIcon = navigationIcon
//        }
//    }
//
//}
//
//@Composable
//fun ProvideAppBarActions(actions: @Composable RowScope.() -> Unit) {
//
//    val viewModelStoreOwner = LocalViewModelStoreOwner.current
//    (viewModelStoreOwner as? NavBackStackEntry)?.let { owner ->
//        val viewModel: TopAppBarViewModel = viewModel(
//            viewModelStoreOwner = owner,
//            initializer = { TopAppBarViewModel() },
//        )
//        LaunchedEffect(actions) {
//            viewModel.actions = actions
//        }
//    }
//
//}
