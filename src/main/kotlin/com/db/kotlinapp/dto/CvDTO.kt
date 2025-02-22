package com.db.kotlinapp.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CvDTO(
    val id: Long? = null,

    @field:NotNull(message = "Date cannot be null")
    val date: LocalDate,

    @field:Min(value = 0, message = "GROCERIES TX cannot be negative")
    val groceriesTx: Double = 0.0,

    @field:Min(value = 0, message = "GROCERIES NTX cannot be negative")
    val groceriesNtx: Double = 0.0,

    @field:Min(value = 0, message = "CIGARETTES cannot be negative")
    val cigarettes: Double = 0.0,

    @field:Min(value = 0, message = "ALCOHOL cannot be negative")
    val alcohol: Double = 0.0,
)
