package com.db.kotlinapp.service

import com.db.kotlinapp.dto.TransactionDTO
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream

@Service
class ExcelService {

    fun exportTransactionsToExcel(filePath: String, transactions: List<TransactionDTO>): Boolean {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Transactions")

            // ✅ Create Header Row
            val headerRow = sheet.createRow(0)
            val headers = listOf("Date", "GROCERIES TX", "GROCERIES NTX", "CIGARETTES", "ALCOHOL", "User ID")

            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            // ✅ Populate Data Rows
            transactions.forEachIndexed { rowIndex, transaction ->
                val row = sheet.createRow(rowIndex + 1)
                row.createCell(0).setCellValue(transaction.date.toString())
                row.createCell(1).setCellValue(transaction.groceriesTx)
                row.createCell(2).setCellValue(transaction.groceriesNtx)
                row.createCell(3).setCellValue(transaction.cigarettes)
                row.createCell(4).setCellValue(transaction.alcohol)
                row.createCell(5).setCellValue(transaction.userId.toDouble()) // ✅ Uses userId instead of full UserEntity
            }

            // ✅ Save the file
            FileOutputStream(File(filePath)).use { workbook.write(it) }
            workbook.close()

            println("✅ Transactions exported to Excel: $filePath")
            true // ✅ Return success
        } catch (e: Exception) {
            println("❌ Excel export failed: ${e.message}")
            false // ✅ Return failure
        }
    }
}
