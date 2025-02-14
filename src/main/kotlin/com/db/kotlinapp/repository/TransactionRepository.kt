package com.db.kotlinapp.repository

import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, Long> {
    fun findByUser(user: UserEntity, pageable: Pageable): Page<TransactionEntity> // ✅ Get transactions for a specific user
    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<TransactionEntity>
}
