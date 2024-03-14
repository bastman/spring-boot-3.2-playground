package com.example.demo.config.oauth2.resourceserver

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.security.oauth2.resourceserver.jwt")
data class OAuth2ResourceServerConfig(
    val userClaimsNamespace: String,
    val userRolesClaim: String,
    val m2mClientNameClaim: String,
)