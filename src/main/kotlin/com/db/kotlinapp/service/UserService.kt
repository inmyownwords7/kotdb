package com.db.kotlinapp.service

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.enums.Role
import com.db.kotlinapp.mapper.UserMapper
import com.db.kotlinapp.repository.UserRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {

    @Transactional
    @PreAuthorize("hasRole('ADMIN')") // ✅ Only admins can change roles
    fun changeUserRole(username: String, newRole: Role): UserDTO {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("❌ User not found!")

        if (user.getRole() == newRole) {
            throw IllegalArgumentException("❌ User is already assigned role: $newRole")
        }

        user.setRole(newRole) // ✅ Update the role
        val updatedUser = userRepository.save(user)
// why are we returning a dto here?
        return userMapper.toDto(updatedUser) // ✅ Return updated user as DTO
    }
//
//    fun registerUser(userDTO: UserDTO): UserDTO {
//        val userEntity = userMapper.toEntity(userDTO)
//        val savedUser = userRepository.save(userEntity)
//        return userMapper.toDto(savedUser) // ✅ Return saved user as DTO
//    }
}
