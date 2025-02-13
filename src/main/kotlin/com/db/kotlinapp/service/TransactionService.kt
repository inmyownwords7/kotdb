package com.db.kotlinapp.service

import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.entity.UserEntity
import com.db.kotlinapp.repository.TransactionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {

    // ✅ Get all transactions for a user (with optional pagination)
    fun getUserTransactions(user: UserEntity, page: Int, size: Int): Page<TransactionEntity> {
        val pageable = PageRequest.of(page, size) // ✅ Only fetch `size` transactions per page
        return transactionRepository.findByUser(user, pageable) // ✅ Efficient query
    }

    // ✅ Create a new transaction
    fun createTransaction(transaction: TransactionEntity): TransactionEntity {
        return transactionRepository.save(transaction)
    }
}
