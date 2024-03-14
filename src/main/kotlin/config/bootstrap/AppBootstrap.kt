package com.example.demo.config.bootstrap

import com.example.demo.api.ApiConfig
import com.example.demo.util.runtime.RuntimeStats
import com.example.demo.util.spring.binder.SpringConfigBinder
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.lang.management.RuntimeMXBean
import java.time.Instant
import java.util.Locale

@Component
class AppBootstrap(
    private val environment: Environment,
    private val apiConfig: ApiConfig,
) {

    companion object : RuntimeStats {
        private val logger = KotlinLogging.logger {}
    }

    fun onApplicationReadyEvent(event: ApplicationReadyEvent) {
        logger.info { "===== SPRING BOOT APP: BOOTSTRAP STARTED ====" }

        // you may want to do sth here, e.g.: ...
        val appConf: Any = SpringConfigBinder.getEnvironmentProperties(
            environment = environment,
            configKey = "app",
            jacksonTypeRef<Any>()
        )
        logger.info { "onApplicationReadyEvent(): appConf: $appConf" }

        logger.info { bootLogMessage(headline = "ON SPRING BOOT APP READY ...") }
        logger.info { "===== SPRING BOOT APP: BOOTSTRAP DONE. ====" }
    }

    fun bootLogMessage(headline: String): String {
        val mx: RuntimeMXBean = runtimeMxBean()
        val systemProps = mx.systemProperties.toMap()
            .filterKeys { it !in listOf("java.class.path") }
        val activeProfileNames = environment.activeProfiles.map { it }
        val defaultProfileNames = environment.defaultProfiles.map { it }

        return """
           | === $headline
           | - service: ${apiConfig.serviceName}
           | - spring.profiles.default: ${defaultProfileNames}
           | - spring.profiles.active: ${activeProfileNames}
           | - charset: ${defaultCharset()} - test: Malmö
           | - timezone: ${defaultTimezone().id} - test: ${Instant.now()}
           | - locale: ${Locale.getDefault()}
           | - locale (FORMAT): ${Locale.getDefault(Locale.Category.FORMAT)}
           | - memory heap stats (in MB): ${memoryStatsInMegaBytes()}
           | - processors: ${availableProcessors()}
           | - mx.inputArgs: ${mx.inputArguments}
           | - mx.systemProps: $systemProps
           | === """.trimMargin()
    }

}