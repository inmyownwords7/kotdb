package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.UserDTO
import com.db.kotlinapp.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {
/*
* UserDTO
* @param user
* @param password
* @return UserDTO object
* */
    @Mapping(target = "password", ignore = true)
    fun toDto(user: UserEntity): UserDTO

    @Mapping(target = "id", ignore = true)        // Ignore the auto-generated id
    fun toEntity(dto: UserDTO): UserEntity
}
