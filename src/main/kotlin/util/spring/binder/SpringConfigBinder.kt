package com.example.demo.util.spring.binder

import com.example.demo.config.jackson.Jackson
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.Environment

object SpringConfigBinder {

    inline fun <reified T : Any> getEnvironmentProperties(
        environment: Environment,
        configKey: String,
        typeRef: TypeReference<T>,
        objectMapper: ObjectMapper = Jackson.defaultMapper()
    ): T {
        try {
            require(configKey.isNotBlank()) { "require(configKey.isNotBlank()) configKey: $configKey" }
        } catch (all: Exception) {
            error("Failed to load configValue. reason: ${all.message}. environment.configKey: $configKey")
        }

        val data: Any = run {
            try {
                val asList = Binder.get(environment)
                    .bind(configKey, Bindable.listOf(Any::class.java)).get()
                return@run asList
            } catch (all: Throwable) {
            }
            try {
                val asMap = Binder.get(environment)
                    .bind(configKey, Bindable.mapOf(String::class.java, Any::class.java)).get()
                return@run asMap
            } catch (all: Throwable) {
            }

        } ?: throw RuntimeException(
            "Failed to convert content of environment.$configKey to ${T::class.java} !"
                    + " Expected: environment.$configKey to be of type Map<String,Any?> or List<Any?>"
                    + " reason: No value bound / configKey not found or not applicable."
        )

        val value: T = try {
            objectMapper.convertValue(data, typeRef)
        } catch (all: Exception) {
            error("Failed to convert configValue from ${data::class.qualifiedName} to ${T::class.qualifiedName}. configKey: $configKey reason: ${all.message}")
        }
        return value
    }

}