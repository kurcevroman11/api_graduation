package com.example.database.user

import com.example.database.Person.PersonModule.deletePerson
import com.example.database.user.UserModule.deleteUser
import com.example.database.user.UserModule.fetchUserID
import com.example.database.user.UserModule.updateUser
import com.example.db.UserRoleProject.UserRoleProjectModel.deleteUserURP
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.UserContriller() {
    routing {
        route("/User") {
            //Вывод определенного пользователя
            get("/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId != null) {
                    val user = fetchUserID(userId)
                    call.respond(user!!)
                }else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }
            //Обновление определенного пользователя
            put("/{id}") {

                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId != null) {
                    val user = call.receive<String>()
                    val gson = Gson()

                    val userDTO = gson.fromJson(user, UsersDTO::class.java)
                    call.respond(updateUser(userId, userDTO))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }
            //Удаление определенного пользователя
            delete("/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull()
                if (userId != null) {
                    deleteUser(userId)
                    deleteUserURP(userId)
                    deletePerson(userId)
                    call.respond(HttpStatusCode.OK, "Delete")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }
        }
    }
}