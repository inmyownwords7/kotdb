package com.db.kotlinapp.service

import com.db.kotlinapp.dto.TransactionDTO
import com.db.kotlinapp.mapper.CsvTransactionMapper
import com.db.kotlinapp.utils.CsvUtils.extractDate
import com.db.kotlinapp.utils.CsvUtils.extractTransactions
import com.db.kotlinapp.utils.CsvUtils.parseTransactions
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.io.File

@Service
class CsvService(
    private val csvTransactionMapper: CsvTransactionMapper // ✅ Inject the correct Mapper
) {

    suspend fun processCSVFiles(directory: String): List<TransactionDTO> = coroutineScope {
        val csvFolder = File(directory)
        if (!csvFolder.exists() || !csvFolder.isDirectory) return@coroutineScope emptyList()

        val csvFiles = csvFolder.listFiles { _, name ->
            name.endsWith(".csv", ignoreCase = true) && name.startsWith("Z005", ignoreCase = true)
        } ?: emptyArray()

        csvFiles.map { file -> async(Dispatchers.IO) { processSingleCSV(file) } }
            .awaitAll()
            .flatten()
            .mapNotNull { csvTransactionMapper.mapCsvToDto(it) } // ✅ Uses the CSV-specific Mapper
    }

    private fun processSingleCSV(file: File): List<Map<String, Any>> {
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
}
