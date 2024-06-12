package com.example.database.Person

import com.example.database.Person.PersonModule.fetchPersonBynIds
import com.example.database.Person.PersonModule.fetchPersonsByTask
import com.example.database.Person.PersonModule.getUserInProj
import com.example.database.user.UserModule
import com.example.db.UserRoleProject.UserRoleProjectModel.fetchFreeUsers
import com.example.db.UserRoleProject.UserRoleProjectModel.fetchFreeUsersTask
import com.example.db.UserRoleProject.UserRoleProjectModel.fetchUserInProj
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.PersonContriller() {
    routing {
        authenticate("auth-jwt") {
            route("/person") {
                // Запрос для отображение всех пользователей
                get {
                    val personDTO = PersonModule.fetchAllPerson()
                    call.respond(personDTO)
                }

                // Запрос для отображение тех пользователей которых можно добавить в проект
                get("/freeperson/{projId}") {
                    val projId = call.parameters["projId"]?.toIntOrNull()
                    if(projId != null) {
                        val users = fetchFreeUsers(projId)
                        call.respond(users ?: mutableListOf())
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Запрос для отображение тех пользователей которых можно добавить в задачу
                get("/freepersontask/{taskId}") {
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                    if(taskId != null) {
                        val users = fetchFreeUsersTask(taskId)
                        call.respond(users ?: mutableListOf())
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Запрос для отображение тех пользователей которые есть в определенной задаче
                get("/personintask/{taskId}") {
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                    if(taskId != null) {
                        val users = fetchPersonsByTask(taskId)
                        call.respond(users)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Запрос для отображение тех пользователей которые есть в определенном проекте
                get("/personinproject/{projectId}") {
                    val projectId = call.parameters["projectId"]?.toIntOrNull()
                    if(projectId != null) {
                        val users = getUserInProj(projectId)
                        call.respond(users)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Создание нового пользователя
                post {
                    val person = call.receive<String>()
                    val gson = Gson()

                    val personDTO = gson.fromJson(person, PersonDTO::class.java)
                    PersonModule.insertPerson(personDTO)
                    call.respond(HttpStatusCode.Created)
                }

                delete("/{id}") {
                    val personId = call.parameters["id"]?.toIntOrNull()
                    if (personId != null) {
                        call.respond(PersonModule.deletePerson(personId), "Delete")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }
            }
        }
    }
}
