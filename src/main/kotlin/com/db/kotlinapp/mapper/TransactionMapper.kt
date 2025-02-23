package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.*;
import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.entity.UserEntity
import org.mapstruct.*

@Mapper(componentModel = "spring")
interface TransactionMapper {

    // ✅ Explicitly map `userId` because CvDTO does NOT contain it
    @Mapping(target = "userId", source = "userId")
    fun toTransactionDTO(cvDTO: CvDTO, userId: Long): TransactionDTO

    @Mapping(target = "user", source = "user") // Correct mapping to assign `UserEntity`
    fun toEntity(dto: TransactionDTO, @Context user: UserEntity): TransactionEntity

    // ✅ Convert TransactionEntity → TransactionDTO (extracts userId)
    @Mapping(source = "user.id", target = "userId") // Converts `UserEntity` to userId
    fun toDto(entity: TransactionEntity): TransactionDTO

}
