package com.example.demo.api.debug

import com.example.demo.api.ApiConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DebugApiController {

    companion object {
        private val logger = KotlinLogging.logger {}
        const val API_BASE_URI: String = "${ApiConfig.API_BASE_URI}/debug"
    }

    @GetMapping("/info")
    fun info(): Any {
        return mapOf("info" to "works :)")
    }

    @GetMapping("/create-error")
    fun createError(): Unit {
        throw RuntimeException("Something is weird here. fixe me!")
    }


}