package com.db.kotlinapp.repository

import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, Long> {
    fun findByUser(user: UserEntity): List<TransactionEntity> // ✅ Get transactions for a specific user
}
