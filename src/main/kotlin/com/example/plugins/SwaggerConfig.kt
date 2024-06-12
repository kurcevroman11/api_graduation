package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*


fun Application.configureSwagger() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "/app/openapi/documentation.yaml")
    }
}