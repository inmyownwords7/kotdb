package com.db.kotlinapp.cvs

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.IOException

object ExcelWriter {
    fun writeToExcel(outputPath: String, extractedData: Map<String, MutableMap<String, Double>>) {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Extracted Data")

        try {
            // Define headers
            val headers = listOf("Date", "GROCERIES TX", "GROCERY NTX", "CIGARETTES", "ALCOHOL")

            // Create header row
            val headerRow: Row = sheet.createRow(0)
            for ((colIndex, header) in headers.withIndex()) {
                val cell = headerRow.createCell(colIndex)
                cell.setCellValue(header)
                cell.cellStyle = createHeaderStyle(workbook) // Bold formatting
            }

            GlobalLogger.info("Writing extracted data to Excel: $outputPath")

            // Populate rows with extracted data
            var rowIndex = 1
            for ((date, amounts) in extractedData) {
                val row = sheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(date) // First column: Date

                for ((colIndex, category) in headers.drop(1).withIndex()) {
                    val amount = amounts[category] ?: 0.0
                    row.createCell(colIndex + 1).setCellValue(amount)
                }
            }

            // Auto-size columns for better readability
            for (i in headers.indices) {
                sheet.autoSizeColumn(i)
            }

            // Save the Excel file
            FileOutputStream(outputPath).use { outputStream ->
                workbook.write(outputStream)
            }
            GlobalLogger.info("Excel file created successfully: $outputPath")

        } catch (e: IOException) {
            GlobalLogger.error("Failed to write Excel file: $outputPath", e)
        } finally {
            try {
                workbook.close()
            } catch (e: IOException) {
                GlobalLogger.error("Error closing Excel workbook", e)
            }
        }
    }

    // Create a bold style for headers
    private fun createHeaderStyle(workbook: Workbook): CellStyle {
        val headerFont: Font = workbook.createFont().apply {
            bold = true
            fontHeightInPoints = 12
        }

        return workbook.createCellStyle().apply {
            setFont(headerFont)
        }
    }
}
