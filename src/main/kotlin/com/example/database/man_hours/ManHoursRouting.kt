package com.example.database.man_hours

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.ManHoursController() {
    routing {
        authenticate("auth-jwt") {
            route("/manhours") {
                post("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.insertManHours()
                    call.response.status(HttpStatusCode(201, "Man-hours created"))
                }

                get("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.fetchManHours()
                    call.respond(HttpStatusCode.OK)
                }

                get("specific/{manhoursid}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.fetchManHoursById()
                    call.respond(HttpStatusCode.OK)
                }

                put("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.updateManHours()
                    call.respond(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.deleteManHours()
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}