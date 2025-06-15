package com.vpn.mindcycle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.jakewharton.threetenabp.AndroidThreeTen
import com.vpn.mindcycle.data.db.MoodDatabase
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.ui.navigation.AppNavigation
import com.vpn.mindcycle.ui.theme.MindCycleTheme
import com.vpn.mindcycle.ui.viewmodel.MoodViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)

        val database = Room.databaseBuilder(
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
                    MindCycleApp(database)
                }
            }
        }
    }
}

@Composable
fun MindCycleApp(database: MoodDatabase) {
    val navController = rememberNavController()
    val viewModel: MoodViewModel = viewModel(
        factory = MoodViewModel.Factory(database.moodEntryDao())
    )

    AppNavigation(
        navController = navController,
        onSaveEntry = { entry ->
            viewModel.addEntry(entry)
        }
    )
}