package com.db.kotlinapp.cvs

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.IOException
import java.util.*

object CsvExtractor {
    private val keywords = listOf("GROCERIES TX", "GROCERY NTX", "CIGARETTES", "ALCOHOL")

    fun extractData(filePath: String): TreeMap<String, MutableMap<String, Double>> {
        val extractedAmounts = TreeMap<String, MutableMap<String, Double>>() // Sorted by date
        val file = File(filePath)

        if (!file.exists()) {
            GlobalLogger.error("❌ File not found: $filePath")
            return extractedAmounts
        }

        try {
            file.reader().use { reader ->
                val lines = reader.readLines()

                // ✅ **Step 1: Confirm File Read Works**
                GlobalLogger.info("✅ CSV File Read Successfully. Total lines: ${lines.size}")

                if (lines.isEmpty()) {
                    GlobalLogger.error("❌ CSV file is empty or could not be read: $filePath")
                    return extractedAmounts
                }

                // ✅ **Step 2: Log First 10 Lines (Force Logging)**
                GlobalLogger.info("🔍 CSV First 10 Rows Preview:")
                lines.take(10).forEachIndexed { index, line ->
                    GlobalLogger.info("Row ${index + 1} Content: '$line'")
                }

                // ✅ **Step 3: Find the Header Row**
                val headerIndex = lines.indexOfFirst { it.contains("DATE", ignoreCase = true) }
                if (headerIndex == -1) {
                    GlobalLogger.error("❌ No valid headers found in file: $filePath")
                    return extractedAmounts
                }

                val validLines = lines.drop(headerIndex) // Start from detected headers
                if (validLines.size < 2) { // If no data after headers, it's invalid
                    GlobalLogger.warn("⚠️ No valid data rows found in file: $filePath")
                    return extractedAmounts
                }

                GlobalLogger.info("✅ Processing file: $filePath with ${validLines.size} lines")

                val csvParser = CSVParser(
                    validLines.joinToString("\n").reader(),
                    CSVFormat.DEFAULT.builder()
                        .setTrim(true)
                        .setIgnoreSurroundingSpaces(true)
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build()
                )

                // 📝 **Step 4: Extract & Log Headers**
                val headers = csvParser.headerNames.map { it.uppercase().trim() }
                GlobalLogger.debug("📝 Extracted Headers: $headers")
                GlobalLogger.debug("📌 Expected Headers: ${listOf("DATE") + keywords}")

                for ((rowIndex, record) in csvParser.withIndex()) {
                    val detectedColumns = record.toList()
                    val colCount = detectedColumns.size

                    // ✅ **Log the actual data before checking column count**
                    GlobalLogger.info("🔹 Row ${rowIndex + 1} Raw Data: $detectedColumns (Columns: $colCount)")

                    // 🚨 **Now log the exact issue before skipping**
                    if (colCount < 5) {
                        GlobalLogger.warn("⚠️ Skipping row ${rowIndex + 1}: Incorrect column count (expected at least 5, found $colCount).")
                        GlobalLogger.warn("   ⬆️ Row Content: $detectedColumns")
                        continue
                    }

                    val recordedDate = record.get(0).trim()
                    val categoryAmounts = mutableMapOf<String, Double>()

                    for (keyword in keywords) {
                        val colIndex = headers.indexOfFirst { it.equals(keyword, ignoreCase = true) }
                        if (colIndex != -1) {
                            val rawValue = record.get(colIndex)
                            val amount = rawValue.replace(",", "").toDoubleOrNull() ?: 0.0
                            categoryAmounts[keyword] = amount
                            GlobalLogger.debug("💰 Found $keyword: $$amount on $recordedDate (Raw: '$rawValue')")
                        } else {
                            GlobalLogger.warn("⚠️ Missing column for '$keyword' in row ${rowIndex + 1}")
                        }
                    }

                    extractedAmounts[recordedDate] = categoryAmounts
                }

                GlobalLogger.info("✅ Extraction completed for file: $filePath. Processed ${extractedAmounts.size} entries.")
            }
        } catch (e: IOException) {
            GlobalLogger.error("❌ Error reading CSV file: $filePath", e)
        }

        return extractedAmounts
    }
}
