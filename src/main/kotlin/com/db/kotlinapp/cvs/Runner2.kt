package com.db.kotlinapp.cvs

import java.io.File
import java.nio.file.Paths

fun main() {
    val classLoader = Thread.currentThread().contextClassLoader
    val file = classLoader.getResource("Z005_30.CSV")?.toURI()?.let { File(it) }

    if (file == null || !file.exists()) {
        println("Error: File not found!")
        return
    }

    val filePath = Paths.get("src/main/resources/Z005_30.CSV").toFile().absolutePath

    val lines = File(filePath).readLines()

    val dateLine = lines.firstOrNull { it.startsWith("\"DATE") }
    val date = dateLine?.split(",")?.getOrNull(1)?.replace("\"", "")?.trim() ?: "Unknown Date"

    val startIndex = lines.indexOfFirst { it.trim().isEmpty() } + 1
    val transactions = lines.drop(startIndex)

    val validCategories = setOf("GROCERY NTX", "GROCERIES TX", "ALCOHOL", "CIGARETTES")

    // Using MutableMap<String, MutableMap<String, Double>> to store accumulated amounts
    val dataMap = mutableMapOf<String, MutableMap<String, Double>>()

    for (line in transactions) {
        val fields = line.split(",").map { it.replace("\"", "").trim().uppercase() }

        if (fields.size < 4) continue  // Skip malformed lines

        val descriptor = fields[1]
        val amount = fields[3].toDoubleOrNull() ?: 0.0  // Convert amount to Double

        for (category in validCategories) {
            if (descriptor.contains(category, ignoreCase = true)) {
                val dateEntries = dataMap.getOrPut(date) { mutableMapOf() }
                dateEntries[category] = dateEntries.getOrDefault(category, 0.0) + amount
            }
        }
    }

    println(dataMap)
}
