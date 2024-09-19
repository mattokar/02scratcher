package com.example.o2scratch.ui.compose

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


sealed class Destination(var route: String) {
    data object Main : Destination("main")
    data object Reveal : Destination("reveal")
    data object Activate : Destination("activate")
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val viewModel : ScratchCardViewModel = hiltViewModel()

    NavHost(navController, startDestination = Destination.Main.route) {
        composable(Destination.Main.route) {
            MainScreen(viewModel) { navController.navigate(it) }
        }
        composable(Destination.Reveal.route) {
            RevealScreen(viewModel) {
                if (navController.navigateUp()) {
                    viewModel.executeAction(ScratchCardContract.Action.CancelRevealCode)
                }
            }
        }
        composable(Destination.Activate.route) {
            ActivateScreen(viewModel) {
                navController.navigateUp()
            }
        }
    }
}