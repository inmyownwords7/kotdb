package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.entity.UserEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {

    fun toDto(user: UserEntity): UserDTO
    fun toEntity(dto: UserDTO): UserEntity
}
