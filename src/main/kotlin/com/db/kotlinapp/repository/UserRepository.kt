package com.db.kotlinapp.repository

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    // This is used a check to see if it's null or not
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserEntity? // ✅ Returns UserEntity, used for authentication
}
