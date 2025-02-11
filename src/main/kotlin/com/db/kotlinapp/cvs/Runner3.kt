//package com.db.kotlinapp.cvs
//
//import com.db.kotlinapp.utils.CsvUtils
//import java.io.File
//
//fun main() {
//    val inputFolder = "src/main/resources/" // Change to your actual folder
//
//    val folder = File(inputFolder)
//    if (!folder.exists() || !folder.isDirectory) {
//        println("Error: Folder not found at $inputFolder")
//        return
//    }
//
//    // Process each CSV file
//    folder.listFiles { _, name -> name.endsWith(".CSV") }?.forEach { file ->
//        println("Processing file: ${file.name}")
//
//        val lines = file.readLines()
//        if (lines.isEmpty()) {
//            println("  Skipping empty file.\n")
//            return@forEach
//        }
//
//        // Use CsvUtils functions
//        val date = CsvUtils.extractDate(lines)
//        val transactions = CsvUtils.extractTransactions(lines)
//        val dataMap = CsvUtils.parseTransactions(transactions)
//
//        // Print results
//        println("  Date: $date")
//        if (dataMap.isEmpty()) {
//            println("  No valid transactions found.\n")
//        } else {
//            dataMap.forEach { (category, amount) ->
//                println("    $category: $amount")
//            }
//            println() // New line after each file for better readability
//        }
//    }
//}
