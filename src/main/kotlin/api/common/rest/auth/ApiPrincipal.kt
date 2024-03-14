package com.example.demo.api.common.rest.auth

sealed class ApiPrincipal {

    data class User(
        val userId: String,
        val givenName: String,
        val familyName: String,
        val email: String,
        val roles: Set<String>,
    ) : ApiPrincipal()

    data class M2M(
        val clientName: String,
        val scopes: Set<String>,
        val sub: String,
        val azp: String,
        val gty: String,
    ) : ApiPrincipal()
}