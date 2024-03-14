# spring-boot-3.2-playground


```

$ sdk use java 21.0.2-tem

jvm args: -Dspring.profiles.active=local

env vars:

          resourceserver.jwt: ...
              issuer-uri: "${JWT_ISSUER_URI}"
              audiences: "${JWT_AUDIENCE}"
              userClaimsNamespace: ${JWT_USER_CLAIMS_NAMESPACE:"https://example.com/claims"}
              userRolesClaim: ${JWT_USER_ROLES_CLAIM:"https://my-app.example/roles"}
              m2mClientNameClaim: ${JWT_M2M_CLIENT_NAME_CLAIM:"https://foo.example.com/client_name"}


swagger-ui: http://localhost:8080/swagger-ui/index.html


```