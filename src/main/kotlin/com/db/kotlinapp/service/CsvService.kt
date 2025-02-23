package com.db.kotlinapp.service
import com.db.kotlinapp.dto.CvDTO
import com.db.kotlinapp.utils.CsvMapperUtil
import com.db.kotlinapp.utils.CsvUtils.extractDate
import com.db.kotlinapp.utils.CsvUtils.extractTransactions
import com.db.kotlinapp.utils.CsvUtils.parseTransactions
import kotlinx.coroutines.*
import org.springframework.stereotype.Service
import java.io.File

@Service
class CsvService {

    suspend fun processCSVFiles(directory: String): List<CvDTO> = coroutineScope {
        val csvFolder = File(directory)
        if (!csvFolder.exists() || !csvFolder.isDirectory) return@coroutineScope emptyList()

        val csvFiles = csvFolder.listFiles { _, name ->
            name.endsWith(".csv", ignoreCase = true) && name.startsWith("Z005", ignoreCase = true)
        } ?: emptyArray()

        val extractedData = csvFiles.map { file ->
            async(Dispatchers.IO) { processSingleCSV(file) } // ✅ Calls `processSingleCSV(file)`, not `processCSVFiles(file)`
        }.awaitAll()
            .flatten()
            .filter { it.isNotEmpty() } // ✅ Remove empty results
            .map { CsvMapperUtil.mapCsvToDto(it) } // ✅ Convert to CvDTO

        extractedData // ✅ Ensure List<CvDTO> is returned
    }

    private fun processSingleCSV(file: File): List<Map<String, Any>> {
        val records = mutableListOf<Map<String, Any>>() // ✅ Ensure valid return type
        println("📄 Processing file: ${file.name}")

        val lines = file.readLines()
        if (lines.isEmpty()) return records // ✅ Avoid null reference issues

        val transactions = extractTransactions(lines)
        val dataMap = parseTransactions(transactions)

        if (dataMap.isNotEmpty()) {
            dataMap["Date"] = extractDate(lines)
            records.add(dataMap)
        }

        return records // ✅ Now always returns a valid list
    }
}
