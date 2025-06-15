package com.vpn.mindcycle.data.db

import androidx.room.*
import com.vpn.mindcycle.data.model.MoodEntry
import com.vpn.mindcycle.data.model.MoodLevel
import com.vpn.mindcycle.data.model.CyclePhase
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): MoodEntry?

    @Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: MoodEntry): Long

    @Delete
    suspend fun deleteEntry(entry: MoodEntry): Int

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllEntries(): Int
}

@Database(entities = [MoodEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodEntryDao(): MoodEntryDao
}

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate = LocalDate.parse(dateString)

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toStringList(value: String): List<String> = 
        if (value.isEmpty()) emptyList() else value.split(",")

    @TypeConverter
    fun fromMoodLevel(value: MoodLevel): String = value.name

    @TypeConverter
    fun toMoodLevel(value: String): MoodLevel = MoodLevel.valueOf(value)

    @TypeConverter
    fun fromCyclePhase(value: CyclePhase): String = value.name

    @TypeConverter
    fun toCyclePhase(value: String): CyclePhase = CyclePhase.valueOf(value)
} 