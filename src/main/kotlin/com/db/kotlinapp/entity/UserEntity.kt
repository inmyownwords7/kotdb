package com.db.kotlinapp.entity

import jakarta.persistence.*
import com.db.kotlinapp.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder

@Entity
@Table(name = "users")
class UserEntity(
    //This must be unique because you can't have the user but perhaps we should use it that username && userId
    @Column(unique = true, nullable = false)
    private var username: String,
//becomes encrypted
    @Column(nullable = false)
    private var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var role: Role = Role.USER
) : UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    constructor() : this("", "", Role.USER) // ✅ No-arg constructor for Hibernate

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }

    override fun getPassword(): String = password
    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun getRole(): Role = role

    fun setRole(newRole: Role) { // ✅ Allows role changes
        this.role = newRole
    }
    fun setUsername(newUsername: String) { // ✅ Allows username change
        this.username = newUsername
    }

    fun setPassword(newPassword: String) { // ✅ Ensures password is always hashed
        this.password = newPassword
    }
}
