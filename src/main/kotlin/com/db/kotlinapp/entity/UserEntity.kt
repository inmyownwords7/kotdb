package com.db.kotlinapp.entity

import jakarta.persistence.*

@Entity
@Table(name = "account_users")
class UserEntity(
    @Column(nullable = false, unique = true)
    private var username: String,

    @Column(nullable = false)
    private var password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    private var roles: Set<String> = setOf("USER") // Default role)

    @Column(nullable = false)
    var isDeleted: Boolean = false

) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0;
    constructor(): this("", "", emptySet())
}
