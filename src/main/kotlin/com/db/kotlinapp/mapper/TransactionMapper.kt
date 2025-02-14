//package com.db.kotlinapp.mapper
//
//import com.db.kotlinapp.dto.TransactionDTO
//import com.db.kotlinapp.entity.TransactionEntity
//
//@Mapper(componentModel = "spring")
//interface TransactionMapper {
//
//    companion object {
//        val INSTANCE: TransactionMapper = Mappers.getMapper(TransactionMapper::class.java)
//    }
//
//    @Mapping(source = "user.id", target = "userId") // ✅ Maps `user.id` to `userId`
//    fun toDto(entity: TransactionEntity): TransactionDTO
//
//    @Mapping(source = "userId", target = "user.id") // ✅ Maps `userId` back to `user`
//    fun toEntity(dto: TransactionDTO): TransactionEntity
//}
