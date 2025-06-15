package com.vpn.mindcycle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vpn.mindcycle.data.model.CyclePhase
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.data.model.MoodLevel
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    entries: List<MoodEntry>,
    onAddEntry: () -> Unit,
    onViewAllEntries: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEntry by remember { mutableStateOf<MoodEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { selectedDate = selectedDate.minusMonths(1) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Предыдущий месяц")
                    }
                },
                actions = {
                    IconButton(onClick = { selectedDate = selectedDate.plusMonths(1) }) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Следующий месяц")
                    }
                    IconButton(onClick = onViewAllEntries) {
                        Icon(Icons.Filled.List, contentDescription = "Все записи")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEntry,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить запись")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
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
                        "Статистика за месяц",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Записей", entries.size.toString())
                        StatItem("Среднее настроение", 
                            when {
                                entries.map { it.moodLevel.ordinal }.average().isNaN() -> "Нет данных"
                                entries.map { it.moodLevel.ordinal }.average() < 1.5 -> "😫"
                                entries.map { it.moodLevel.ordinal }.average() < 2.5 -> "😔"
                                entries.map { it.moodLevel.ordinal }.average() < 3.5 -> "😐"
                                entries.map { it.moodLevel.ordinal }.average() < 4.5 -> "🙂"
                                else -> "😊"
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Частая фаза", entries.groupBy { it.cyclePhase }.maxByOrNull { it.value.size }?.key?.toString() ?: "Не выбрано")
                        StatItem("Частый симптом", entries.flatMap { it.symptoms }.groupBy { it }.maxByOrNull { it.value.size }?.key ?: "")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items((1 until selectedDate.dayOfWeek.value).toList()) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }

                items((1..selectedDate.lengthOfMonth()).toList()) { day ->
                    val date = selectedDate.withDayOfMonth(day)
                    val dayEntry = entries.find { it.date == date }
                    CalendarDay(
                        date = date,
                        entry = dayEntry,
                        isSelected = date == selectedDate,
                        onClick = { selectedEntry = dayEntry }
                    )
                }
            }
        }
    }

    selectedEntry?.let { entry ->
        DayDetailsDialog(
            date = selectedDate,
            entry = entry,
            onDismiss = { selectedEntry = null },
            onAddEntry = {
                selectedEntry = null
                onAddEntry()
            }
        )
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

@Composable
fun CalendarDay(
    date: LocalDate,
    entry: MoodEntry?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    entry != null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            if (entry != null) {
                Text(
                    text = when (entry.moodLevel) {
                        MoodLevel.VERY_BAD -> "😫"
                        MoodLevel.BAD -> "😔"
                        MoodLevel.NEUTRAL -> "😐"
                        MoodLevel.GOOD -> "🙂"
                        MoodLevel.EXCELLENT -> "😊"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun DayDetailsDialog(
    date: LocalDate,
    entry: MoodEntry?,
    onDismiss: () -> Unit,
    onAddEntry: () -> Unit
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
                    text = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                if (entry != null) {
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
                } else {
                    Text(
                        "Нет записей на этот день",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAddEntry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (entry != null) "Редактировать" else "Добавить запись",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

data class MonthlyStats(
    val totalEntries: Int,
    val averageMood: Double,
    val mostCommonPhase: CyclePhase,
    val mostCommonSymptoms: String
) 