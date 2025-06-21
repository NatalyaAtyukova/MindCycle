package com.app.mindcycle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mindcycle.data.model.CyclePhase
import com.app.mindcycle.data.model.CyclePrediction
import com.app.mindcycle.data.model.MoodEntry
import com.app.mindcycle.data.model.MoodLevel
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.WeekFields
import java.util.*

// Карты для иконок и русских подписей вынесены на уровень файла
private val moodIcons = mapOf(
    MoodLevel.VERY_BAD to Icons.Filled.MoodBad,
    MoodLevel.BAD to Icons.Filled.SentimentDissatisfied,
    MoodLevel.NEUTRAL to Icons.Filled.Psychology,
    MoodLevel.GOOD to Icons.Filled.SentimentSatisfied,
    MoodLevel.VERY_GOOD to Icons.Filled.SentimentVerySatisfied,
    MoodLevel.EXCELLENT to Icons.Filled.Star
)

private val moodLabels = mapOf(
    MoodLevel.VERY_BAD to "Очень плохо",
    MoodLevel.BAD to "Плохо",
    MoodLevel.NEUTRAL to "Нейтрально",
    MoodLevel.GOOD to "Хорошо",
    MoodLevel.VERY_GOOD to "Очень хорошо",
    MoodLevel.EXCELLENT to "Отлично"
)

private val phaseIcons = mapOf(
    CyclePhase.MENSTRUATION to Icons.Filled.Bloodtype,
    CyclePhase.FOLLICULAR to Icons.Filled.Spa,
    CyclePhase.OVULATION to Icons.Filled.WbSunny,
    CyclePhase.LUTEAL to Icons.Filled.Nightlight,
    CyclePhase.PMS to Icons.Filled.MoodBad,
    CyclePhase.NONE to Icons.Filled.RemoveCircle
)

private val phaseLabels = mapOf(
    CyclePhase.MENSTRUATION to "Менструация",
    CyclePhase.FOLLICULAR to "Фолликулярная",
    CyclePhase.OVULATION to "Овуляция",
    CyclePhase.LUTEAL to "Лютеиновая",
    CyclePhase.PMS to "ПМС",
    CyclePhase.NONE to "Нет"
)

private val symptomIcons = mapOf(
    "Усталость" to Icons.Filled.BatteryAlert,
    "Боль" to Icons.Filled.Healing,
    "Раздражение" to Icons.Filled.MoodBad,
    "Тревога" to Icons.Filled.SentimentDissatisfied,
    "Головная боль" to Icons.Filled.Psychology,
    "Спазмы" to Icons.Filled.FlashOn,
    "Вздутие" to Icons.Filled.Air,
    "Бессонница" to Icons.Filled.NightsStay
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    entries: List<MoodEntry>,
    cyclePrediction: CyclePrediction?,
    onNavigateToAddEntry: (String) -> Unit,
    onNavigateToEditEntry: (Long) -> Unit,
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
            Button(onClick = { onNavigateToAddEntry(selectedDate.toString()) }) {
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
                            val isToday = date == LocalDate.now()
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                                            isToday -> MaterialTheme.colorScheme.secondaryContainer
                                            isPredictedPeriod -> Color.Red.copy(alpha = 0.3f)
                                            isPredictedOvulation -> Color.Green.copy(alpha = 0.3f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        selectedDate = date
                                        if (entry != null) {
                                            selectedEntry = entry
                                            showDayDetails = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = (day + 1).toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    entry?.let {
                                        Icon(
                                            imageVector = moodIcons[it.moodLevel] ?: Icons.Default.HelpOutline,
                                            contentDescription = "Mood",
                                            modifier = Modifier.size(20.dp),
                                            tint = if (it.isPeriod) Color.Red else LocalContentColor.current
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

    if (showDayDetails && selectedEntry != null) {
        AlertDialog(
            onDismissRequest = { showDayDetails = false },
            title = { Text("Детали записи") },
            text = {
                Column {
                    Text("Дата: ${selectedEntry!!.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}")
                    Text("Настроение: ${moodLabels[selectedEntry!!.moodLevel]}")
                    Text("Фаза цикла: ${phaseLabels[selectedEntry!!.cyclePhase]}")
                    if (selectedEntry!!.symptoms.isNotEmpty()) {
                        Text("Симптомы: ${selectedEntry!!.symptoms.joinToString(", ")}")
                    }
                    if (!selectedEntry!!.note.isNullOrEmpty()) {
                        Text("Заметки: ${selectedEntry!!.note}")
                    }
                    if (selectedEntry!!.isPeriodStart) {
                        Text("Начало менструации")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onNavigateToEditEntry(selectedEntry!!.id)
                    showDayDetails = false
                }) {
                    Text("Редактировать")
                }
            },
            dismissButton = {
                Button(onClick = { showDayDetails = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DayDetailsDialog(
    entry: MoodEntry,
    onDismissRequest: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Запись за ${entry.date.toLocalDate()}") },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = moodIcons[entry.moodLevel] ?: Icons.Filled.Psychology,
                                contentDescription = moodLabels[entry.moodLevel] ?: entry.moodLevel.toString(),
                                tint = when (entry.moodLevel) {
                                    MoodLevel.VERY_BAD -> Color(0xFFD32F2F)
                                    MoodLevel.BAD -> Color(0xFFF57C00)
                                    MoodLevel.NEUTRAL -> Color(0xFF757575)
                                    MoodLevel.GOOD -> Color(0xFF388E3C)
                                    MoodLevel.VERY_GOOD -> Color(0xFF1976D2)
                                    MoodLevel.EXCELLENT -> Color(0xFFD81B60)
                                    else -> Color.Unspecified
                                },
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = moodLabels[entry.moodLevel] ?: entry.moodLevel.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = phaseIcons[entry.cyclePhase] ?: Icons.Filled.RemoveCircle,
                                contentDescription = phaseLabels[entry.cyclePhase] ?: entry.cyclePhase.toString(),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = phaseLabels[entry.cyclePhase] ?: entry.cyclePhase.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (entry.symptoms.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                entry.symptoms.forEach { symptom ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(symptom) },
                                        leadingIcon = { Icon(symptomIcons[symptom] ?: Icons.Filled.Psychology, contentDescription = symptom) }
                                    )
                                }
                            }
                        }
                        if (!entry.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Заметки:", style = MaterialTheme.typography.titleMedium)
                            Text(text = entry.note, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (entry.isPeriodStart) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color.Red)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Начало менструации", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Закрыть")
            }
        },
        dismissButton = {
            TextButton(onClick = onEdit) {
                Text("Редактировать")
            }
        }
    )
} 