package com.vpn.mindcycle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vpn.mindcycle.data.model.CyclePhase
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.data.model.MoodLevel
import org.threeten.bp.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    onNavigateBack: () -> Unit,
    onSaveEntry: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf<MoodLevel?>(null) }
    var selectedPhase by remember { mutableStateOf<CyclePhase?>(null) }
    var symptoms by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isPeriodStart by remember { mutableStateOf(false) }

    val symptomsList = listOf(
        "Усталость", "Боль", "Раздражение", "Тревога",
        "Головная боль", "Спазмы", "Вздутие", "Бессонница"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Новая запись",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Добавить запись",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Выбор настроения
            Text("Настроение", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodLevel.values().forEach { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood },
                        label = { Text(mood.toString()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор фазы цикла
            Text("Фаза цикла", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CyclePhase.values().forEach { phase ->
                    FilterChip(
                        selected = selectedPhase == phase,
                        onClick = { selectedPhase = phase },
                        label = { Text(phase.toString()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Симптомы
            OutlinedTextField(
                value = symptoms,
                onValueChange = { symptoms = it },
                label = { Text("Симптомы") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Заметки
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Заметки") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Переключатель начала менструации
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Начало менструации")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isPeriodStart,
                    onCheckedChange = { isPeriodStart = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedMood != null && selectedPhase != null) {
                        onSaveEntry(
                            MoodEntry(
                                date = LocalDateTime.now(),
                                moodLevel = selectedMood!!,
                                cyclePhase = selectedPhase!!,
                                symptoms = symptoms.split(",").map { it.trim() },
                                notes = notes.takeIf { it.isNotBlank() },
                                isPeriodStart = isPeriodStart
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMood != null && selectedPhase != null
            ) {
                Text("Сохранить")
            }
        }
    }
} 