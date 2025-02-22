package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring")
interface UserMapper {
    @Mapping(target = "password", ignore = true)
    fun toDto(user: UserEntity): UserDTO

    @Mapping(target = "id", ignore = true)        // Ignore the auto-generated id
    @Mapping(target = "copy", ignore = true)
    fun toEntity(dto: UserDTO): UserEntity
}
