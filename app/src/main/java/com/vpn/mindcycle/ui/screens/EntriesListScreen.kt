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
    onNavigateBack: () -> Unit,
    onEditEntry: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEntry by remember { mutableStateOf<MoodEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Все записи",
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
        ) {
            // Статистика
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
                        "Общая статистика",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Всего записей", entries.size.toString())
                        StatItem("Среднее настроение", 
                            when {
                                entries.isEmpty() -> "Нет данных"
                                entries.map { it.moodLevel.ordinal }.average() < 1.5 -> "😫"
                                entries.map { it.moodLevel.ordinal }.average() < 2.5 -> "😔"
                                entries.map { it.moodLevel.ordinal }.average() < 3.5 -> "😐"
                                entries.map { it.moodLevel.ordinal }.average() < 4.5 -> "🙂"
                                else -> "😊"
                            }
                        )
                    }
                }
            }

            // Список записей
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries.sortedByDescending { it.date }) { entry ->
                    EntryCard(
                        entry = entry,
                        onClick = { selectedEntry = entry }
                    )
                }
            }
        }
    }

    // Диалог с деталями записи
    selectedEntry?.let { entry ->
        EntryDetailsDialog(
            entry = entry,
            onDismiss = { selectedEntry = null },
            onEdit = {
                selectedEntry = null
                onEditEntry(entry)
            }
        )
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

                if (entry.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Заметка",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(entry.note)
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