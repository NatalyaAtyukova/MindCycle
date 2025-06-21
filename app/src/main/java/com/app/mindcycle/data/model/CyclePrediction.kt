package com.app.mindcycle.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

data class CyclePrediction(
    val nextPeriodStart: LocalDateTime,
    val nextPeriodEnd: LocalDateTime,
    val nextOvulation: LocalDateTime,
    val averageCycleLength: Int
) 