package com.example.database.Status

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.StatusContriller() {
    routing {
        route("/status") {
            get {
                val statusDTO = StatusModel.getStatusAll()
                val gson = Gson()

                val description = gson.toJson(statusDTO)

                call.respond(description)
            }
        }
    }
}