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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RemoveCircle
import org.threeten.bp.format.DateTimeFormatter
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import org.threeten.bp.ZoneId

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEntryScreen(
    entryToEdit: MoodEntry?,
    onNavigateBack: () -> Unit,
    onSaveEntry: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf(entryToEdit?.moodLevel) }
    var selectedPhase by remember { mutableStateOf(entryToEdit?.cyclePhase) }
    var note by remember { mutableStateOf(entryToEdit?.note ?: "") }
    var isPeriodStart by remember { mutableStateOf(entryToEdit?.isPeriodStart ?: false) }
    var selectedSymptoms by remember { mutableStateOf(entryToEdit?.symptoms?.toSet() ?: emptySet()) }
    var entryDate by remember { mutableStateOf(entryToEdit?.date ?: LocalDateTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val symptomsList = listOf(
        "Усталость" to Icons.Filled.BatteryAlert,
        "Боль" to Icons.Filled.Healing,
        "Раздражение" to Icons.Filled.MoodBad,
        "Тревога" to Icons.Filled.SentimentDissatisfied,
        "Головная боль" to Icons.Filled.Psychology,
        "Спазмы" to Icons.Filled.FlashOn,
        "Вздутие" to Icons.Filled.Air,
        "Бессонница" to Icons.Filled.NightsStay
    )

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = entryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    var tempHour by remember { mutableStateOf(entryDate.hour) }
    var tempMinute by remember { mutableStateOf(entryDate.minute) }

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
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val moodIcons = mapOf(
                    MoodLevel.VERY_BAD to Icons.Filled.MoodBad,
                    MoodLevel.BAD to Icons.Filled.SentimentDissatisfied,
                    MoodLevel.NEUTRAL to Icons.Filled.Psychology,
                    MoodLevel.GOOD to Icons.Filled.SentimentSatisfied,
                    MoodLevel.VERY_GOOD to Icons.Filled.SentimentVerySatisfied,
                    MoodLevel.EXCELLENT to Icons.Filled.Star
                )
                val moodLabels = mapOf(
                    MoodLevel.VERY_BAD to "Очень плохо",
                    MoodLevel.BAD to "Плохо",
                    MoodLevel.NEUTRAL to "Нейтрально",
                    MoodLevel.GOOD to "Хорошо",
                    MoodLevel.VERY_GOOD to "Очень хорошо",
                    MoodLevel.EXCELLENT to "Отлично"
                )
                MoodLevel.values().forEach { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood },
                        label = { Text(moodLabels[mood] ?: mood.toString()) },
                        leadingIcon = { Icon(moodIcons[mood] ?: Icons.Filled.Psychology, contentDescription = mood.toString()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор фазы цикла
            Text("Фаза цикла", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val phaseIcons = mapOf(
                    CyclePhase.MENSTRUATION to Icons.Filled.Bloodtype,
                    CyclePhase.FOLLICULAR to Icons.Filled.Spa,
                    CyclePhase.OVULATION to Icons.Filled.WbSunny,
                    CyclePhase.LUTEAL to Icons.Filled.Nightlight,
                    CyclePhase.PMS to Icons.Filled.MoodBad,
                    CyclePhase.NONE to Icons.Filled.RemoveCircle
                )
                val phaseLabels = mapOf(
                    CyclePhase.MENSTRUATION to "Менструация",
                    CyclePhase.FOLLICULAR to "Фолликулярная",
                    CyclePhase.OVULATION to "Овуляция",
                    CyclePhase.LUTEAL to "Лютеиновая",
                    CyclePhase.PMS to "ПМС",
                    CyclePhase.NONE to "Нет"
                )
                CyclePhase.values().forEach { phase ->
                    FilterChip(
                        selected = selectedPhase == phase,
                        onClick = { selectedPhase = phase },
                        label = { Text(phaseLabels[phase] ?: phase.toString()) },
                        leadingIcon = { Icon(phaseIcons[phase] ?: Icons.Filled.RemoveCircle, contentDescription = phase.toString()) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Симптомы
            Text("Симптомы", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                symptomsList.forEach { (symptom, icon) ->
                    FilterChip(
                        selected = selectedSymptoms.contains(symptom),
                        onClick = {
                            selectedSymptoms = if (selectedSymptoms.contains(symptom))
                                selectedSymptoms - symptom else selectedSymptoms + symptom
                        },
                        label = { Text(symptom) },
                        leadingIcon = { Icon(icon, contentDescription = symptom) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Заметки
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Заметки") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Переключатель начала менструации
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isPeriodStart) Modifier.background(Color(0xFFFFCDD2), RoundedCornerShape(8.dp)) else Modifier),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Начало менструации")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isPeriodStart,
                    onCheckedChange = { isPeriodStart = it }
                )
                if (isPeriodStart) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Red)
                    )
                }
            }

            // Отображение выбранного настроения красиво
            if (selectedMood != null) {
                val moodIcons = mapOf(
                    MoodLevel.VERY_BAD to Icons.Filled.MoodBad,
                    MoodLevel.BAD to Icons.Filled.SentimentDissatisfied,
                    MoodLevel.NEUTRAL to Icons.Filled.Psychology,
                    MoodLevel.GOOD to Icons.Filled.SentimentSatisfied,
                    MoodLevel.VERY_GOOD to Icons.Filled.SentimentVerySatisfied,
                    MoodLevel.EXCELLENT to Icons.Filled.Star
                )
                val moodLabels = mapOf(
                    MoodLevel.VERY_BAD to "Очень плохо",
                    MoodLevel.BAD to "Плохо",
                    MoodLevel.NEUTRAL to "Нейтрально",
                    MoodLevel.GOOD to "Хорошо",
                    MoodLevel.VERY_GOOD to "Очень хорошо",
                    MoodLevel.EXCELLENT to "Отлично"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = moodIcons[selectedMood] ?: Icons.Filled.Psychology,
                        contentDescription = moodLabels[selectedMood] ?: selectedMood.toString(),
                        tint = when (selectedMood) {
                            MoodLevel.VERY_BAD -> Color(0xFFD32F2F)
                            MoodLevel.BAD -> Color(0xFFF57C00)
                            MoodLevel.NEUTRAL -> Color(0xFF757575)
                            MoodLevel.GOOD -> Color(0xFF388E3C)
                            MoodLevel.VERY_GOOD -> Color(0xFF1976D2)
                            MoodLevel.EXCELLENT -> Color(0xFFD81B60)
                            else -> Color.Unspecified
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = moodLabels[selectedMood] ?: selectedMood.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = when (selectedMood) {
                            MoodLevel.VERY_BAD -> Color(0xFFD32F2F)
                            MoodLevel.BAD -> Color(0xFFF57C00)
                            MoodLevel.NEUTRAL -> Color(0xFF757575)
                            MoodLevel.GOOD -> Color(0xFF388E3C)
                            MoodLevel.VERY_GOOD -> Color(0xFF1976D2)
                            MoodLevel.EXCELLENT -> Color(0xFFD81B60)
                            else -> Color.Unspecified
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isPeriodStart) {
                val periodLength = 5 // дней
                val cycleLength = 28 // дней
                val periodStart = entryDate.toLocalDate()
                val periodEnd = periodStart.plusDays((periodLength - 1).toLong())
                val nextPeriodStart = periodStart.plusDays(cycleLength.toLong())
                val nextPeriodEnd = nextPeriodStart.plusDays((periodLength - 1).toLong())
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color(0xFFF8BBD0), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text("Примерные дни менструации:", color = Color(0xFFD81B60))
                    Text(
                        "${periodStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} — ${periodEnd.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                        color = Color(0xFFD81B60),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Следующий цикл ожидается:", color = Color(0xFF7B1FA2))
                    Text(
                        "${nextPeriodStart.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} — ${nextPeriodEnd.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                        color = Color(0xFF7B1FA2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор даты и времени
            Text("Дата и время записи", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    entryDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Button(onClick = { showDatePicker = true }) {
                    Text("Изменить дату")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { showTimePicker = true }) {
                    Text("Изменить время")
                }
            }
            if (showDatePicker) {
                AlertDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = millis
                                entryDate = entryDate.withYear(cal.get(Calendar.YEAR))
                                    .withMonth(cal.get(Calendar.MONTH) + 1)
                                    .withDayOfMonth(cal.get(Calendar.DAY_OF_MONTH))
                            }
                            showDatePicker = false
                        }) { Text("ОК") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
                    },
                    title = { Text("Выберите дату") },
                    text = {
                        DatePicker(state = datePickerState)
                    }
                )
            }
            if (showTimePicker) {
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            entryDate = entryDate.withHour(tempHour).withMinute(tempMinute)
                            showTimePicker = false
                        }) { Text("ОК") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
                    },
                    title = { Text("Выберите время") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Часы: ")
                            Slider(
                                value = tempHour.toFloat(),
                                onValueChange = { tempHour = it.toInt() },
                                valueRange = 0f..23f,
                                steps = 23
                            )
                            Text(tempHour.toString().padStart(2, '0'))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Минуты: ")
                            Slider(
                                value = tempMinute.toFloat(),
                                onValueChange = { tempMinute = it.toInt() },
                                valueRange = 0f..59f,
                                steps = 59
                            )
                            Text(tempMinute.toString().padStart(2, '0'))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedMood != null && selectedPhase != null) {
                        val entry = MoodEntry(
                            id = entryToEdit?.id ?: 0,
                            date = entryDate,
                            moodLevel = selectedMood!!,
                            cyclePhase = selectedPhase!!,
                            note = note,
                            symptoms = selectedSymptoms.toList(),
                            isPeriodStart = isPeriodStart
                        )
                        onSaveEntry(entry)
                        onNavigateBack()
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