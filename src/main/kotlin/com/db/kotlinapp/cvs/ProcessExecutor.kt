package com.db.kotlinapp.cvs

import com.db.kotlinapp.excel.appendRecordsToExcel
import com.db.kotlinapp.utils.*
import java.io.File
import java.util.Scanner

suspend fun executeProcess() {
    val month = "November"
    val csvDir = "src/main/resources/11/"
    val excelFilePath = "src/main/resources/${month}_sales_data.xlsx"
    val file = File(excelFilePath)

    val overwrite = checkFileOverwrite(file)

    val newRecords = processCSVFiles(csvDir) // ✅ Calls `processCSVFiles()`, which is suspend

    if (newRecords.isEmpty()) {
        logWarn("⚠️ Warning: No valid transactions extracted from any CSV files.")
        return
    }

    appendRecordsToExcel(excelFilePath, newRecords, overwrite)

    logInfo("✅ Processing completed. Check the updated Excel file at: $excelFilePath")
}

/**
 * Checks if a file exists and asks for overwrite confirmation.
 */
fun checkFileOverwrite(file: File): Boolean {
    if (file.exists()) {
        logWarn("⚠️ The file '${file.path}' already exists. Overwrite? (yes/no)")
        val userInput = Scanner(System.`in`).nextLine().trim().lowercase()

        return when (userInput) {
            "yes" -> true
            "no" -> {
                logInfo("❌ Operation canceled. The existing Excel file was not modified.")
                false
            }
            else -> {
                logWarn("⚠️ Invalid input. Defaulting to 'no'.")
                false
            }
        }
    }
    logInfo("ℹ️ No existing Excel file found. A new file will be created.")
    return true
}
