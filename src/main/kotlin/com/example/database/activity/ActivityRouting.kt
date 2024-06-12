package com.example.database.activity

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.bouncycastle.asn1.x500.style.RFC4519Style.description

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