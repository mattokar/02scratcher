package com.example.o2scratch.ui.compose.base

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Scope of action handler in event processor holds context reference.
 * It allows to access resources in more convenient way.
 */
@Immutable
data class EventsProcessorScope(val context: Context)

/**
 * Process [ViewEvent]s inside [LaunchedEffect] in lifecycle aware manner.
 *
 * @param viewEvents Flow of view events.
 * @param action Action to be executed on each view event.
 */
@Composable
fun <Event : ViewEvent> EventsProcessor(
    viewEvents: Flow<Event>,
    action: suspend EventsProcessorScope.(Event) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val eventsProcessorScope = remember(context) { EventsProcessorScope(context) }
    LaunchedEffect(lifecycleOwner, eventsProcessorScope) {
        viewEvents
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect {
                launch {
                    runCatching {
                        eventsProcessorScope.action(it)
                    }.onFailure {
                        Log.e(this::class.java.simpleName, "Failed to process event $it")
                    }
                }
            }
    }
}

fun EventsProcessorScope.getString(@StringRes id: Int): String = context.getString(id)