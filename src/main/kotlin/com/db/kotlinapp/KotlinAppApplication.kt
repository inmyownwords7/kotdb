package com.db.kotlinapp

import com.db.kotlinapp.cvs.executeProcess
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinAppApplication

fun main(args: Array<String>) {
    runApplication<KotlinAppApplication>(*args)

    runBlocking { // ✅ Ensures `executeProcess()` runs in a coroutine
        executeProcess()
    }
}
