package com.db.kotlinapp.service

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.entity.UserEntity
import com.db.kotlinapp.enums.Role
import com.db.kotlinapp.mapper.UserMapper
import com.db.kotlinapp.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/*
 * @param username
 * @param password?
 * @param newRole
 * @return UserDTO
 *
 * This method changes the user's role and returns the updated user as a DTO.
 * It throws an exception if the user does not exist or if the new role is the same as the existing one.
 * It also ensures that only admins can change roles.
 *
 * Note: This method is annotated with the @PreAuthorize annotation to enforce role-based access control.
 *
 * In a real-world scenario, you might want to log the changes made to the user's role,
 * or perform additional validation or processing based on the new role.
 *
 * Also, consider using a more efficient data structure or method to store and retrieve user roles.
 * For example, you could use a Map<String, Role> to store the user roles, where the username is the key.
 *
 * */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct // ✅ Runs automatically when the application starts
    @Transactional
    fun createDefaultAdminUser() {
        val adminUsername = "ADMIN"

        if (!userRepository.existsByUsername(adminUsername)) {
            val adminUser = UserEntity(
                username = adminUsername,
                password = passwordEncoder.encode("admin123"), // ✅ Change this in production!
                role = Role.ADMIN
            )
            userRepository.save(adminUser)
            println("✅ Default ADMIN user created: $adminUsername")
        } else {
            println("✅ ADMIN user already exists")
        }
    }

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
        return userMapper.toDto(updatedUser) // ✅ Return updated user as DTO
    }
    fun registerUser(userDTO: UserDTO): UserDTO {
        if (userRepository.existsByUsername(userDTO.username)) {
            throw IllegalArgumentException("�� User already exists!")
        }

        val userEntity = userMapper.toEntity(userDTO)
        userEntity.password = passwordEncoder.encode(userDTO.password)
        val savedUser = userRepository.save(userEntity)
        return userMapper.toDto(savedUser)
    }
    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserDTO? {
        val userEntity = userRepository.findByUsername(username) ?: return null
        return userMapper.toDto(userEntity) // ✅ Convert UserEntity → UserDTO before returning
    }
}
