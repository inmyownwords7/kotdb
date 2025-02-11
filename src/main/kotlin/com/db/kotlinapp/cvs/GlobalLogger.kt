package com.db.kotlinapp.cvs

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Global logging utility for consistent logging across the application.
 */
object GlobalLogger {
    private val logger: Logger = LoggerFactory.getLogger("GlobalLogger")

    fun info(message: String) {
        logger.info(message)
    }

    fun warn(message: String) {
        logger.warn(message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.error(message, throwable)
        } else {
            logger.error(message)
        }
    }

    fun debug(message: String) {
        logger.debug(message)
    }

    fun trace(message: String) {
        logger.trace(message)
    }

    /**
     * Logs messages with the class name that calls the function.
     */
    fun logWithClass(clazz: Class<*>, level: String, message: String, throwable: Throwable? = null) {
        val classLogger = LoggerFactory.getLogger(clazz)

        when (level.lowercase()) {
            "info" -> classLogger.info(message)
            "warn" -> classLogger.warn(message)
            "error" -> if (throwable != null) classLogger.error(message, throwable) else classLogger.error(message)
            "debug" -> classLogger.debug(message)
            "trace" -> classLogger.trace(message)
            else -> classLogger.info(message) // Default to INFO level
        }
    }
}
