package com.db.kotlinapp.entity

import com.db.kotlinapp.enums.Role
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "account_users")
class UserEntity(
    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING) // ✅ Ensures role is stored as a String
    var role: Role, // ✅ A user has only one role

) : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var isDeleted: Boolean = false

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return role.getAuthorities() // ✅ Convert Role to Authorities
    }

    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    // ✅ Corrected default constructor (Avoids error)
    constructor() : this("", "", Role.USER)
}
