package com.db.kotlinapp.utils

object CsvUtils {
    /**
     * Extracts the date from the metadata in the CSV file.
     */
    fun extractDate(lines: List<String>): String {
        val dateLine = lines.firstOrNull { it.startsWith("\"DATE") }
        return dateLine?.split(",")?.getOrNull(1)?.replace("\"", "")?.trim() ?: "Unknown Date"
    }

    /**
     * Extracts transaction records from the CSV file.
     */
    fun extractTransactions(lines: List<String>): List<String> {
        val startIndex = lines.indexOfFirst { it.contains("RECORD", ignoreCase = true) } + 1
        return if (startIndex > 0 && startIndex < lines.size) {
            lines.drop(startIndex)
        } else {
            emptyList()
        }
    }

    /**
     * Parses transaction records into a structured format.
     */
    fun parseTransactions(transactions: List<String>): MutableMap<String, Any> {
        val dataMap = mutableMapOf<String, Any>()

        for (line in transactions) {
            val fields = line.split(",").map { it.replace("\"", "").trim().uppercase() }
            if (fields.size < 4) continue // Skip malformed lines

            val descriptor = fields[1]
            val amount = fields[3].toDoubleOrNull() ?: 0.0

            when (descriptor) {
                "GROCERY NTX" -> dataMap["GROCERIES NTX"] = (dataMap["GROCERIES NTX"] as? Double ?: 0.0) + amount
                "GROCERIES TX" -> dataMap["GROCERIES TX"] = (dataMap["GROCERIES TX"] as? Double ?: 0.0) + amount
                "CIGARETTES" -> dataMap["CIGARETTES"] = (dataMap["CIGARETTES"] as? Double ?: 0.0) + amount
                "ALCOHOL" -> dataMap["ALCOHOL"] = (dataMap["ALCOHOL"] as? Double ?: 0.0) + amount
            }
        }

        return dataMap
    }
}
