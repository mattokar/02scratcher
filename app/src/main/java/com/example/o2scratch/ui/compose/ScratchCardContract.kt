package com.example.o2scratch.ui.compose

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.o2scratch.ui.compose.base.ViewAction
import com.example.o2scratch.ui.compose.base.ViewEvent
import com.example.o2scratch.ui.compose.base.ViewState

class ScratchCardContract {
    @Immutable
    data class State(
        val code: String = "XXXXXX",
        val activated: Boolean = false,
        val loading: Boolean = false
    ) : ViewState() {
        val notRevealed: Boolean
            get() = code == "XXXXXX"
    }

    sealed class Action : ViewAction() {
        data object RevealCode : Action()
        data object CancelRevealCode : Action()
        data object Activate : Action()
        data object Reset : Action()
    }

    sealed class Event : ViewEvent() {
        data class Error(@StringRes val message: Int) : Event()
    }
}