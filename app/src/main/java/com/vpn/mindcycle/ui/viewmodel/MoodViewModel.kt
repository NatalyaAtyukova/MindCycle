package com.vpn.mindcycle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vpn.mindcycle.data.db.MoodEntryDao
import com.vpn.mindcycle.data.model.CyclePrediction
import com.vpn.mindcycle.data.model.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit

class MoodViewModel(private val moodEntryDao: MoodEntryDao) : ViewModel() {

    private val _entries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val entries: StateFlow<List<MoodEntry>> = _entries.asStateFlow()

    private val _cyclePrediction = MutableStateFlow<CyclePrediction?>(null)
    val cyclePrediction: StateFlow<CyclePrediction?> = _cyclePrediction.asStateFlow()

    init {
        loadEntries()
        predictNextCycle()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val endDate = LocalDateTime.now()
            val startDate = endDate.minus(3, ChronoUnit.MONTHS)
            _entries.value = moodEntryDao.getEntriesBetweenDates(startDate, endDate)
        }
    }

    private fun predictNextCycle() {
        viewModelScope.launch {
            // _cyclePrediction.value = moodEntryDao.predictNextCycle()
        }
    }

    fun addEntry(entry: MoodEntry) {
        viewModelScope.launch {
            moodEntryDao.insertEntry(entry)
            _entries.value = _entries.value + entry
            predictNextCycle()
        }
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch {
            moodEntryDao.deleteEntry(entry)
            _entries.value = _entries.value - entry
            predictNextCycle()
        }
    }

    fun getEntryForDate(date: LocalDateTime): MoodEntry? {
        return entries.value.find { it.date == date }
    }

    class Factory(private val moodEntryDao: MoodEntryDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
                return MoodViewModel(moodEntryDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 