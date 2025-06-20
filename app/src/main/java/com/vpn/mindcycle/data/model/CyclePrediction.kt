package com.vpn.mindcycle.data.model

import org.threeten.bp.LocalDateTime

data class CyclePrediction(
    val nextPeriodStart: LocalDateTime,
    val nextPeriodEnd: LocalDateTime,
    val nextOvulation: LocalDateTime,
    val averageCycleLength: Int
) 