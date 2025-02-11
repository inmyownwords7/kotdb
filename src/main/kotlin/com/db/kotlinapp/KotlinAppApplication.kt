package com.db.kotlinapp

import com.db.kotlinapp.cvs.CsvExtractor
import com.db.kotlinapp.cvs.ExcelWriter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinAppApplication

fun main(args: Array<String>) {
    val inputCsvPath = "src/main/resources/Z005_30.CSV"
    val outputExcelPath = "src/main/resources/ExtractedData.xlsx"

    // Extract data from CSV
    val extractedData = CsvExtractor.extractData(inputCsvPath)

    // Debugging: Print extracted data before writing to Excel
    println("Extracted Data Preview:")
    extractedData.forEach { (date, amounts) ->
        println("Date: $date | Data: $amounts")
    }

    // Check if any data was extracted
    if (extractedData.isEmpty()) {
        println("No data found. Exiting...")
        return
    }

    // Write to Excel file
    ExcelWriter.writeToExcel(outputExcelPath, extractedData)
    println("Excel file successfully created at: $outputExcelPath")

    runApplication<KotlinAppApplication>(*args)
}
