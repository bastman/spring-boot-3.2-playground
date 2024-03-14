package com.example.demo.api.debug

import com.example.demo.api.ApiConfig
import com.example.demo.api.common.rest.auth.ApiPrincipal
import com.example.demo.api.common.rest.auth.ApiPrincipalService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DebugApiController(
    private val apiPrincipalService: ApiPrincipalService
) {

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val API_BASE_URI: String = "${ApiConfig.API_BASE_URI}/debug"
    }

    @GetMapping("$API_BASE_URI/info")
    fun info(): Any {
        return mapOf("info" to "works :)")
    }

    @GetMapping("$API_BASE_URI/create-error")
    fun createError(): Unit {
        throw RuntimeException("Something is weird here. fixe me!")
    }

    /**
     * https://www.baeldung.com/get-user-in-spring-security
     */
    @GetMapping("$API_BASE_URI/me")
    fun me(
        authentication: JwtAuthenticationToken?
    ): Any {
        if (authentication == null) {
            return mapOf(
                "msg" to "unauthorized. no jwt.",
                "apiPrincipal" to null,
                "_debug" to mapOf(
                    "authenticationName" to null,
                    "jwt" to null,
                )
            )
        }
        val apiPrincipal: ApiPrincipal = apiPrincipalService.apiPrincipalFromAuth(authentication)
        val jwt: Jwt = apiPrincipalService.jwtFromAuth(authentication)
        val outcome: Map<String, Any?> = mapOf(
            "apiPrincipal" to apiPrincipal,
            "_debug" to mapOf(
                "authenticationName" to authentication.name,
                "jwt" to jwt,
            )
        )
        return outcome
    }


    @GetMapping("/test-authorization")
    fun testAuthorization(authentication: JwtAuthenticationToken): Any {
        val apiPrincipal: ApiPrincipal = apiPrincipalService.apiPrincipalFromAuth(authentication)

        val rolesRequiredAnyOf: Set<String> = setOf(
            "role100", "role200"
        )
        val m2mScopesRequiredAnyOf: Set<String> = setOf("scopeA", "scopeB")

        apiPrincipalService.requireRolesOrScopesAnyOf(
            apiPrincipal,
            rolesAnyOf = rolesRequiredAnyOf,
            m2mScopesAnyOf = m2mScopesRequiredAnyOf
        )

        return mapOf(
            "authorized" to true,
            "rolesRequiredAnyOf" to rolesRequiredAnyOf,
            "m2mScopesRequiredAnyOf" to m2mScopesRequiredAnyOf,
        )
    }

}