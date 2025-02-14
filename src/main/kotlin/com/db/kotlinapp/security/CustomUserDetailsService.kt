package com.db.kotlinapp.security

import com.db.kotlinapp.entity.UserEntity
import com.db.kotlinapp.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user: UserEntity = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}")) // ✅ Fix: Use `user.role`

        return User.builder()
            .username(user.username)
            .password(user.password) // ✅ Password should already be encoded
            .authorities(authorities) // ✅ Convert Role to Spring Security format
            .build()
    }
}
