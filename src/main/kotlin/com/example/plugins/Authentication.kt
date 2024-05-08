package com.example.plugins

import com.example.utils.TokenManager
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*

fun Application.configureAuthentication(config: ApplicationConfig) {
    val tokenManager = TokenManager(config)
    val myRealm = config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(tokenManager.verifyJWTToken())
            realm = myRealm
            validate { jwtCredential ->
                if(jwtCredential.payload.getClaim("role").asString().isNotEmpty()) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }
    }
}