package com.example.integrations

import com.example.configIntegrationTestApp
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class LoginRoutingTest {
    @Test
    fun testPerformLogin() = testApplication {
        configIntegrationTestApp()
        val jsonBody = """{"login": "projecter9", "password": "00"}"""
        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        val test = response.bodyAsText()
        println(test)
        assertEquals(HttpStatusCode.OK, response.status)
    }
}