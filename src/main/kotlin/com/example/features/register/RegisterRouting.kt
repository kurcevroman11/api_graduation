package com.example.features.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun Application.configureRegisterRouting() {
    routing {
        authenticate("auth-jwt"){
            post("/register") {
                val principle = call.principal<JWTPrincipal>()
                val role = principle!!.payload.getClaim("role").asString()

                if(role == "Админ" || role == "Проект-менеджер") {
                    val registerController = RegisterController(call)
                    registerController.registerNewUser()
                    call.response.status(HttpStatusCode(201, "User created"))
                }
                else{
                    call.response.status(HttpStatusCode(403, "You aren't admin or project manager!"))
                }
            }
        }
    }
}

