package com.db.kotlinapp

import com.db.kotlinapp.cvs.CsvExtractor
import com.db.kotlinapp.cvs.GlobalLogger
import java.nio.file.Paths

fun main() {
    val filePath = Paths.get("src/main/resources/Z005_30.CSV").toFile().absolutePath

    // ✅ Extract data from CSV file
    val extractedData = CsvExtractor.extractData(filePath)

    if (extractedData.isEmpty()) {
        GlobalLogger.warn("⚠️ No valid data extracted from CSV.")
        return
    }

    // ✅ Log extracted data
    GlobalLogger.info("📊 Extracted Data Preview:")
    extractedData.forEach { (date, amounts) ->
        GlobalLogger.debug("📅 Date: $date | Data: $amounts")
    }

    // ✅ Log formatted output
    logFormattedTable(extractedData)

    GlobalLogger.info("✅ CSV processing completed successfully!")
}

/**
 * Logs extracted data in a formatted table output.
 */
fun logFormattedTable(data: Map<String, Map<String, Double>>) {
    val headers = listOf("Date", "GROCERIES TX", "GROCERY NTX", "CIGARETTES", "ALCOHOL")

    GlobalLogger.info("📌 Filtered Data Output:")
    GlobalLogger.info("| %-10s | %12s | %12s | %10s | %7s |".format(*headers.toTypedArray()))
    GlobalLogger.info("|------------|-------------|-------------|------------|---------|")

    data.forEach { (date, amounts) ->
        GlobalLogger.info(
            "| %-10s | %12.2f | %12.2f | %10.2f | %7.2f |".format(
                date,
                amounts["GROCERIES TX"] ?: 0.0,
                amounts["GROCERY NTX"] ?: 0.0,
                amounts["CIGARETTES"] ?: 0.0,
                amounts["ALCOHOL"] ?: 0.0
            )
        )
    }
}
