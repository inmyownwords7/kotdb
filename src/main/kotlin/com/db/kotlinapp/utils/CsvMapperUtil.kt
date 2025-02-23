package com.db.kotlinapp.utils

import com.db.kotlinapp.dto.CvDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CsvMapperUtil {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun mapCsvToDto(data: Map<String, Any>): CvDTO {
        return CvDTO(
            date = parseDate(data["Date"]),
            groceriesTx = parseDouble(data["GROCERIES_TX"]),
            groceriesNtx = parseDouble(data["GROCERIES_NTX"]),
            cigarettes = parseDouble(data["CIGARETTES"]),
            alcohol = parseDouble(data["ALCOHOL"])
        )
    }

    private fun parseDate(value: Any?): LocalDate {
        return try {
            value?.toString()?.let { LocalDate.parse(it, dateFormatter) } ?: LocalDate.now()
        } catch (e: Exception) {
            LocalDate.now() // ✅ Default to today's date if parsing fails
        }
    }

    private fun parseDouble(value: Any?): Double {
        return value?.toString()?.toDoubleOrNull() ?: 0.0 // ✅ Default to 0.0 if null or invalid
    }
}