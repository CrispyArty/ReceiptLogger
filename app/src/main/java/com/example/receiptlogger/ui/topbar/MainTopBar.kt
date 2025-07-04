@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.receiptlogger.ui.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.receiptlogger.R
import com.example.receiptlogger.ui.AppViewModelProvider
import com.example.receiptlogger.ui.theme.ReceiptLoggerTheme
import kotlinx.coroutines.delay

@Composable
fun MainTopBarWithNav(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    canNavigateBack: Boolean = false,
    viewModal: TopBarViewModal = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiData by TopBarUiState.data.collectAsState()
    val uiState by viewModal.jobStatusUiState.collectAsStateWithLifecycle()
    val missingJobsReceipts by viewModal.missingJobsReceipts.collectAsStateWithLifecycle()
    var isVisible by remember { mutableStateOf(true) }
    LaunchedEffect(uiState) {
        if (uiState is JobStatusUiState.Complete) {
            isVisible = true
            delay(3000)
            isVisible = false
        }
    }

    CenterAlignedTopAppBar(
        title = uiData.title,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
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
        },
        actions = {
            val actionModifier = Modifier
                .padding(end = 16.dp)
                .size(32.dp)
                .align(Alignment.CenterVertically)
            when (uiState) {
                is JobStatusUiState.Loading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = actionModifier
                )

                is JobStatusUiState.Complete -> AnimatedVisibility(
                    visible = isVisible,
                    exit = fadeOut(tween(2000)),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Uploaded",
                        tint = colorResource(R.color.green_check),
                        modifier = actionModifier

                    )
                }

                else -> if (missingJobsReceipts.isNotEmpty()) {
                    IconButton(
                        onClick = viewModal::enqueueMissing
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_sync),
                            contentDescription = "Sync",
                            modifier = actionModifier
                        )
                    }

                }
            }

        },
    )

}


@Composable
fun ProvideAppBarTitle(title: @Composable () -> Unit) = TopBarUiState.updateTitle(title)

@Composable
fun ProvideAppBarFAB(title: @Composable () -> Unit) = TopBarUiState.updateFAB(title)


@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    ReceiptLoggerTheme {
        ProvideAppBarTitle {
            Text("Receipt Logger")
        }
        MainTopBarWithNav()
    }
}