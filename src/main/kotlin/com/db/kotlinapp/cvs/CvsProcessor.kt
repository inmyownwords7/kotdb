package com.db.kotlinapp.cvs

import com.db.kotlinapp.utils.CsvUtils.extractDate
import com.db.kotlinapp.utils.CsvUtils.extractTransactions
import com.db.kotlinapp.utils.CsvUtils.parseTransactions
import kotlinx.coroutines.*
import java.io.File

/**
 * Reads CSV files and extracts transaction data.
 */
suspend fun processCSVFiles(directory: String): List<Map<String, Any>> = coroutineScope {
    val csvFolder = File(directory)
    if (!csvFolder.exists() || !csvFolder.isDirectory) return@coroutineScope emptyList()

    val csvFiles = csvFolder.listFiles { _, name ->
        name.endsWith(".csv", ignoreCase = true) && name.startsWith("Z005", ignoreCase = true)
    } ?: emptyArray()

    csvFiles.map { file -> async(Dispatchers.IO) { processSingleCSV(file) } }
        .awaitAll()
        .flatten()
}

/**
 * Parses a single CSV file and extracts transaction data.
 */
fun processSingleCSV(file: File): List<Map<String, Any>> {
    val records = mutableListOf<Map<String, Any>>()
    println("📄 Processing file: ${file.name}")

    val lines = file.readLines()
    if (lines.isEmpty()) return records

    val transactions = extractTransactions(lines)
    val dataMap = parseTransactions(transactions)

    if (dataMap.isNotEmpty()) {
        dataMap["Date"] = extractDate(lines)
        records.add(dataMap)
    }

    return records
}
