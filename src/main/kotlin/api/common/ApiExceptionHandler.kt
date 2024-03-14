package com.example.demo.api.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@ControllerAdvice
class ApiExceptionHandler(
    private val errorAttributes: ErrorAttributes,
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val defaultErrorAttributeOptions: ErrorAttributeOptions = run {
        ErrorAttributeOptions.of(*ErrorAttributeOptions.Include.values())
    }

    @ExceptionHandler(value = [ResponseStatusException::class])
    protected fun catchResponseStatusException(
        ex: ResponseStatusException, request: WebRequest, authentication: Authentication?
    ): ResponseEntity<Any> {
        val springErrorCtx: Map<String, Any?> = errorAttributes.getErrorAttributes(
            request, defaultErrorAttributeOptions
        )
        val webReqCtx: WebRequestDetails = WebRequestDetails.of(webRequest = request, timestamp = Instant.now())

        val data: Map<String, Any?> = mapOf(
            "error" to mapOf(
                "message" to ex.message,
            ),
            "_reqCtx" to webReqCtx,
            "_springError" to springErrorCtx,
        )
        logger.error { "catchResponseStatusException(): ${ex.message}" }

        return ResponseEntity(data, ex.statusCode)
    }

    @ExceptionHandler(value = [Throwable::class])
    protected fun catchAll(
        ex: Throwable, request: WebRequest, authentication: Authentication?,
    ): ResponseEntity<Any> {
        val springErrorCtx: Map<String, Any?> = errorAttributes.getErrorAttributes(
            request, defaultErrorAttributeOptions
        )
        val webReqCtx: WebRequestDetails = WebRequestDetails.of(webRequest = request, timestamp = Instant.now())

        val data: Map<String, Any?> = mapOf(
            "error" to mapOf(
                "message" to ex.message,
            ),
            "_reqCtx" to webReqCtx,
            "_springError" to springErrorCtx,
        )
        logger.error { "catchAll(): ${ex.message}" }
        return ResponseEntity(data, HttpStatusCode.valueOf(500))
    }


}


private data class WebRequestDetails(
    val timestamp: Instant,
    val httpMethod: String,
    val httpPath: String,
    val requestUri: String?,
) {

    val httpEndpoint: String =
        listOfNotNull(httpMethod, httpPath)
            .filter { it.isNotBlank() }
            .joinToString(separator = " ")

    companion object {
        fun of(webRequest: WebRequest, timestamp: Instant): WebRequestDetails {
            val httpMethod: String? = when (webRequest) {
                is ServletWebRequest -> webRequest.httpMethod.toString()
                else -> null
            }
            val reqURI: String? = when (webRequest) {
                is ServletWebRequest -> webRequest.request.requestURI
                else -> null
            }
            val httpPath: String = listOfNotNull(
                webRequest.contextPath,
                reqURI
            ).coalesce()
                ?: ""

            return WebRequestDetails(
                timestamp = timestamp,
                httpMethod = httpMethod ?: "",
                httpPath = httpPath,
                requestUri = reqURI
            )
        }

        private fun List<String?>.coalesce(): String? = this.firstOrNull { !it.isNullOrBlank() }
    }
}





