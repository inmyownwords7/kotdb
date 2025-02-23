package com.db.kotlinapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // ✅ Centralized Format

    fun parseDate(date: Any?): LocalDate? {
        return try {
            date?.toString()?.let { LocalDate.parse(it, formatter) }
        } catch (e: Exception) {
            null // ✅ Safe fallback if parsing fails
        }
    }
}
