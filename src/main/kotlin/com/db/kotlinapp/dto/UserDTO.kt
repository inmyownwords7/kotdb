package com.db.kotlinapp.dto

data class UserDTO(
    // id is only used here for output it is null in case of input
    val id: Long? = null,
    val username: String,
    val password: String? = null,
    val roles: List<String> = listOf("USER") // ✅ Now stored as Strings instead of Enums
)
