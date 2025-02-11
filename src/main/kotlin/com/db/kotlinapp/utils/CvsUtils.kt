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
        val startIndex = lines.indexOfFirst { it.trim().isEmpty() } + 1
        return if (startIndex > 0 && startIndex < lines.size) lines.drop(startIndex) else emptyList()
    }

    /**
     * Parses transaction records and filters valid categories.
     */
    fun parseTransactions(transactions: List<String>): Map<String, Double> {
        val validCategories = setOf("GROCERY NTX", "GROCERIES TX", "ALCOHOL", "CIGARETTES")
        val dataMap = mutableMapOf<String, Double>()

        for (line in transactions) {
            val fields = line.split(",").map { it.replace("\"", "").trim().uppercase() }

            if (fields.size < 4) continue  // Skip malformed lines

            val descriptor = fields[1]
            val amount = fields[3].toDoubleOrNull() ?: 0.0 // Convert to Double

            for (category in validCategories) {
                if (descriptor.contains(category, ignoreCase = true)) {
                    dataMap[category] = dataMap.getOrDefault(category, 0.0) + amount
                }
            }
        }

        return dataMap
    }
}
