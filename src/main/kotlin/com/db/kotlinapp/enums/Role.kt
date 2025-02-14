package com.db.kotlinapp.enums

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role {
    USER {
        override fun getAuthorities(): Set<GrantedAuthority> {
            return setOf(SimpleGrantedAuthority("ROLE_USER"))
        }
    },
    ADMIN {
        override fun getAuthorities(): Set<GrantedAuthority> {
            return setOf(
                SimpleGrantedAuthority("ROLE_ADMIN"),
                SimpleGrantedAuthority("CAN_DELETE"),
                SimpleGrantedAuthority("CAN_MANAGE_USERS")
            )
        }
    };

    abstract fun getAuthorities(): Set<GrantedAuthority>
}
