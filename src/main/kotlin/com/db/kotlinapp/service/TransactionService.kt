package com.db.kotlinapp.service

import com.db.kotlinapp.dto.TransactionDTO
import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.repository.TransactionRepository
import com.db.kotlinapp.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) {
    fun saveTransaction(transactionDTO: TransactionDTO): TransactionDTO {
        val user = userRepository.findById(transactionDTO.userId)
            .orElseThrow { IllegalArgumentException("User not found with ID: ${transactionDTO.userId}") }

        // ✅ Convert DTO → Entity before saving
        val transaction = TransactionEntity(
            date = transactionDTO.date,
            groceriesTx = transactionDTO.groceriesTx,
            groceriesNtx = transactionDTO.groceriesNtx,
            cigarettes = transactionDTO.cigarettes,
            alcohol = transactionDTO.alcohol,
            user = user
        )

        val savedTransaction = transactionRepository.save(transaction) // ✅ Now it's a TransactionEntity

        // ✅ Convert Entity → DTO before returning the response
        return TransactionDTO(
            date = savedTransaction.date,
            groceriesTx = savedTransaction.groceriesTx,
            groceriesNtx = savedTransaction.groceriesNtx,
            cigarettes = savedTransaction.cigarettes,
            alcohol = savedTransaction.alcohol,
            userId = savedTransaction.user.id!!
        )
    }
    fun getTransactionsByUserId(userId: Long, pageable: Pageable): Page<TransactionDTO> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with ID: $userId") }

        val transactionsPage: Page<TransactionEntity> = transactionRepository.findByUser(user, pageable)

        // ✅ Automatically maps `Page<TransactionEntity>` → `Page<TransactionDTO>`
        return transactionsPage.map { transaction ->
            TransactionDTO(
                date = transaction.date,
                groceriesTx = transaction.groceriesTx,
                groceriesNtx = transaction.groceriesNtx,
                cigarettes = transaction.cigarettes,
                alcohol = transaction.alcohol,
                userId = transaction.user.id!!
            )
        }
    }
    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<TransactionDTO> {
        val transactionsPage: Page<TransactionEntity> =
            transactionRepository.findByDateBetween(startDate, endDate, pageable)

        return transactionsPage.map { transaction ->
            TransactionDTO(
                date = transaction.date,
                groceriesTx = transaction.groceriesTx,
                groceriesNtx = transaction.groceriesNtx,
                cigarettes = transaction.cigarettes,
                alcohol = transaction.alcohol,
                userId = transaction.user.id!!
            )
        }
    }
}
