package com.vpn.mindcycle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vpn.mindcycle.data.model.CyclePrediction
import com.vpn.mindcycle.data.model.MoodEntry
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.WeekFields
import java.util.*

@Composable
fun CalendarScreen(
    entries: List<MoodEntry>,
    cyclePrediction: CyclePrediction?,
    onAddEntry: (MoodEntry) -> Unit,
    onDeleteEntry: (MoodEntry) -> Unit,
    onNavigateToAddEntry: () -> Unit,
    onNavigateToEntriesList: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDayDetails by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<MoodEntry?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Календарь",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Предсказание цикла
        cyclePrediction?.let { prediction ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Предсказание цикла",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Следующая менструация: ${prediction.nextPeriodStart.toLocalDate()}")
                    Text("Овуляция: ${prediction.nextOvulation.toLocalDate()}")
                    Text("Средняя длительность цикла: ${prediction.averageCycleLength} дней")
                }
            }
        }

        // Кнопки навигации
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onNavigateToAddEntry) {
                Text("Добавить запись")
            }
            Button(onClick = onNavigateToEntriesList) {
                Text("Список записей")
            }
        }

        // Статистика
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Статистика",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Всего записей: ${entries.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                val averageMood = entries.map { it.moodLevel.ordinal }.average()
                Text(
                    text = "Среднее настроение: ${String.format("%.1f", averageMood)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Календарь
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Здесь будет реализация календаря
                val currentMonth = YearMonth.from(selectedDate)
                val firstDayOfMonth = currentMonth.atDay(1)
                val lastDayOfMonth = currentMonth.atEndOfMonth()
                val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
                val daysInMonth = lastDayOfMonth.dayOfMonth
                val firstDayOfMonthWeekday = firstDayOfMonth.dayOfWeek.value
                val offsetDays = (firstDayOfMonthWeekday - firstDayOfWeek.value + 7) % 7

                Column {
                    // Заголовок месяца
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = { /* TODO: Добавить навигацию по месяцам */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Следующий месяц")
                        }
                    }

                    // Дни недели
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                        weekDays.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Сетка календаря
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Пустые ячейки в начале
                        items(offsetDays) {
                            Box(modifier = Modifier.aspectRatio(1f))
                        }

                        // Дни месяца
                        items(daysInMonth) { day ->
                            val date = currentMonth.atDay(day + 1)
                            val isSelected = date == selectedDate
                            val entry = entries.find { it.date.toLocalDate() == date }
                            val isPredictedPeriod = cyclePrediction?.let { prediction ->
                                date.isAfter(prediction.nextPeriodStart.toLocalDate().minusDays(1)) &&
                                date.isBefore(prediction.nextPeriodEnd.toLocalDate().plusDays(1))
                            } ?: false
                            val isPredictedOvulation = cyclePrediction?.let { prediction ->
                                date == prediction.nextOvulation.toLocalDate()
                            } ?: false

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .background(
                                        when {
                                            isPredictedPeriod -> Color(0xFFFFE4E1)
                                            isPredictedOvulation -> Color(0xFFE6E6FA)
                                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        selectedDate = date
                                        selectedEntry = entry
                                        showDayDetails = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = (day + 1).toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    entry?.let {
                                        Text(
                                            text = it.moodLevel.name,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог с деталями дня
    if (showDayDetails) {
        val dayEntries = entries.filter { it.date.toLocalDate() == selectedDate }
        AlertDialog(
            onDismissRequest = { showDayDetails = false },
            title = { Text("Записи за ${selectedDate}") },
            text = {
                Column {
                    dayEntries.forEach { entry ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Настроение: ${entry.moodLevel}")
                                Text("Фаза цикла: ${entry.cyclePhase}")
                                if (entry.symptoms.isNotEmpty()) {
                                    Text("Симптомы: ${entry.symptoms.joinToString(", ")}")
                                }
                                entry.notes?.let { Text("Заметки: $it") }
                                if (entry.isPeriodStart) {
                                    Text("Начало менструации")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDayDetails = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
} 