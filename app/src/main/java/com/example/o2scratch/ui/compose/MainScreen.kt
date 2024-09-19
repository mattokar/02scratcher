package com.example.o2scratch.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.o2scratch.ui.compose.ScratchCardContract.Action
import com.example.o2scratch.ui.compose.ScratchCardContract.Event
import com.example.o2scratch.ui.compose.ScratchCardContract.State
import com.example.o2scratch.ui.compose.base.EventsProcessor
import com.example.o2scratch.ui.compose.base.getString

@Composable
fun MainScreen(
    viewModel: ScratchCardViewModel,
    onNavigate: (String) -> Unit
) {
    val viewState by viewModel.viewStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsProcessor(viewModel.viewEventsFlow) {
        when (it) {
            is Event.Error -> snackbarHostState.showSnackbar(getString(it.message))
        }
    }

    MainContent(
        snackbarHostState = snackbarHostState,
        viewState = viewState,
        executeAction = { viewModel.executeAction(it) },
        onNavigate = onNavigate
    )
}

@Composable
private fun MainContent(
    viewState: State = State(),
    snackbarHostState: SnackbarHostState,
    executeAction: (Action) -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
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
            Text(
                text = viewState.code,
                color = if (!viewState.activated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(text = "Activated: ${viewState.activated}")
            Button(
                onClick = { onNavigate(Destination.Reveal.route) },
                enabled = viewState.notRevealed
            ) {
                Text(text = stringResource(id = R.string.reveal_code))
            }
            Button(
                onClick = { onNavigate(Destination.Activate.route) },
                enabled = !viewState.activated && !viewState.notRevealed
            ) {
                Text(text = stringResource(id = R.string.activate_code))
            }
            if (viewState.activated && !viewState.notRevealed) {
                Button(
                    onClick = { executeAction(Action.Reset) }
                ) {
                    Text(text = stringResource(id = R.string.reset))
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenContentPreview() {
    MaterialTheme {
        MainContent(
            viewState = State(code = "XXXXXX", activated = false),
            snackbarHostState = SnackbarHostState(),
            {},
            {}
        )
    }
}