package com.vpn.mindcycle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vpn.mindcycle.data.model.CyclePhase
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.data.model.MoodLevel
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesListScreen(
    entries: List<MoodEntry>,
    onDeleteEntry: (MoodEntry) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Список записей",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Список записей
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries.sortedByDescending { it.date }) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = entry.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Настроение: ${entry.moodLevel}")
                        Text("Фаза цикла: ${entry.cyclePhase}")
                        if (entry.symptoms.isNotEmpty()) {
                            Text("Симптомы: ${entry.symptoms.joinToString(", ")}")
                        }
                        entry.notes?.let { Text("Заметки: $it") }
                        if (entry.isPeriodStart) {
                            Text("Начало менструации")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onDeleteEntry(entry) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Удалить")
                        }
                    }
                }
            }
        }

        // Кнопка возврата
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Назад")
        }
    }
}

@Composable
private fun EntryCard(
    entry: MoodEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = entry.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (entry.moodLevel) {
                            MoodLevel.VERY_BAD -> "😫"
                            MoodLevel.BAD -> "😔"
                            MoodLevel.NEUTRAL -> "😐"
                            MoodLevel.GOOD -> "🙂"
                            MoodLevel.VERY_GOOD -> "😃"
                            MoodLevel.EXCELLENT -> "😊"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (entry.cyclePhase) {
                            CyclePhase.MENSTRUATION -> "Менструация"
                            CyclePhase.FOLLICULAR -> "Фолликулярная фаза"
                            CyclePhase.OVULATION -> "Овуляция"
                            CyclePhase.LUTEAL -> "Лютеиновая фаза"
                            CyclePhase.PMS -> "ПМС"
                            CyclePhase.NONE -> "Не выбрано"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Редактировать",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EntryDetailsDialog(
    entry: MoodEntry,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = entry.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Настроение
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Настроение",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        when (entry.moodLevel) {
                            MoodLevel.VERY_BAD -> "😫"
                            MoodLevel.BAD -> "😔"
                            MoodLevel.NEUTRAL -> "😐"
                            MoodLevel.GOOD -> "🙂"
                            MoodLevel.VERY_GOOD -> "😃"
                            MoodLevel.EXCELLENT -> "😊"
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Фаза цикла
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Фаза цикла",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        when (entry.cyclePhase) {
                            CyclePhase.MENSTRUATION -> "Менструация"
                            CyclePhase.FOLLICULAR -> "Фолликулярная фаза"
                            CyclePhase.OVULATION -> "Овуляция"
                            CyclePhase.LUTEAL -> "Лютеиновая фаза"
                            CyclePhase.PMS -> "ПМС"
                            CyclePhase.NONE -> "Не выбрано"
                        }
                    )
                }

                if (entry.symptoms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Симптомы",
                        style = MaterialTheme.typography.titleMedium
                    )
                    entry.symptoms.forEach { symptom ->
                        Text(
                            "• $symptom",
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                if (!entry.notes.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Заметка",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(entry.notes ?: "")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Редактировать",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
} 