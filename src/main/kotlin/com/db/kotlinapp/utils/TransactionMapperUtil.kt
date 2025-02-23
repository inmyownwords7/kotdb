package com.db.kotlinapp.utils

import com.db.kotlinapp.dto.TransactionDTO
import com.db.kotlinapp.entity.TransactionEntity
import com.db.kotlinapp.entity.UserEntity
import java.time.LocalDate

object TransactionMapperUtil {

    fun toTransactionDTO(entity: TransactionEntity): TransactionDTO {
        return TransactionDTO(
            id = entity.id,
            date = entity.date ?: LocalDate.now(),
            groceriesTx = entity.groceriesTx,
            groceriesNtx = entity.groceriesNtx,
            cigarettes = entity.cigarettes,
            alcohol = entity.alcohol,
            userId = entity.user?.id // ✅ Assign userId manually
        )
    }

    fun toTransactionEntity(dto: TransactionDTO, user: UserEntity): TransactionEntity {
        return TransactionEntity(
            date = dto.date,
            groceriesTx = dto.groceriesTx,
            groceriesNtx = dto.groceriesNtx,
            cigarettes = dto.cigarettes,
            alcohol = dto.alcohol,
            user = user // ✅ Set user reference manually
        )
    }
}