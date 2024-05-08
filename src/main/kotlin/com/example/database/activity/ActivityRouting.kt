package com.example.database.activity

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.ActivityController() {
    routing {
        authenticate("auth-jwt") {
            route("/activity") {
                get() {
                    val activity = ActivityController(call)
                    activity.getAllActivity()
                }
            }
        }
    }
}