package com.example.features.login

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.routing.*

fun Route.configureLoginRouting(config: ApplicationConfig) {
    post("/login") {
        val loginController = LoginController(call, config)
        loginController.performLogin()
    }
}