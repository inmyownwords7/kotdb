package com.db.kotlinapp.service

import com.db.kotlinapp.dto.TransactionDTO
import com.db.kotlinapp.mapper.TransactionMapper
import com.db.kotlinapp.repository.TransactionRepository
import com.db.kotlinapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val transactionMapper: TransactionMapper,
    private val csvService: CsvService
) {
    @Transactional
    suspend fun saveTransactionsFromCSV(directory: String, username: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("❌ User '$username' not found!")

        val csvDtoList = csvService.processCSVFiles(directory) // ✅ Fetch parsed CSV data

        if (csvDtoList.isEmpty()) {
            println("⚠️ No transactions found in CSV. Nothing to save.")
            return
        }

        val transactionEntities = csvDtoList.map { csvDto ->
            val transactionDto = transactionMapper.toTransactionDTO(csvDto, user.id!!)
            transactionMapper.toEntity(transactionDto, user)
        }

        transactionRepository.saveAll(transactionEntities) // ✅ Save to DB
    }

    // ✅ Save a list of transactions
    @Transactional
    suspend fun saveTransactions(transactions: List<TransactionDTO>) = withContext(Dispatchers.IO) {
        if (transactions.isEmpty()) {
            println("⚠️ No transactions to save.")
            return@withContext
        }

        val transactionEntities = transactions.map { dto ->
            val user = userRepository.findById(dto.userId!!)
                .orElseThrow { IllegalArgumentException("❌ User ID ${dto.userId} not found!") }

            transactionMapper.toEntity(dto, user)
        }
        transactionRepository.saveAll(transactionEntities) // ✅ Save all transactions
        println("✅ ${transactionEntities.size} transactions saved to database.")
    }

    // ✅ Retrieve all transactions from the database
    @Transactional(readOnly = true)
    fun getAllTransactions(): List<TransactionDTO> {
        val transactionEntities = transactionRepository.findAll() // ✅ Fetch from DB
        return transactionEntities.map { transactionMapper.toDto(it) } // ✅ Convert Entity to DTO
    }
}
