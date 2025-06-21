package com.app.mindcycle.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Создаем временную таблицу с новой структурой
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS mood_entries_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                timestamp TEXT NOT NULL,
                moodLevel TEXT NOT NULL,
                cyclePhase TEXT NOT NULL,
                symptoms TEXT NOT NULL,
                notes TEXT,
                isPeriodStart INTEGER NOT NULL DEFAULT 0,
                cycleLength INTEGER
            )
        """)

        // Проверяем существование колонки date
        val cursor = database.query("PRAGMA table_info(mood_entries)")
        val hasDateColumn = cursor.use {
            while (it.moveToNext()) {
                val columnName = it.getString(it.getColumnIndexOrThrow("name"))
                if (columnName == "date") return@use true
            }
            false
        }

        if (hasDateColumn) {
            // Копируем данные из старой таблицы в новую
            database.execSQL("""
                INSERT INTO mood_entries_temp (id, timestamp, moodLevel, cyclePhase, symptoms, notes, isPeriodStart)
                SELECT id, date, moodLevel, cyclePhase, symptoms, note, isPeriodStart
                FROM mood_entries
            """)
        }

        // Удаляем старую таблицу
        database.execSQL("DROP TABLE IF EXISTS mood_entries")

        // Переименовываем временную таблицу
        database.execSQL("ALTER TABLE mood_entries_temp RENAME TO mood_entries")
    }
} 