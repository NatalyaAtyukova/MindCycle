package com.vpn.mindcycle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.vpn.mindcycle.data.db.MoodDatabase
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.ui.navigation.AppNavigation
import com.vpn.mindcycle.ui.theme.MindCycleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class MainActivity : ComponentActivity() {
    private lateinit var database: MoodDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            MoodDatabase::class.java,
            "mood_database"
        ).build()

        setContent {
            MindCycleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val entries by database.moodEntryDao()
                        .getEntriesBetweenDates(
                            LocalDate.now().minusMonths(1),
                            LocalDate.now().plusMonths(1)
                        )
                        .collectAsState(initial = emptyList())

                    AppNavigation(
                        navController = navController,
                        entries = entries,
                        onAddEntry = { entry: MoodEntry ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.moodEntryDao().insertEntry(entry)
                            }
                        }
                    )
                }
            }
        }
    }
}