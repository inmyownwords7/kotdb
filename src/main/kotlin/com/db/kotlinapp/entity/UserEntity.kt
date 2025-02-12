package com.db.kotlinapp.entity

import jakarta.persistence.*

@Entity
@Table(name = "account_users")
class UserEntity(
    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false)
    var password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: Set<String> = setOf("USER") // Default role)
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;
    constructor(): this("", "", emptySet())
}
