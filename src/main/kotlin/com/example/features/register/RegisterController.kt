package com.example.features.register

import com.example.database.Person.PersonDTO
import com.example.database.Person.PersonForUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import  com.example.database.user.UserModule
import com.example.database.user.UsersDTO
import io.ktor.server.request.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class RegisterController(val call: ApplicationCall) {
    suspend fun registerNewUser(){
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()
        val words = registerReceiveRemote.fio.split(" ")

        val userDTO = UserModule.fetchUser(registerReceiveRemote.login)
        if(userDTO != null){
            call.respond(HttpStatusCode.Conflict, "User already exists")
        }
        else if(words.size < 2) {
            call.respond(HttpStatusCode.BadRequest, "Not fio")
        }
        else {
            transaction {
                addLogger(StdOutSqlLogger)

                val person = PersonDTO(
                    id = null,
                    surname = "",
                    name = "",
                    patronymic = "",
                    role = ""
                )

                when (words.size) {
                    2 -> {
                        person.surname = words[0]
                        person.name = words[1]
                    }
                    3 -> {
                        person.surname = words[0]
                        person.name = words[1]
                        person.patronymic = words[2]

                    }
                    else -> {
                        println("Строка не содержит двух или трех слов")
                    }
                }
                person.role = registerReceiveRemote.role

                val personId: Int? = PersonForUser.insertAndGetId(person).toInt()

                UserModule.insert(
                    UsersDTO(
                        id = null,
                        login = registerReceiveRemote.login,
                        password = registerReceiveRemote.password,
                        personId = personId
                    )
                )
            }
        }
        call.respondText("User creat")
        call.respond(HttpStatusCode.OK)
    }
}