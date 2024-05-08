package com.example.database.file

import com.example.database.Person.PersonModule
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.FileContriller() {
    routing {
        authenticate("auth-jwt") {
            route("/file") {
                get("/{id}") {
                    val personId = call.parameters["id"]?.toIntOrNull()
                    if (personId != null) {
                        val personDTO = PersonModule.fetchPersonID(personId)
                        call.respond(personDTO!!)
                    }else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }
            }
        }
    }
}