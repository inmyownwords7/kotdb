package com.db.kotlinapp.dto

data class UserDTO(
    val id: Long? = null,
    val username: String,
    val roles: List<String> = listOf("USER") // ✅ Now stored as Strings instead of Enums
)
