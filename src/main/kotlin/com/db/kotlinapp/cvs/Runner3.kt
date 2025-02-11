package com.db.kotlinapp.cvs

import java.io.File

/**
 * Processes all CSV files in the given folder.
 */
fun processCSVFiles(folder: File): MutableMap<String, MutableMap<String, Map<String, String>>> {
    val allData = mutableMapOf<String, MutableMap<String, Map<String, String>>>()

    folder.listFiles { _, name -> name.endsWith(".CSV") }?.forEach { file ->
        println("Processing file: ${file.name}")

        val fileData = processCSVFile(file)
        if (fileData.isNotEmpty()) {
            allData[file.name] = fileData
        }
    }

    return allData
}

/**
 * Processes a single CSV file and extracts relevant data.
 */
fun processCSVFile(file: File): MutableMap<String, Map<String, String>> {
    val lines = file.readLines()
    if (lines.isEmpty()) return mutableMapOf()

    val date = extractDate(lines)
    val transactions = extractTransactions(lines)

    val dataMap = parseTransactions(transactions)

    return if (dataMap.isNotEmpty()) mutableMapOf(date to dataMap) else mutableMapOf()
}

/**
 * Extracts the date from the metadata in the CSV file.
 */
//fun extractDate(lines: List<String>): String {
//    val dateLine = lines.firstOrNull { it.startsWith("\"DATE") }
//    return dateLine?.split(",")?.getOrNull(1)?.replace("\"", "")?.trim() ?: "Unknown Date"
//}

/**
 * Extracts transaction records from the CSV file.
 */
fun extractTransactions(lines: List<String>): List<String> {
    val startIndex = lines.indexOfFirst { it.trim().isEmpty() } + 1
    return if (startIndex > 0 && startIndex < lines.size) lines.drop(startIndex) else emptyList()
}

/**
 * Parses transaction records and filters valid categories.
 */
fun parseTransactions(transactions: List<String>): Map<String, String> {
    val validCategories = setOf("GROCERY NTX", "GROCERIES TX", "ALCOHOL", "CIGARETTES")
    println("Valid Categories: $validCategories")

    val dataMap = mutableMapOf<String, String>()

    for (line in transactions) {
        val fields = line.split(",").map { it.replace("\"", "").trim().uppercase() }

        if (fields.size < 4) continue  // Skip malformed lines

        val descriptor = fields[1]
        val amount = fields[3]

        for (category in validCategories) {
            if (descriptor.contains(category, ignoreCase = true)) {
                dataMap[category] = amount
            }
        }
    }

    return dataMap
}
fun main() {
    val inputFolder = "src/main/resources/" // Change to your input folder

    val folder = File(inputFolder)
    if (!folder.exists() || !folder.isDirectory) {
        println("Error: Folder not found at $inputFolder")
        return
    }
//this prints the output of the function processCSVFile() function
    val allData = processCSVFiles(folder)
    println(allData)
}