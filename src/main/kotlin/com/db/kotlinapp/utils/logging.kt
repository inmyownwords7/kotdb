package com.db.kotlinapp.utils

import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("AppLogger")

fun logInfo(message: String) = logger.info(message)
fun logWarn(message: String) = logger.warn(message)
fun logError(message: String) = logger.error(message)
