package com.db.kotlinapp.excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Scanner

fun main() {
    val csvDir = "src/main/resources/11/" // Change to your CSV directory
    val excelFilePath = "src/main/resources/sales_data.xlsx"

    // Ask user whether to overwrite or append
    println("🔄 Do you want to overwrite the existing Excel file? (yes/no)")
    val scanner = Scanner(System.`in`)
    val overwrite = scanner.nextLine().trim().lowercase() == "yes"

    // Step 1: Process CSV files
    val newRecords = processCSVFiles(csvDir)

    // Validation: Check if data was extracted
    if (newRecords.isEmpty()) {
        println("⚠️ Warning: No valid transactions extracted from any CSV files.")
        return
    }

    // Step 2: Append or overwrite extracted data in Excel
    appendRecordsToExcel(excelFilePath, newRecords, overwrite)

    println("✅ Processing completed. Check the updated Excel file at: $excelFilePath")
}

/**
 * Reads CSV files from the specified directory and extracts transaction data.
 */
fun processCSVFiles(directory: String): List<Map<String, Any>> {
    val csvFolder = File(directory)
    if (!csvFolder.exists() || !csvFolder.isDirectory) {
        println("❌ Error: CSV directory not found at $directory")
        return emptyList()
    }

    val records = mutableListOf<Map<String, Any>>()

    // Handle CSV file detection with different cases and spaces, and check if filename starts with "Z005"
    val csvFiles = csvFolder.listFiles { _, name ->
        name.trim().lowercase().endsWith(".csv") && name.trim().uppercase().startsWith("Z005")
    } ?: emptyArray()

    if (csvFiles.isEmpty()) {
        println("⚠️ Warning: No CSV files starting with 'Z005' found in directory $directory")
        return emptyList()
    }

    csvFiles.forEach { file ->
        println("📄 Processing file: ${file.name}")

        val lines = file.readLines()
        if (lines.isEmpty()) {
            println("⚠️ Warning: File ${file.name} is empty. Skipping.")
            return@forEach
        }

        // Extract the date from the file
        val date = lines.firstOrNull { it.startsWith("\"DATE") }
            ?.split(",")?.getOrNull(1)?.replace("\"", "")?.trim() ?: "Unknown Date"

        // Find where transactions start (first empty line)
        val startIndex = lines.indexOfFirst { it.trim().isEmpty() } + 1
        if (startIndex <= 0 || startIndex >= lines.size) {
            println("⚠️ Warning: No transactions found in ${file.name}. Skipping.")
            return@forEach
        }

        val transactions = lines.drop(startIndex)
        val dataMap = mutableMapOf<String, Any>("Date" to date)

        var transactionsFound = false

        for (line in transactions) {
            val fields = line.split(",").map { it.replace("\"", "").trim().uppercase() }
            if (fields.size < 4) continue

            val descriptor = fields[1].trim()
            val amount = fields[3].toDoubleOrNull() ?: 0.0

            // Skip "DEPT" transactions from debug output
            if (descriptor.startsWith("DEPT")) continue

            when {
                descriptor.contains("GROCERY NTX", ignoreCase = true) -> {
//                    println("✅ Matched: 'GROCERY NTX' → Storing as 'GROCERIES NTX' with amount: $amount")
                    dataMap["GROCERIES NTX"] = dataMap.getOrDefault("GROCERIES NTX", 0.0) as Double + amount
                    transactionsFound = true
                }
                descriptor.contains("GROCERIES TX", ignoreCase = true) -> {
//                    println("✅ Matched: 'GROCERIES TX' → Storing as is")
                    dataMap["GROCERIES TX"] = amount
                    transactionsFound = true
                }
                descriptor.contains("CIGARETTES", ignoreCase = true) -> {
//                    println("✅ Matched: 'CIGARETTES'")
                    dataMap["CIGARETTES"] = amount
                    transactionsFound = true
                }
                descriptor.contains("ALCOHOL", ignoreCase = true) -> {
//                    println("✅ Matched: 'ALCOHOL'")
                    dataMap["ALCOHOL"] = amount
                    transactionsFound = true
                }
            }
        }

        if (transactionsFound) {
            records.add(dataMap)
        } else {
            println("⚠️ Warning: No valid transactions found in ${file.name}. Skipping.")
        }
    }

    return records
}

/**
 * Appends or overwrites records in an existing Excel file.
 */
fun appendRecordsToExcel(filePath: String, newRecords: List<Map<String, Any>>, overwrite: Boolean) {
    val file = File(filePath)

    val workbook: XSSFWorkbook = if (overwrite || !file.exists()) {
        println("📝 Overwriting the Excel file...")
        XSSFWorkbook() // Create a new workbook if overwriting
    } else {
        println("📎 Appending to the existing Excel file...")
        FileInputStream(file).use { fis -> WorkbookFactory.create(fis) as XSSFWorkbook }
    }

    val sheet = workbook.getSheet("Sales Data") ?: workbook.createSheet("Sales Data")

    // Define headers (Ensuring proper column names)
    val headers = listOf("Date", "GROCERIES TX", "GROCERIES NTX", "CIGARETTES", "ALCOHOL")

    if (overwrite) {
        // Clear all existing rows if overwriting
        while (sheet.physicalNumberOfRows > 0) {
            sheet.removeRow(sheet.getRow(0))
        }
    }

    // If the sheet is empty or first row is missing headers, add headers
    if (sheet.physicalNumberOfRows == 0 || sheet.getRow(0)?.getCell(0)?.stringCellValue != "Date") {
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header -> headerRow.createCell(index).setCellValue(header) }
    }

    // Read existing dates to prevent duplicates when appending
    val existingDates = mutableSetOf<String>()
    if (!overwrite) {
        for (row in sheet) {
            row.getCell(0, org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ?.let { existingDates.add(it.stringCellValue) }
        }
    }

    var lastRowIndex = if (overwrite) 1 else sheet.lastRowNum + 1

    // Append only new records
    for (record in newRecords) {
        val date = record["Date"] as String
//        println("📅 Writing to Excel: Date=$date, GROCERIES NTX=${record["GROCERIES NTX"]}")

        if (!existingDates.contains(date)) {
            val row = sheet.createRow(lastRowIndex++)
            headers.forEachIndexed { index, header ->
                val cell = row.createCell(index)
                when (val value = record[header]) {
                    is String -> cell.setCellValue(value)
                    is Number -> cell.setCellValue(value.toDouble())
                    else -> cell.setCellValue("")
                }
            }
        }
    }

    FileOutputStream(file).use { fos -> workbook.write(fos) }
    workbook.close()

    println("✅ Excel file updated successfully: $filePath")
}
