package com.app.mindcycle.data.model

enum class MoodLevel(val value: Int, val displayName: String) {
    VERY_BAD(1, "Very Bad"),
    BAD(2, "Bad"),
    NEUTRAL(3, "Neutral"),
    GOOD(4, "Good"),
    VERY_GOOD(5, "Very Good"),
    EXCELLENT(6, "Excellent");

    companion object {
        fun fromValue(value: Int): MoodLevel {
            return entries.find { it.value == value } ?: NEUTRAL
        }
    }
} 