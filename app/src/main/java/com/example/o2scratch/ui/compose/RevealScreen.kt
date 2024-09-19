package com.example.o2scratch.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.o2scratch.R
import com.example.o2scratch.ui.compose.ScratchCardContract.Event
import com.example.o2scratch.ui.compose.ScratchCardContract.State
import com.example.o2scratch.ui.compose.base.EventsProcessor
import com.example.o2scratch.ui.compose.base.getString
import com.example.o2scratch.ui.compose.composables.DefaultTopAppBar

@Composable
fun RevealScreen(
    viewModel: ScratchCardViewModel,
    onNavBack: () -> Unit
) {
    val viewState by viewModel.viewStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsProcessor(viewModel.viewEventsFlow) {
        when (it) {
            is Event.Error -> snackbarHostState.showSnackbar(getString(it.message))
        }
    }

    BackHandler {
        onNavBack()
    }

    RevealContent(
        snackbarHostState = snackbarHostState,
        viewState = viewState,
        onNavBack = onNavBack,
        executeAction = { viewModel.executeAction(it) }
    )
}

@Composable
private fun RevealContent(
    viewState: State = State(),
    snackbarHostState: SnackbarHostState,
    onNavBack: () -> Unit,
    executeAction: (ScratchCardContract.Action) -> Unit = {}
) {
    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(id = R.string.reveal_code),
                onNavBack = onNavBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (viewState.loading) {
                CircularProgressIndicator()
            }
            Text(
                text = viewState.code,
                color = if (!viewState.activated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = { executeAction(ScratchCardContract.Action.RevealCode) },
                enabled = viewState.notRevealed
            ) {
                Text(text = stringResource(id = R.string.reveal_code))
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenContentPreview() {
    MaterialTheme {
        RevealContent(
            viewState = State(code = "123456", activated = false),
            snackbarHostState = SnackbarHostState(),
            {},
            {}
        )
    }
}