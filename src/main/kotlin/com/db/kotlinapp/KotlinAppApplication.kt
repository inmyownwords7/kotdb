package com.db.kotlinapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Scanner
@SpringBootApplication
class KotlinAppApplication

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

fun main(args: Array<String>) {
    runApplication<KotlinAppApplication>(*args)
    val month = "November"
    val csvDir = "src/main/resources/11/" // Change this to your actual CSV directory
    val excelFilePath = "src/main/resources/${month}_sales_data.xlsx"
    val file = File(excelFilePath)

    // ✅ Check if the output file exists
    var overwrite = true

    if (file.exists()) {
        println("⚠️ The file '$excelFilePath' already exists. Do you want to overwrite it? (yes/no)")
        val scanner = Scanner(System.`in`)
        val userInput = scanner.nextLine().trim().lowercase()

        if (userInput == "no") {
            println("❌ Operation canceled. The existing Excel file was not modified.")
            return // 🚨 Stop execution immediately
        } else if (userInput == "yes") {
            overwrite = true
        } else {
            println("⚠️ Invalid input. Defaulting to 'no'.")
            return
        }
    } else {
        println("ℹ️ No existing Excel file found. A new file will be created.")
    }

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

        // ✅ STEP 1: Find the header row before reading transactions
        val startIndex = lines.indexOfFirst { it.contains("RECORD", ignoreCase = true) } + 1
        if (startIndex <= 0 || startIndex >= lines.size) {
            println("⚠️ Warning: No transactions found in ${file.name}. Skipping.")
            return@forEach
        }

        val transactions = lines.drop(startIndex)
        val dataMap = mutableMapOf<String, Any>()

        var transactionsFound = false

        for (line in transactions) {
            val fields = line.split(",").map { it.replace("\"", "").trim().uppercase() }

            if (fields.size < 4) continue  // Ensure there are enough fields

            val descriptor = fields[1].trim()
            val rawAmount = fields[3].trim()
            val amount = rawAmount.toDoubleOrNull()

            if (amount == null) {
                println("⚠️ ALERT: Invalid amount '$rawAmount' found in file '${file.name}' (setting to 0.0)")
            }

            val finalAmount = amount ?: 0.0

            when (descriptor) {
                "GROCERY NTX" -> {
                    dataMap["GROCERIES NTX"] = (dataMap["GROCERIES NTX"] as? Double ?: 0.0) + finalAmount
                    transactionsFound = true
                }

                "GROCERIES TX" -> {
                    dataMap["GROCERIES TX"] = finalAmount
                    transactionsFound = true
                }

                "CIGARETTES" -> {
                    dataMap["CIGARETTES"] = finalAmount
                    transactionsFound = true
                }

                "ALCOHOL" -> {
                    dataMap["ALCOHOL"] = finalAmount
                    transactionsFound = true
                }
            }
        }

        if (transactionsFound) {
            dataMap["Date"] = lines.firstOrNull { it.contains("DATE", ignoreCase = true) }
                ?.split(",")?.getOrNull(1)?.replace("\"", "")?.trim() ?: "Unknown Date"
            records.add(dataMap)
        } else {
            println("⚠️ Warning: No valid transactions found in ${file.name}. Skipping.")
        }
    }
    return records
}

