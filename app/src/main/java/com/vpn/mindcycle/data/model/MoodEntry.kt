package com.vpn.mindcycle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

enum class MoodLevel {
    VERY_BAD,
    BAD,
    NEUTRAL,
    GOOD,
    EXCELLENT
}

enum class CyclePhase {
    MENSTRUATION,
    FOLLICULAR,
    OVULATION,
    LUTEAL,
    PMS,
    NONE
}

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val moodLevel: MoodLevel,
    val cyclePhase: CyclePhase = CyclePhase.NONE,
    val symptoms: List<String> = emptyList(),
    val note: String = ""
) 