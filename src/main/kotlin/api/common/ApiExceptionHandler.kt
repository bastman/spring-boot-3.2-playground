package com.example.demo.api.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class ApiExceptionHandler {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @ExceptionHandler(value = [ResponseStatusException::class])
    protected fun catchResponseStatusException(
        ex: ResponseStatusException, request: WebRequest, authentication: Authentication?,
    ): ResponseEntity<Any> {

        val data: Map<String, Any?> = mapOf(
            "error" to mapOf(
                "message" to ex.message,
            )
        )
        logger.error(ex) { "catchResponseStatusException(): ${ex.message}" }

        return ResponseEntity(data, ex.statusCode)
    }

    @ExceptionHandler(value = [Throwable::class])
    protected fun catchAll(
        ex: Throwable, request: WebRequest, authentication: Authentication?,
    ): ResponseEntity<Any> {

        val data: Map<String, Any?> = mapOf(
            "error" to mapOf(
                "message" to ex.message,
            )
        )
        logger.error(ex) { "catchAll(): ${ex.message}" }
        return ResponseEntity(data, HttpStatusCode.valueOf(500))
    }


}