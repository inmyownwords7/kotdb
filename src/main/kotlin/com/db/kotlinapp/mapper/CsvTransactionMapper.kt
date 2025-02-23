package com.db.kotlinapp.mapper

import com.db.kotlinapp.dto.CvDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Mapper(componentModel = "spring")
interface CsvTransactionMapper {

    @Mapping(source = "Date", target = "date", qualifiedByName = ["mapDate"])
    @Mapping(source = "GROCERIES_TX", target = "groceriesTx", qualifiedByName = ["mapDouble"])
    @Mapping(source = "GROCERIES_NTX", target = "groceriesNtx", qualifiedByName = ["mapDouble"])
    @Mapping(source = "CIGARETTES", target = "cigarettes", qualifiedByName = ["mapDouble"])
    @Mapping(source = "ALCOHOL", target = "alcohol", qualifiedByName = ["mapDouble"])
    fun mapCsvToDto(data: Map<String, Any>): CvDTO

    // ✅ Fix: Convert `Date` safely
    @Named("mapDate")
    fun mapDate(date: Any?): LocalDate? {
        return try {
            date?.toString()?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
        } catch (e: Exception) {
            null // ✅ Returns null if conversion fails
        }
    }

    // ✅ Convert `Double` fields safely
    @Named("mapDouble")
    fun mapDouble(value: Any?): Double {
        return value?.toString()?.toDoubleOrNull() ?: 0.0 // ✅ Avoids exceptions
    }
}
