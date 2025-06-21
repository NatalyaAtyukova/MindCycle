package com.app.mindcycle.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.mindcycle.data.model.CyclePhase
import com.app.mindcycle.data.model.CyclePrediction
import com.app.mindcycle.data.model.MoodEntry
import com.app.mindcycle.data.model.MoodLevel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.threeten.bp.temporal.ChronoUnit

@androidx.room.Dao
interface MoodEntryDao {
    @androidx.room.Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getEntriesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): List<MoodEntry>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: MoodEntry)

    @androidx.room.Delete
    suspend fun deleteEntry(entry: MoodEntry)

    @androidx.room.Query("SELECT * FROM mood_entries WHERE isPeriodStart = 1 ORDER BY date DESC")
    suspend fun getPeriodStarts(): List<MoodEntry>

    @androidx.room.Query("SELECT * FROM mood_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLastEntry(): MoodEntry?

    /*
    suspend fun predictNextCycle(): CyclePrediction? {
        try {
            val entries = getEntriesBetweenDates(
                LocalDateTime.now().minus(6, ChronoUnit.MONTHS),
                LocalDateTime.now()
            )

            if (entries.size < 2) {
                return null
            }

            // Calculate average cycle length
            val cycleLengths = mutableListOf<Long>()
            var lastEntryDate: LocalDateTime? = null

            for (entry in entries.sortedBy { it.date }) {
                if (lastEntryDate != null) {
                    val daysBetween = ChronoUnit.DAYS.between(lastEntryDate, entry.date)
                    if (daysBetween in 20..40) { // Reasonable cycle length range
                        cycleLengths.add(daysBetween)
                    }
                }
                lastEntryDate = entry.date
            }

            if (cycleLengths.isEmpty()) {
                return null
            }

            val averageCycleLength = cycleLengths.average()
            val lastEntry = entries.maxByOrNull { it.date }
            
            return lastEntry?.let {
                val nextPeriodStart = it.date.plus(averageCycleLength.toLong(), ChronoUnit.DAYS)
                CyclePrediction(
                    nextPeriodStart = nextPeriodStart,
                    nextPeriodEnd = nextPeriodStart.plusDays(5),
                    nextOvulation = nextPeriodStart.minusDays(14),
                    averageCycleLength = averageCycleLength.toInt()
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to predict next cycle: ${e.message}")
        }
    }

    private fun calculateConfidence(cycleLengths: List<Long>): Double {
        if (cycleLengths.size < 2) return 0.5

        val average = cycleLengths.average()
        val standardDeviation = cycleLengths.map { (it - average) * (it - average) }.average().let { Math.sqrt(it) }
        
        // Higher confidence for more consistent cycles
        return 1.0 - (standardDeviation / average).coerceIn(0.0, 1.0)
    }
    */
}

@Database(
    entities = [MoodEntry::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao

    companion object {
        @Volatile
        private var INSTANCE: MoodDatabase? = null

        fun buildDatabase(context: Context): MoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodDatabase::class.java,
                    "mind_cycle_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 