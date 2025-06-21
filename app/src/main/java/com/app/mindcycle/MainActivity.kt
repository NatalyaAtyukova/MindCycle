package com.app.mindcycle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.mindcycle.data.db.MoodDatabase
import com.app.mindcycle.data.model.CyclePrediction
import com.app.mindcycle.data.model.MoodEntry
import com.app.mindcycle.ui.navigation.AppNavigation
import com.app.mindcycle.ui.theme.MindCycleTheme
import com.app.mindcycle.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MindCycleTheme {
                val viewModel: MainViewModel = viewModel()
                var isLoading by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                val coroutineScope = rememberCoroutineScope()

                // Load initial data
                LaunchedEffect(Unit) {
                    try {
                        isLoading = true
                        viewModel.loadInitialData()
                    } catch (e: Exception) {
                        errorMessage = "Failed to load data: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }

                val entries by viewModel.entries.collectAsState()
                val cyclePrediction by viewModel.cyclePrediction.collectAsState()

                AppNavigation(
                    entries = entries,
                    cyclePrediction = cyclePrediction,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onAddEntry = { entry ->
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            try {
                                viewModel.addEntry(entry)
                            } catch (e: Exception) {
                                errorMessage = "Failed to add entry: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    onDeleteEntry = { entry ->
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            try {
                                viewModel.deleteEntry(entry)
                            } catch (e: Exception) {
                                errorMessage = "Failed to delete entry: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    onDismissError = {
                        errorMessage = null
                    }
                )
            }
        }
    }
}