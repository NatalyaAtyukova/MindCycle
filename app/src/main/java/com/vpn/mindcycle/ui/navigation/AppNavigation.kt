package com.vpn.mindcycle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.ui.screens.AddEntryScreen
import com.vpn.mindcycle.ui.screens.CalendarScreen

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object AddEntry : Screen("add_entry")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onSaveEntry: (MoodEntry) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Calendar.route
    ) {
        composable(Screen.Calendar.route) {
            CalendarScreen(
                onAddEntry = {
                    navController.navigate(Screen.AddEntry.route)
                }
            )
        }
        
        composable(Screen.AddEntry.route) {
            AddEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveEntry = { entry ->
                    onSaveEntry(entry)
                    navController.popBackStack()
                }
            )
        }
    }
} 