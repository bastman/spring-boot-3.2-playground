package com.example.demo.api.debug

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import io.swagger.v3.oas.annotations.media.Schema

// see: https://github.com/swagger-api/swagger-core/issues/4156
private const val OA_SCHEMA_NAME = "MySealedPayloadExampleType"

@Schema(
    description = "IDPProvider. idpProviderType is one of 'AUTH0', 'ENTRA'",
    name = OA_SCHEMA_NAME,
    oneOf = [SealedPayloadExample.Auth0::class, SealedPayloadExample.Entra::class],
    discriminatorProperty = "idpProviderType"
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "idpProviderType")
sealed class SealedPayloadExample() {
    abstract val someCommonField: String?
    abstract val idpProviderType: IDPProviderType

    @JsonTypeName("AUTH0")
    @Schema(name = "${OA_SCHEMA_NAME}_AUTH0")
    data class Auth0(
        val auth0Tenant: String,
        val userId: String,
        override val someCommonField: String?
    ) : SealedPayloadExample() {
        override val idpProviderType = IDPProviderType.AUTH0
    }

    @JsonTypeName("ENTRA")
    @Schema(name = "${OA_SCHEMA_NAME}_ENTRA")
    data class Entra(
        val entraTenant: String,
        val userId: String,
        val onPremId: String?,
        override val someCommonField: String?
    ) : SealedPayloadExample() {
        override val idpProviderType = IDPProviderType.ENTRA
    }

    enum class IDPProviderType {
        AUTH0, ENTRA;
    }
}