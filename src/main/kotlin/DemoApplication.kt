package com.example.demo

import com.example.demo.config.bootstrap.AppBootstrap
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.models.annotations.OpenAPI31
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import java.util.Locale
import java.util.TimeZone

@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPI31
class DemoApplication(
    private val appBootstrap: AppBootstrap,
) : ApplicationListener<ApplicationReadyEvent> {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostConstruct
    fun starting() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Locale.setDefault(Locale.US)
        logger.info { appBootstrap.bootLogMessage(headline = "STARTING SPRING BOOT APP ...") }
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        appBootstrap.onApplicationReadyEvent(event)
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
