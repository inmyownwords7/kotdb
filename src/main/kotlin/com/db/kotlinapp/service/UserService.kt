package com.db.kotlinapp.service

import com.db.kotlinapp.entity.UserEntity
import com.db.kotlinapp.enums.Role
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

        val user = UserEntity(username = username, password = hashedPassword, roles = setOf(Role.USER.name)
        )
        return userRepository.save(user)
    }
}
