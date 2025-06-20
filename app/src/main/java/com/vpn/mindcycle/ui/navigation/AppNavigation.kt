package com.vpn.mindcycle.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vpn.mindcycle.data.model.CyclePrediction
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.ui.screens.AddEntryScreen
import com.vpn.mindcycle.ui.screens.CalendarScreen
import com.vpn.mindcycle.ui.screens.EntriesListScreen
import androidx.compose.material3.SnackbarDuration

object AppDestinations {
    const val CALENDAR_ROUTE = "calendar"
    const val ADD_ENTRY_ROUTE = "add_entry"
    const val ENTRIES_LIST_ROUTE = "entries_list"
}

@Composable
fun AppNavigation(
    entries: List<MoodEntry>,
    cyclePrediction: CyclePrediction?,
    isLoading: Boolean,
    errorMessage: String?,
    onAddEntry: (MoodEntry) -> Unit,
    onDeleteEntry: (MoodEntry) -> Unit,
    onDismissError: () -> Unit
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        NavHost(navController = navController, startDestination = AppDestinations.CALENDAR_ROUTE) {
            composable(AppDestinations.CALENDAR_ROUTE) {
                CalendarScreen(
                    entries = entries,
                    cyclePrediction = cyclePrediction,
                    onAddEntry = { entry ->
                        onAddEntry(entry)
                        navController.navigateUp()
                    },
                    onDeleteEntry = onDeleteEntry,
                    onNavigateToAddEntry = {
                        navController.navigate(AppDestinations.ADD_ENTRY_ROUTE)
                    },
                    onNavigateToEntriesList = {
                        navController.navigate(AppDestinations.ENTRIES_LIST_ROUTE)
                    }
                )
            }

            composable(AppDestinations.ADD_ENTRY_ROUTE) {
                AddEntryScreen(
                    onSaveEntry = { entry ->
                        onAddEntry(entry)
                        navController.navigateUp()
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable(AppDestinations.ENTRIES_LIST_ROUTE) {
                EntriesListScreen(
                    entries = entries,
                    onDeleteEntry = onDeleteEntry,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Error handling
        errorMessage?.let { error ->
            LaunchedEffect(error) {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Short
                )
                onDismissError()
            }
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
} 