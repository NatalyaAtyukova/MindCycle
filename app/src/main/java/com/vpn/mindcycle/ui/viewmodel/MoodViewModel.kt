package com.vpn.mindcycle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vpn.mindcycle.data.db.MoodEntryDao
import com.vpn.mindcycle.data.model.MoodEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class MoodViewModel(
    private val moodEntryDao: MoodEntryDao
) : ViewModel() {

    private val _entries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val entries: StateFlow<List<MoodEntry>> = _entries.asStateFlow()

    init {
        loadCurrentMonthEntries()
    }

    fun loadCurrentMonthEntries() {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        viewModelScope.launch {
            moodEntryDao.getEntriesBetweenDates(startOfMonth, endOfMonth)
                .collect { entries ->
                    _entries.value = entries
                }
        }
    }

    fun addEntry(entry: MoodEntry) {
        viewModelScope.launch {
            moodEntryDao.insertEntry(entry)
        }
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch {
            moodEntryDao.deleteEntry(entry)
        }
    }

    fun getEntryForDate(date: LocalDate): MoodEntry? {
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