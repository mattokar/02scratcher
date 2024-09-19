package com.example.o2scratch.ui.compose

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.o2scratch.R
import com.example.o2scratch.manager.ScratchCardManager
import com.example.o2scratch.ui.compose.ScratchCardContract.Action
import com.example.o2scratch.ui.compose.ScratchCardContract.Event
import com.example.o2scratch.ui.compose.ScratchCardContract.State
import com.example.o2scratch.ui.compose.base.BaseComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ScratchCardViewModel @Inject constructor(
    private val scratchCardManager: ScratchCardManager,
) : BaseComposeViewModel<State, Event, Action>(State()) {

    private var revealJob: Job? = null

    override fun processViewAction(action: Action) {
        when (action) {
            is Action.RevealCode -> revealCode()
            is Action.CancelRevealCode -> cancelCodeReveal()
            is Action.Activate -> activateCode()
            is Action.Reset -> updateState { copy(code = "XXXXXX", activated = false) }
        }
    }


    private fun cancelCodeReveal() {
        revealJob?.cancel()
        updateState { copy(loading = false) }
    }

    private fun revealCode() {
        revealJob = viewModelScope.launch {
            updateState { copy(loading = true) }
            delay(2000)
            val code = UUID.randomUUID().toString()
            updateState { copy(code = code, loading = false) }
        }
    }

    private fun activateCode() {
        viewModelScope.launch {
            runCatching {
                scratchCardManager.activateScratchCard(viewState.code)
            }
                .onSuccess { response ->
                    val version = response.android?.toInt()
                    val success = version != null && version.toInt() > ScratchCardManager.SCRATCH_CARD_ACTIVATION_THRESHOLD
                    updateState { copy(activated = success) }
                    if (!success) {
                        sendEvent(Event.Error(R.string.activation_error))
                    }
                }
                .onFailure {
                    sendEvent(Event.Error(R.string.activation_error))
                    Log.e(tag, "$it")
                }
        }
    }
}