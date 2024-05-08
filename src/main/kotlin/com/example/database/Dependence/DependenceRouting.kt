package com.example.database.Dependence

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.DependenceController() {
    routing {
        authenticate("auth-jwt") {
            route("/dependence") {
                post {
                    val dependenceController = DependenceController(call)
                    dependenceController.insertDependence()
                    call.response.status(HttpStatusCode(201, "Dependence created"))
                }

                delete("/{dependsOn}") {
                    val dependenceController = DependenceController(call)
                    dependenceController.delete()
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}