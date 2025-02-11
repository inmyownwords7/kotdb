//package com.db.kotlinapp.cvs
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import java.io.FileOutputStream
//
//// Define the Headers data class
//data class Headers(
//    val date: String = "",
//    val h1: String = "GROCERIES TX",
//    val h2: String = "GROCERIES NTX",
//    val h3: String = "CIGARETTES",
//    val h4: String = "ALCOHOL"
//)
//
//fun createExcelWithHeaders(filePath: String) {
//    val workbook = XSSFWorkbook()
//    val sheet = workbook.createSheet("Sales Data")
//
//    // Create a header row
//    val headerRow = sheet.createRow(0)
//
//    // Add "Date" as the first column
//    headerRow.createCell(0).setCellValue("Date")
//
//    // Get values of h1 to h4 dynamically
//    val headers = Headers() // Create an instance to access property values
//    val columnNames = listOf(headers.h1, headers.h2, headers.h3, headers.h4) // Extract values
//
//    // Add extracted column names to the sheet
//    columnNames.forEachIndexed { index, value ->
//        headerRow.createCell(index + 1).setCellValue(value) // Start from second column (index + 1)
//    }
//
//    // Auto-size columns for better readability
//    (0..columnNames.size).forEach { sheet.autoSizeColumn(it) }
//
//    // Save the Excel file
//    FileOutputStream(filePath).use { fos ->
//        workbook.write(fos)
//    }
//    workbook.close()
//
//    println("Excel file created successfully with 'Date' and correct column names!")
//}
//
//fun main() {
//    val filePath = "sales_data.xlsx"
//    createExcelWithHeaders(filePath)
//}
