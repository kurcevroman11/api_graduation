package com.example.db.UserRoleProject

import com.example.database.UserRoleProject.UserRoleProjectDTO
import com.example.db.UserRoleProject.UserRoleProjectModel.clearFieldCreateProject
import com.example.db.UserRoleProject.UserRoleProjectModel.deleteURP
import com.example.db.UserRoleProject.UserRoleProjectModel.fetchUserInProj
import com.example.db.UserRoleProject.UserRoleProjectModel.getALLUserProject
import com.example.db.UserRoleProject.UserRoleProjectModel.getTask_executors
import com.example.db.UserRoleProject.UserRoleProjectModel.getUserProject
import com.example.db.UserRoleProject.UserRoleProjectModel.insert
import com.example.db.UserRoleProject.UserRoleProjectModel.linkinUserRootTask
import com.example.db.UserRoleProject.UserRoleProjectModel.scheduling
import com.example.db.UserRoleProject.UserRoleProjectModel.updateURP
import com.example.pluginsimport.Excel
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.UserRoleProjectController() {
    routing {
        authenticate("auth-jwt") {
            route("/user_role_project") {
                // Запрос на получение проектов, в которых добавлен пользователь
                get("/project") {
                    val principle = call.principal<JWTPrincipal>()
                    val userId = principle!!.payload.getClaim("userId").asInt()
                    val serializedList = getUserProject(userId)

                    call.respond(serializedList!!)
                }

                get("/task_executors") {
                    getTask_executors()
                    call.respond(HttpStatusCode.Created)
                }

                get("/calendar_plan") {
                    val serializedList = scheduling()
                    call.respond(serializedList)
                }

                get("/excel") {
                    val ex = Excel()
                    ex.writeExcel("C:\\Users\\386\\OneDrive\\Документы\\Сайт\\plan.xlsx")
                    call.respond(HttpStatusCode.OK)
                }

                get("/task/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val gson = Gson()
                    if (id != null) {
                        val URPDTO = getALLUserProject(id)
                        call.respond(gson.toJson(URPDTO))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Привязка пользователей в проекте или к задаче
                post {
                    val principle = call.principal<JWTPrincipal>()
                    val roles = principle!!.payload.getClaim("role").asString()

                    if(roles == "Админ" || roles == "Проект-менеджер") {
                        val urp = call.receive<String>()
                        val gson = Gson()

                        val getData = gson.fromJson(urp, UserRoleProjectDTO::class.java)

                        if (getData.projectid != null || getData.current_task_id != null) {
                            // Привязка пользователя к проекту
                            if (getData.projectid != null) {
                                val usersInProj = fetchUserInProj(getData.userid, getData.projectid!!)
                                if (usersInProj.isNotEmpty()) {
                                    call.respond(HttpStatusCode.Conflict, "User already added")
                                    // Очистка поля creater_project, так как не только создатель привязан к проекту
                                    clearFieldCreateProject(getData.projectid)
                                } else {
                                    insert(getData)
                                }
                                call.respond(HttpStatusCode.Created)
                            } else if(getData.current_task_id != null) {
                                linkinUserRootTask(getData)
                                call.respond(HttpStatusCode.Created)
                            }

                        } else {
                            call.response.status(HttpStatusCode(403, "You aren't admin or project manager!"))
                        }
                    }
                }

                put("/{id}") {
                    val urpId = call.parameters["id"]?.toIntOrNull()
                    if (urpId != null) {
                        val urp = call.receive<String>()
                        val gson = Gson()

                        val principle = call.principal<JWTPrincipal>()
                        val userId = principle!!.payload.getClaim("userId").asInt()

                        val URPDTO = gson.fromJson(urp, UserRoleProjectDTO::class.java)
                        call.respond(updateURP(urpId, userId, URPDTO))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                delete("/{id}") {
                    val URPId = call.parameters["id"]?.toIntOrNull()
                    if (URPId != null) {
                        call.respond(deleteURP(URPId), "Delete")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }
            }
        }
    }
}