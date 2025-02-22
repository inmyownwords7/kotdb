package com.db.kotlinapp

import com.db.kotlinapp.entity.UserEntity
import com.db.kotlinapp.enums.Role
import com.db.kotlinapp.executor.executeProcess
import com.db.kotlinapp.repository.UserRepository
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
@Bean
fun initializeDefaultUser(userRepository: UserRepository): CommandLineRunner {
    return CommandLineRunner {
        // Check if any user exists. If not, create a default user.
        if (userRepository.count() == 0L) {
            // Create a new UserEntity with a default username, password, and role.
            val defaultUser = UserEntity("defaultUser", "encryptedPassword", Role.ADMIN)
            userRepository.save(defaultUser)
            println("Default user created: defaultUser")
        } else {
            println("Users already exist. No default user created.")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<KotlinAppApplication>(*args)
}
