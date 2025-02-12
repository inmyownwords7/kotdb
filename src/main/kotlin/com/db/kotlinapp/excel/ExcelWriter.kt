package com.db.kotlinapp.excel

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Appends or overwrites records in an existing Excel file.
 */
fun appendRecordsToExcel(filePath: String, newRecords: List<Map<String, Any>>, overwrite: Boolean) {
    val file = File(filePath)

    val workbook: XSSFWorkbook = if (overwrite || !file.exists()) {
        XSSFWorkbook()
    } else {
        FileInputStream(file).use { fis -> WorkbookFactory.create(fis) as XSSFWorkbook }
    }

    val sheet = workbook.getSheet("Sales Data") ?: workbook.createSheet("Sales Data")

    val headers = listOf("Date", "GROCERIES TX", "GROCERIES NTX", "CIGARETTES", "ALCOHOL")

    if (overwrite) {
        val oldIndex = workbook.getSheetIndex(sheet)
        if (oldIndex != -1) workbook.removeSheetAt(oldIndex)
        workbook.createSheet("Sales Data")
    }

    val existingDates = sheet.mapNotNull { row -> row.getCell(0)?.stringCellValue }.toMutableSet()

    var lastRowIndex = if (overwrite) 1 else sheet.lastRowNum + 1

    for (record in newRecords) {
        val date = record["Date"] as String
        if (!existingDates.contains(date)) {
            val row = sheet.createRow(lastRowIndex++)
            headers.forEachIndexed { index, header ->
                row.createCell(index).setCellValue(record[header]?.toString() ?: "")
            }
        }
    }

    FileOutputStream(file).use { fos -> workbook.write(fos) }
    workbook.close()
}
