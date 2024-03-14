package com.example.demo.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class ApiConfig(
    @Value(value = "\${app.serviceName}") val serviceName: String,
    @Value(value = "\${app.envName}") val envName: AppEnvName,
) {
    val qualifiedServiceName: String
        get() = "$serviceName-$envName"
    val title: String
        get() = "API $serviceName ($envName)"

    companion object {
        const val API_BASE_URI = "/api"
    }
}