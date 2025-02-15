package com.db.kotlinapp.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

/**
 * Global exception handler for handling application-wide errors.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles generic exceptions.
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = ex.localizedMessage ?: "An unexpected error occurred",
            path = request.getDescription(false)
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * Handles `IllegalArgumentException` (e.g., invalid user input).
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.localizedMessage,
            path = request.getDescription(false)
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handles validation errors (e.g., `@NotNull`, `@Size` constraints).
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associateBy(
            { it.field },
            { it.defaultMessage ?: "Invalid value" }
        )

        val errorResponse = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = "Validation failed",
            path = request.getDescription(false),
            validationErrors = errors
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}

/**
 * Data class for returning structured error responses.
 */
data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val validationErrors: Map<String, String>? = null
)
