package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.TransactionDTO
import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.entity.UserEntity
import org.mapstruct.*

@Mapper(componentModel = "spring")
interface TransactionMapper {

    @Mapping(source = "user.id", target = "userId") // ✅ Converts `UserEntity` to `userId`
    fun toDto(entity: TransactionEntity): TransactionDTO

    @Mapping(source = "userId", target = "user") // ✅ Converts `userId` back to `UserEntity`
    fun toEntity(dto: TransactionDTO, @Context user: UserEntity): TransactionEntity

    @ObjectFactory
    fun createTransactionEntity(dto: TransactionDTO, @Context user: UserEntity): TransactionEntity {
        return TransactionEntity(
            date = dto.date,
            groceriesTx = dto.groceriesTx,
            groceriesNtx = dto.groceriesNtx,
            cigarettes = dto.cigarettes,
            alcohol = dto.alcohol,
            user = user // ✅ Maps `userId` to `UserEntity`
        )
    }
}
