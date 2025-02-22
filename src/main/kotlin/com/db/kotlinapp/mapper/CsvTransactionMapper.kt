package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.CvDTO
import com.db.kotlinapp.dto.TransactionDTO
import org.mapstruct.*
import java.time.LocalDate

@Mapper(componentModel = "spring")
interface CsvTransactionMapper {

    @Mapping(source = "Date", target = "date", qualifiedByName = ["mapDate"])
    @Mapping(source = "GROCERIES_TX", target = "groceriesTx", qualifiedByName = ["mapDouble"])
    @Mapping(source = "GROCERIES_NTX", target = "groceriesNtx", qualifiedByName = ["mapDouble"])
    @Mapping(source = "CIGARETTES", target = "cigarettes", qualifiedByName = ["mapDouble"])
    @Mapping(source = "ALCOHOL", target = "alcohol", qualifiedByName = ["mapDouble"])
    fun mapCsvToDto(data: Map<String, Any>): CvDTO

    @Named("mapDate")
    fun mapDate(date: Any?): LocalDate = LocalDate.parse(date.toString())

    @Named("mapDouble")
    fun mapDouble(value: Any?): Double = value?.toString()?.toDoubleOrNull() ?: 0.0
}
