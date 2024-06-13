package com.example.features.login

import com.example.database.Person.PersonModule
import com.example.database.user.UserModule
import com.example.database.user.UserModule.fetchRole
import com.example.features.register.RegisterResponseRemote
import com.example.utils.TokenManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LoginController(private val call: ApplicationCall, private val config: ApplicationConfig) {
    suspend fun performLogin() {
        val receive = call.receive<LoginReceiveRemote>()
        val userDTO = UserModule.fetchUser(receive.login)

        if(userDTO == null){
            call.respond(HttpStatusCode.BadRequest, "User not found")
        } else {
            if(userDTO.password == receive.password){
                val tokenManager = TokenManager(config)

                val role = fetchRole(receive.login)
                val personDTO = PersonModule.fetchPersonID(userDTO.personId!!)
                val tokenLong = tokenManager.generateToken(personDTO?.id!!, role)
                transaction {
                    addLogger(StdOutSqlLogger)
                }

                var adminOrProjectManager = false
                if(role == "Админ" || role == "Проектный менеджмент"){
                    adminOrProjectManager = true
                }

                call.respond(
                    RegisterResponseRemote( tokenLong = tokenLong, adminOrProjectManager = adminOrProjectManager)
                )
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }
    }
}