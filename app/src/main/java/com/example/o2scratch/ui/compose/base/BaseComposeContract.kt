package com.example.o2scratch.ui.compose.base

/**
 * Describes UI state to render.
 * Flow: ViewModel -> Composable
 */
abstract class ViewState

/**
 * One time events like navigation or showing snackbar.
 * Flow: ViewModel -> Composable
 */
abstract class ViewEvent

/**
 * Action to be executed by ViewModel. It is only way how to call VM from UI.
 * Flow: Composable -> ViewModel.
 */
abstract class ViewAction