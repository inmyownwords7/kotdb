package com.db.kotlinapp.executor

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.mapper.TransactionMapper
import com.db.kotlinapp.service.CsvService
import com.db.kotlinapp.service.TransactionService
import com.db.kotlinapp.service.ExcelService
import com.db.kotlinapp.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The main execution class that orchestrates CSV processing, database saving, and Excel exporting.
 */
suspend fun executeProcess(
    csvService: CsvService,
    transactionService: TransactionService,
    excelService: ExcelService, userService: UserService, username: String,
    transactionMapper: TransactionMapper // Assuming TransactionMapper is defined elsewhere
): Boolean {
    val month = "November"
    val csvDir = System.getenv("CSV_DIRECTORY") ?: "src/main/resources/11/" // ✅ Configurable path
    val excelFilePath = "src/main/resources/${month}_sales_data.xlsx"

    println("📌 Starting process...")

    return withContext(Dispatchers.IO) { // ✅ Ensures database and file operations run in IO thread
        try {
            // ✅ Step 1: Extract transactions from CSV files
            val user: UserDTO = withContext(Dispatchers.IO) { userService.getUserByUsername(username) }
                ?: throw IllegalArgumentException("❌ User '$username' not found!")

            val userId = user.id ?: throw IllegalStateException("❌ User ID is null!")

            val csvDtoList = csvService.processCSVFiles(csvDir).map {dto -> dto.copy()}

            if (csvDtoList.isEmpty()) {
                println("⚠️ No valid transactions extracted from CSV files. Process aborted.")
                return@withContext false
            }
            println("✅ CSV processing completed. ${csvDtoList.size} transactions found.")


            // ✅ Step 3: Convert `CvDTO` → `TransactionDTO`
            val transactionDtoList = csvDtoList.map { transactionMapper.toTransactionDTO(it, userId) }


            withContext(Dispatchers.IO) {
                transactionService.saveTransactions(transactionDtoList)
            }
            // ✅ Step 5: Retrieve saved transactions from DB
            val savedTransactions = withContext(Dispatchers.IO) {
                transactionService.getAllTransactions()
            }

            // ✅ Step 6: Export transactions to Excel
            val success = excelService.exportTransactionsToExcel(excelFilePath, savedTransactions)

            if (!success) {
                println("⚠️ Excel export failed. Process incomplete.")
                return@withContext false
            }

            println("✅ Processing completed! Check the updated Excel file: $excelFilePath")
            true // ✅ Process completed successfully
        } catch (e: Exception) {
            println("❌ An error occurred: ${e.message}")
            false // ✅ Process failed
        }
    }
}
