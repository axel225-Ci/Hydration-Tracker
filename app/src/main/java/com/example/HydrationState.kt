package com.example

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HydrationEntry(
    val id: Long = System.currentTimeMillis(),
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}

data class HydrationUiState(
    val currentMl: Int = 0,
    val targetMl: Int = 2000,
    val history: List<HydrationEntry> = emptyList(),
    val streakDays: Int = 1
) {
    val progress: Float
        get() = if (targetMl > 0) (currentMl.toFloat() / targetMl.toFloat()).coerceIn(0f, 1.5f) else 0f

    val progressPercent: Int
        get() = if (targetMl > 0) ((currentMl.toFloat() / targetMl.toFloat()) * 100).toInt() else 0

    val remainingMl: Int
        get() = (targetMl - currentMl).coerceAtLeast(0)

    val currentInLiters: String
        get() = String.format(Locale.FRANCE, "%.2f", currentMl / 1000f)

    val targetInLiters: String
        get() = String.format(Locale.FRANCE, "%.1f", targetMl / 1000f)

    val isGoalReached: Boolean
        get() = currentMl >= targetMl

    val cupsConsumed: Int
        get() = currentMl / 250

    val totalCups: Int
        get() = targetMl / 250
}
