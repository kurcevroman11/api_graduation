package com.example.plugins

import com.example.features.login.configureLoginRouting
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(config: ApplicationConfig) {
    routing {
        configureLoginRouting(config)
    }
}