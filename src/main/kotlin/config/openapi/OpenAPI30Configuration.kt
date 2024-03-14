package com.example.demo.config.openapi

import com.example.demo.api.ApiConfig
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


/**
 * https://www.baeldung.com/openapi-jwt-authentication
 */
@Component
class OpenAPI30Configuration(
    private val apiConfig: ApiConfig
) {

    @Bean
    fun customizeOpenAPI(): OpenAPI {
        val securitySchemeName: String = "bearerAuth"
        val securityScheme: SecurityScheme = SecurityScheme()
            .name(securitySchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")

        val info: Info = Info().title(apiConfig.qualifiedServiceName)

        val openApi: OpenAPI = OpenAPI()
            .info(info)
            .addSecurityItem(
                SecurityRequirement()
                    .addList(securitySchemeName)
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName, securityScheme
                    )
            )
        return openApi
    }
}