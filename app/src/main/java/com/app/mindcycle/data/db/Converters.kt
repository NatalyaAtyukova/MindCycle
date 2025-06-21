package com.app.mindcycle.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.app.mindcycle.data.model.CyclePhase
import com.app.mindcycle.data.model.MoodLevel
import java.time.LocalDate
import java.time.ZoneOffset
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromMoodLevel(moodLevel: MoodLevel): Int {
        return moodLevel.value
    }

    @TypeConverter
    fun toMoodLevel(value: Int): MoodLevel {
        return MoodLevel.fromValue(value)
    }

    @TypeConverter
    fun fromCyclePhase(value: CyclePhase): String {
        return value.name
    }

    @TypeConverter
    fun toCyclePhase(value: String): CyclePhase {
        return CyclePhase.fromString(value)
    }
} 