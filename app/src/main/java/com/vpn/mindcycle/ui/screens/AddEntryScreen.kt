package com.vpn.mindcycle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vpn.mindcycle.data.model.CyclePhase
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.data.model.MoodLevel
import org.threeten.bp.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    onNavigateBack: () -> Unit,
    onSaveEntry: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedMood by remember { mutableStateOf<MoodLevel?>(null) }
    var selectedCyclePhase by remember { mutableStateOf<CyclePhase?>(null) }
    var note by remember { mutableStateOf("") }
    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }

    val symptoms = listOf(
        "Усталость", "Боль", "Раздражение", "Тревога",
        "Головная боль", "Спазмы", "Вздутие", "Бессонница"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая запись") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date selector
            OutlinedTextField(
                value = selectedDate.toString(),
                onValueChange = { },
                label = { Text("Дата") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            // Mood selection
            Text("Настроение", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodLevel.values().forEach { mood ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .selectable(
                                selected = selectedMood == mood,
                                onClick = { selectedMood = mood }
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = when (mood) {
                                MoodLevel.VERY_BAD -> "😫"
                                MoodLevel.BAD -> "😔"
                                MoodLevel.NEUTRAL -> "😐"
                                MoodLevel.GOOD -> "🙂"
                                MoodLevel.EXCELLENT -> "😊"
                            },
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            // Cycle phase selection
            Text("Фаза цикла", style = MaterialTheme.typography.titleMedium)
            Column {
                CyclePhase.values().forEach { phase ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedCyclePhase == phase,
                                onClick = { selectedCyclePhase = phase }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCyclePhase == phase,
                            onClick = { selectedCyclePhase = phase }
                        )
                        Text(
                            text = when (phase) {
                                CyclePhase.MENSTRUATION -> "Менструация"
                                CyclePhase.FOLLICULAR -> "Фолликулярная фаза"
                                CyclePhase.OVULATION -> "Овуляция"
                                CyclePhase.LUTEAL -> "Лютеиновая фаза"
                                CyclePhase.PMS -> "ПМС"
                                CyclePhase.NONE -> "Не выбрано"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Symptoms selection
            Text("Симптомы", style = MaterialTheme.typography.titleMedium)
            Column {
                symptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = symptom in selectedSymptoms,
                            onCheckedChange = { checked ->
                                selectedSymptoms = if (checked) {
                                    selectedSymptoms + symptom
                                } else {
                                    selectedSymptoms - symptom
                                }
                            }
                        )
                        Text(
                            text = symptom,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Note field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Заметка") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Save button
            Button(
                onClick = {
                    selectedMood?.let { mood ->
                        onSaveEntry(
                            MoodEntry(
                                date = selectedDate,
                                moodLevel = mood,
                                cyclePhase = selectedCyclePhase ?: CyclePhase.NONE,
                                symptoms = selectedSymptoms.toList(),
                                note = note
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMood != null
            ) {
                Text("Сохранить")
            }
        }
    }
} 