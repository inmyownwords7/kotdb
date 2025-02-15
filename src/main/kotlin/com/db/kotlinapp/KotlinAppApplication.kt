package com.db.kotlinapp

import com.db.kotlinapp.executor.executeProcess
import com.db.kotlinapp.service.CsvService
import com.db.kotlinapp.service.ExcelService
import com.db.kotlinapp.service.TransactionService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class KotlinAppApplication {
    @Bean
    fun runProcess(
        csvService: CsvService,
        transactionService: TransactionService,
        excelService: ExcelService
    ): CommandLineRunner {
        return CommandLineRunner {
            runBlocking {
                executeProcess(csvService, transactionService, excelService)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<KotlinAppApplication>(*args)
}
