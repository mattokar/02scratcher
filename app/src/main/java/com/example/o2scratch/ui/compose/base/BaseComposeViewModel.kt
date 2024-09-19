package com.example.o2scratch.ui.compose.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

/**
 * Base class for ViewModel in MVI architecture.
 *
 * @param UiState State of the UI.
 * @param Event Event that can be sent to the UI.
 * @param Action Action that can be executed by the ViewModel.
 */
abstract class BaseComposeViewModel<UiState : ViewState, Event : ViewEvent, Action : ViewAction>(
    initialState: UiState
) : ViewModel() {

    protected val tag = this::class.java.simpleName

    protected abstract fun processViewAction(action: Action)

    private val _viewStateFlow: MutableStateFlow<UiState> = MutableStateFlow(initialState)
    val viewStateFlow = _viewStateFlow.asStateFlow()
    val viewState
        get() = viewStateFlow.value

    private val _viewEventsFlow = Channel<Event>(Channel.BUFFERED)
    val viewEventsFlow = _viewEventsFlow.receiveAsFlow()

    private val _viewActionsFlow: MutableSharedFlow<Action> = MutableSharedFlow()

    init {
        Log.d(tag, "onCreate()")
        viewModelScope.launch {
            _viewActionsFlow.collect {
                runCatching {
                    processViewAction(it)
                }.onFailure {
                    Log.e(tag, "Failed to process action $it")
                }
            }
        }
    }

    protected fun updateState(update: UiState.() -> UiState) {
        val old = viewState
        val new = _viewStateFlow.updateAndGet(update)
        if (old != new)
            Log.d(tag, "State: $new")
    }

    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            _viewEventsFlow.send(event)
            Log.d(tag, "Sent Event: $event")
        }
    }

    fun executeAction(action: Action) {
        viewModelScope.launch {
            _viewActionsFlow.emit(action)
            Log.d(tag, "Executed Action: $action")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(tag, "onCleared()")
    }

}