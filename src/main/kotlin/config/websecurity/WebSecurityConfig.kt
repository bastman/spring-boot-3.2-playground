package com.example.demo.config.websecurity

import com.example.demo.api.debug.DebugApiController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
class WebSecurityConfig {

    private val unprotectedUrls: List<String> = listOf(
        "/health",

        // swagger
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",

        // custom
        "/version.txt",
        "${DebugApiController.API_BASE_URI}/info",
        "${DebugApiController.API_BASE_URI}/create-error",
        "${DebugApiController.API_BASE_URI}/debug/me",
        "${DebugApiController.API_BASE_URI}/polymorphic-deserialization-example"

    )


    @Bean
    fun securityFilterChain(
        http: HttpSecurity

    ): DefaultSecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(*unprotectedUrls.toTypedArray()).permitAll()
            }
            .authorizeHttpRequests {
                it.anyRequest().fullyAuthenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
                oauth2.jwt {}
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }


        return http.build()
    }

}

