package com.db.kotlinapp.service

import com.db.kotlinapp.entity.UserEntity
import com.db.kotlinapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(username: String, password: String): UserEntity {
val hashedPassword = passwordEncoder.encode(password)
        val user = UserEntity(username, hashedPassword, setOf("USER"))
        return userRepository.save(user)
    }
}
