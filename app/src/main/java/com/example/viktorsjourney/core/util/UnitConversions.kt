package com.example.viktorsjourney.core.util

fun parseTimeToMinutes(timeString: String): Float {
    val parts = timeString.split(" ")
    if (parts.size != 2) return 0f // Handle invalid formats gracefully

    val value = parts[0].toFloatOrNull() ?: return 0f
    val unit = parts[1]

    return if (unit.startsWith("hr")) { // handles "hr" and "hrs"
        value * 60f
    } else { // Assumes "mins"
        value
    }
}