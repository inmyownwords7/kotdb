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
    private val transactionMapper: TransactionMapper
) {

    @Transactional
    suspend fun saveTransactions(dtoList: List<TransactionDTO>) = withContext(Dispatchers.IO) { // ✅ Moves to IO thread
        val entities = dtoList.mapNotNull { dto ->
            val user = userRepository.findById(dto.userId)
                .orElse(null) // ✅ Avoids crash if user not found

            if (user == null) {
                println("⚠️ Skipping transaction: User ID ${dto.userId} not found!")
                null // ✅ Skips invalid transactions
            } else {
                transactionMapper.toEntity(dto, user) // ✅ Correctly maps `userId` → `UserEntity`
            }
        }

        transactionRepository.saveAll(entities) // ✅ Saves only valid transactions
    }

    suspend fun getAllTransactions(): List<TransactionDTO> = withContext(Dispatchers.IO) {
        transactionRepository.findAll().map { transactionMapper.toDto(it) }
    }
}
