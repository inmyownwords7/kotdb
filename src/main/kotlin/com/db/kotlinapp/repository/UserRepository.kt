package com.db.kotlinapp.repository

import com.db.kotlinapp.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): Optional<UserEntity> // ✅ Find user by username
    fun findByIsDeletedFalse(): List<UserEntity> // ✅ Get only active users
}
