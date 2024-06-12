package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.config.*
import java.util.*

val dotenv = Dotenv.configure().directory("/app").load()


class TokenManager(config: ApplicationConfig) {
    val issuer = config.property("jwt.issuer").getString()
    val secret = config.property("jwt.secret").getString()
    val audience = config.property("jwt.audience").getString()
    val expiration: Long = 2592000000  // 30 дней в миллисекундах


    fun generateToken(userId: Int, role: String): String {
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("role", role)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(Algorithm.HMAC256(secret))
        return token
    }

    fun verifyJWTToken(): JWTVerifier {
        return JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}