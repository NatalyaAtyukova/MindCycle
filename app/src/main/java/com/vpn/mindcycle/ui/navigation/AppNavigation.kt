package com.vpn.mindcycle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.ui.screens.AddEntryScreen
import com.vpn.mindcycle.ui.screens.CalendarScreen
import com.vpn.mindcycle.ui.screens.EntriesListScreen

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object AddEntry : Screen("add_entry")
    object EntriesList : Screen("entries_list")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    entries: List<MoodEntry>,
    onAddEntry: (MoodEntry) -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Calendar.route) {
        composable(Screen.Calendar.route) {
            CalendarScreen(
                entries = entries,
                onAddEntry = { navController.navigate(Screen.AddEntry.route) },
                onViewAllEntries = { navController.navigate(Screen.EntriesList.route) }
            )
        }
        composable(Screen.AddEntry.route) {
            AddEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveEntry = { entry ->
                    onAddEntry(entry)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EntriesList.route) {
            EntriesListScreen(
                entries = entries,
                onNavigateBack = { navController.popBackStack() },
                onEditEntry = { _ ->
                    navController.navigate(Screen.AddEntry.route)
                }
            )
        }
    }
} 