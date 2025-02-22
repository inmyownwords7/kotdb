package com.db.kotlinapp.service

import com.db.kotlinapp.dto.CvDTO
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
    suspend fun saveTransactions(csvDtoList: List<CvDTO>) = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsername("defaultUser") // ✅ Look up user

        val transactionDtoList = csvDtoList.map { csvDto ->
            transactionMapper.toTransactionDTO(csvDto, user) // ✅ Assign userId
        }

        val transactionEntities = transactionDtoList.map { dto ->
            transactionMapper.toEntity(dto, user) // ✅ Convert to TransactionEntity
        }

        transactionRepository.saveAll(transactionEntities) // ✅ Save transactions
    }
}
    suspend fun getAllTransactions(): List<TransactionDTO> = withContext(Dispatchers.IO) {
        transactionRepository.findAll().map { transactionMapper.toDto(it) }
    }
}
