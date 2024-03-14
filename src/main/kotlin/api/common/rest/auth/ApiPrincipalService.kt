package com.example.demo.api.common.rest.auth

import com.example.demo.config.oauth2.resourceserver.OAuth2ResourceServerConfig
import org.springframework.http.HttpStatusCode
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class ApiPrincipalService(
    private val resourceServerConfig: OAuth2ResourceServerConfig
) {

    private val userClaimsNamespace: String = resourceServerConfig.userClaimsNamespace
    private val userRolesClaim: String = resourceServerConfig.userRolesClaim

    fun jwtFromAuth(authentication: JwtAuthenticationToken): Jwt {
        val jwt: Jwt = (authentication.credentials as? Jwt)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "no jwt")
        return jwt
    }

    fun apiPrincipalFromAuth(authentication: JwtAuthenticationToken): ApiPrincipal {
        val jwt: Jwt = jwtFromAuth(authentication)

        val isM2M: Boolean = run {
            val gty: String? = jwt.getClaimAsString("gty")
            when (gty) {
                null -> false
                else -> true
            }
        }

        val principal: ApiPrincipal = when (isM2M) {
            true -> m2mPrincipalFromAuth(authentication)
            false -> userPrincipalFromAuth(authentication)
        }

        return principal
    }

    fun m2mPrincipalFromAuth(authentication: JwtAuthenticationToken): ApiPrincipal.M2M {
        val jwt: Jwt = jwtFromAuth(authentication)

        val principal = ApiPrincipal.M2M(
            clientName = jwt.getClaimAsString(resourceServerConfig.m2mClientNameClaim) ?: "",
            sub = jwt.getClaimAsString("sub") ?: "",
            azp = jwt.getClaimAsString("azp") ?: "",
            gty = jwt.getClaimAsString("gty") ?: "",
            scopes = (jwt.getClaimAsString("scope") ?: "")
                .split(" ")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .sorted()
                .toSet(),
        )
        return principal
    }

    fun userPrincipalFromAuth(authentication: JwtAuthenticationToken): ApiPrincipal.User {
        val jwt: Jwt = (authentication.credentials as? Jwt)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "no jwt")
        val principal = ApiPrincipal.User(
            userId = jwt.getClaimAsString("$userClaimsNamespace/userid") ?: "",
            givenName = jwt.getClaimAsString("$userClaimsNamespace/given_name") ?: "",
            familyName = jwt.getClaimAsString("$userClaimsNamespace/family_name") ?: "",
            email = jwt.getClaimAsString("$userClaimsNamespace/email") ?: "",
            roles = (jwt.getClaimAsStringList(userRolesClaim) ?: emptyList())
                .sorted()
                .toSet(),
        )
        return principal
    }

    fun requireRolesOrScopesAnyOf(
        apiPrincipal: ApiPrincipal, rolesAnyOf: Set<String>, m2mScopesAnyOf: Set<String>
    ) {
        when (apiPrincipal) {
            is ApiPrincipal.M2M -> requireM2mScopesAnyOf(m2mPrincipal = apiPrincipal, scopes = m2mScopesAnyOf)
            is ApiPrincipal.User -> requireRolesAnyOf(apiUser = apiPrincipal, roles = rolesAnyOf)
        }
    }

    fun requireRolesAnyOf(apiUser: ApiPrincipal.User, roles: Set<String>) {
        val expected: Set<String> = roles
        if (expected.isEmpty()) return
        val given: Set<String> = apiUser.roles
        val matching: Set<String> = expected.intersect(given)
        if (matching.isNotEmpty()) {
            return
        }
        throw ResponseStatusException(HttpStatusCode.valueOf(403), "roles missing anyOf: $roles")
    }

    fun requireM2mScopesAnyOf(m2mPrincipal: ApiPrincipal.M2M, scopes: Set<String>) {
        val expected: Set<String> = scopes
        if (expected.isEmpty()) return
        val given: Set<String> = m2mPrincipal.scopes
        val matching: Set<String> = expected.intersect(given)
        if (matching.isNotEmpty()) {
            return
        }
        throw ResponseStatusException(HttpStatusCode.valueOf(403), "m2m scopes missing anyOf: $scopes")
    }


}