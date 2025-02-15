package com.db.kotlinapp.executor

import com.db.kotlinapp.service.CsvService
import com.db.kotlinapp.service.TransactionService
import com.db.kotlinapp.service.ExcelService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The main execution class that orchestrates CSV processing, database saving, and Excel exporting.
 */
suspend fun executeProcess(csvService: CsvService, transactionService: TransactionService, excelService: ExcelService): Boolean {
    val month = "November"
    val csvDir = System.getenv("CSV_DIRECTORY") ?: "src/main/resources/11/" // ✅ Configurable path
    val excelFilePath = "src/main/resources/${month}_sales_data.xlsx"

    println("📌 Starting process...")

    return withContext(Dispatchers.IO) { // ✅ Ensures database and file operations run in IO thread
        try {
            // ✅ Step 1: Extract transactions from CSV files
            val transactions = csvService.processCSVFiles(csvDir)
            if (transactions.isEmpty()) {
                println("⚠️ No valid transactions extracted from CSV files. Process aborted.")
                return@withContext false
            }
            println("✅ CSV processing completed. ${transactions.size} transactions found.")

            // ✅ Step 2: Save transactions to the database
            transactionService.saveTransactions(transactions)
            println("✅ Transactions successfully saved to the database.")

            // ✅ Step 3: Retrieve saved transactions from DB
            val savedTransactions = transactionService.getAllTransactions()

            // ✅ Step 4: Export transactions to Excel
            // ✅ Ensure `success` is properly assigned before using it
            val success = excelService.exportTransactionsToExcel(excelFilePath, savedTransactions)

            if (!success) { // ✅ Check `success` correctly
                println("⚠️ Excel export failed. Process incomplete.")
                return@withContext false // ✅ Properly return inside coroutine
            }


            println("✅ Processing completed! Check the updated Excel file: $excelFilePath")
            true // ✅ Process completed successfully
        } catch (e: Exception) {
            println("❌ An error occurred: ${e.message}")
            false // ✅ Process failed
        }
    }
}
