package com.vpn.mindcycle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vpn.mindcycle.data.db.Converters
import org.threeten.bp.LocalDateTime

@Entity(tableName = "mood_entries")
@TypeConverters(Converters::class)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDateTime,
    val moodLevel: MoodLevel,
    val cyclePhase: CyclePhase,
    val notes: String? = null,
    val symptoms: List<String> = emptyList(),
    val isPeriodStart: Boolean = false
) {
    init {
        require(date <= LocalDateTime.now()) { "Date cannot be in the future" }
    }

    companion object {
        fun create(
            date: LocalDateTime = LocalDateTime.now(),
            moodLevel: MoodLevel,
            cyclePhase: CyclePhase,
            notes: String? = null,
            symptoms: List<String> = emptyList(),
            isPeriodStart: Boolean = false
        ): MoodEntry {
            return MoodEntry(
                date = date,
                moodLevel = moodLevel,
                cyclePhase = cyclePhase,
                notes = notes,
                symptoms = symptoms,
                isPeriodStart = isPeriodStart
            )
        }
    }
} 