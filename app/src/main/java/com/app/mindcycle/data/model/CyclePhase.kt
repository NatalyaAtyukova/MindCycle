package com.app.mindcycle.data.model

enum class CyclePhase(val displayName: String) {
    MENSTRUATION("Menstruation"),
    FOLLICULAR("Follicular"),
    OVULATION("Ovulation"),
    LUTEAL("Luteal"),
    PMS("PMS"),
    NONE("None");

    companion object {
        fun fromString(value: String): CyclePhase {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: LUTEAL
        }
    }
} 