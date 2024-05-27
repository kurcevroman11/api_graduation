package com.example.db.Task

import com.example.database.Dependence.DependenceModel.getAllDependences
import com.example.database.Dependence.DependenceModel.getDependenceForDelete
import com.example.database.Dependence.DependenceModel.getDependences
import com.example.database.UserRoleProject.UserRoleProjectDTO
import com.example.database.man_hours.ManHoursModel
import com.example.db.Description.DescriptionModel.insert
import com.example.db.Task.TaskForId.insertandGetIdTask
import com.example.db.Task.TaskModel.addUserCount
import com.example.db.Task.TaskModel.collectAllTasks
import com.example.db.Task.TaskModel.deletTask
import com.example.db.Task.TaskModel.getDownTask
import com.example.db.Task.TaskModel.getParentId
import com.example.db.Task.TaskModel.getTask
import com.example.db.Task.TaskModel.getTaskById
import com.example.db.Task.TaskModel.getTaskByIdNotExecuter
import com.example.db.Task.TaskModel.recalculationScore
import com.example.db.Task.TaskModel.recalculationScoreWithDependence
import com.example.db.Task.TaskModel.recalculationScoreWithDependenceForDelete
import com.example.db.Task.TaskModel.updateTask
import com.example.db.UserRoleProject.UserRoleProjectModel
import com.example.plugins.createMedia
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.TaskContriller() {
    routing {
        authenticate("auth-jwt") {
            route("/task") {
                //Вывод определенного id
                get("/{id}") {
                    val taskId = call.parameters["id"]?.toIntOrNull()

                    val principle = call.principal<JWTPrincipal>()
                    val userId = principle!!.payload.getClaim("userId").asInt()

                    if (taskId != null) {
                        val taskByID = getTaskById(taskId, userId)
                        if(taskByID?.id == null) {
                            // Данная функция используется в том случае если задача не привязана к пользователю
                            val taskByID = getTaskByIdNotExecuter(taskId)
                            call.respond(taskByID!!)
                        }
                        call.respond(taskByID!!)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                route("/downtask"){
                    //Вывод не выполненных задач/подзадач
                    get("/unfulfilled/{id}") {
                        val taskId = call.parameters["id"]?.toIntOrNull()
                        if (taskId != null) {
                            val taskDTO = addUserCount(taskId, 2)
                            call.respond(taskDTO!!)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                        }
                    }

                    //Вывод не выполненных задач/подзадач
                    get("/completed/{id}") {
                        val taskId = call.parameters["id"]?.toIntOrNull()
                        if (taskId != null) {
                            val taskDTO = addUserCount(taskId, 1)
                            call.respond(taskDTO!!)
                        } else {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                        }
                    }
                }

                get("/taskdependence/{projectid}/{taskid}") {
                    val projectid = call.parameters["projectid"]?.toIntOrNull()
                    val taskid = call.parameters["taskid"]?.toIntOrNull()
                    if (taskid != null && projectid != null) {
                        val listTaskDTO = collectAllTasks(
                            projectId = projectid
                        )

                        val task = listTaskDTO.find { it.id == taskid }

                        // Удаление родителя
                        val parentTask = listTaskDTO.find { it.id == task?.parent }
                        listTaskDTO.removeIf { it.id == parentTask?.id }

                        // Удаление дочерненего элемента
                        val childTask = listTaskDTO.find { it.parent == task?.id }
                        listTaskDTO.removeIf { it.id == childTask?.id }

                        listTaskDTO.removeIf { it.id == taskid }

                        call.respond(listTaskDTO!!)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Создание задачи/подзадачи
                post("/{id}") {
                    val task = call.receive<String>()
                    val gson = Gson()
                    val taskId = call.parameters["id"]?.toInt()

                    if(taskId != null) {
                        var taskOrSubtask = gson.fromJson(task, TaskDTO::class.java)

                        // Увелечение поколения
                        var taskParent = getTask(taskId!!)
                        taskOrSubtask.generation = taskParent!!.generation!! + 1

                        val id = insertandGetIdTask(taskOrSubtask)
                        taskOrSubtask.parent = taskId
                        // Создание папки в которой будет храниться файлы
                        insert(id.toInt())
                        taskOrSubtask.status = 3 // В ожидании

                        updateTask(id.toInt(), taskOrSubtask)

                        updateTask(taskParent!!.id!!, taskParent!!)

                        val projectId = getParentId(id.toInt())
                        recalculationScore(projectId, taskOrSubtask.generation!!)
                        recalculationScoreWithDependence()



                        call.respond(HttpStatusCode.Created)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Создание проекта
                post {
                    val task = call.receive<String>()
                    val principle = call.principal<JWTPrincipal>()
                    val userId = principle!!.payload.getClaim("userId").asInt()
                    val gson = Gson()

                    val role = principle!!.payload.getClaim("role").asString()

                    if (role == "Админ" || role == "Проект-менеджер") {
                        val name = gson.fromJson(task, TaskDTO::class.java)
                        name.generation = 1
                        name.scope = 0

                        val id = insertandGetIdTask(name)

                        val userRoleProjectDTO: UserRoleProjectDTO? = UserRoleProjectDTO(
                            creater_project = userId.toInt(),
                            projectid = id.toInt()
                        )

                        if (userRoleProjectDTO != null) {
                            UserRoleProjectModel.insert(userRoleProjectDTO)
                        }

                        call.respond(HttpStatusCode.Created)
                    } else {
                        call.response.status(HttpStatusCode(403, "You aren't admin or project manager!"))
                    }
                }

                //Обновеление задачи
                put("update/{id}") {
                    val taskId = call.parameters["id"]?.toIntOrNull()

                    val task = call.receive<String>()
                    val gson = Gson()
                    val taskDTO = gson.fromJson(task, TaskDTO::class.java)
                    call.respond(updateTask(taskId!!, taskDTO))
                }

                //Удаление задачи
                delete("/{id}") {
                    val taskId = call.parameters["id"]?.toIntOrNull()
                    if (taskId != null) {
                        val projectId = getParentId(taskId)
                        val task = getTask(taskId)

                        ManHoursModel.deleteByTask(taskId)

                        //Перерасчет с времени с учетом зависимости
                        val dependence = getDependenceForDelete(taskId)
                        if(dependence != null) {
                            var dependentId2 = getDependenceForDelete(dependence?.dependent!!)
                            if(dependentId2 != null) {
                                var dependentTaskDTO2 = getTask(dependentId2?.dependent!!)
                                while(dependentId2 != null) {
                                    // Удаляемая привязанная задача
                                    val dependentOnTaskDTO = getTask(dependentId2.dependsOn!!)
                                    dependentTaskDTO2?.scope = dependentTaskDTO2?.scope!! - dependentOnTaskDTO?.scope!!

                                    // Обновление
                                    updateTask(dependentTaskDTO2.id!!, dependentTaskDTO2)

                                    // Возвраещается DependeOn
                                    dependentId2 = getDependenceForDelete(dependentId2.dependent)
                                }
                            } else {
                                val dependentOnTaskDTO = getTask(dependence?.dependsOn!!)
                                val dependentTaskDTO = getTask(dependence?.dependent!!)
                                dependentTaskDTO?.scope = dependentTaskDTO?.scope!! - dependentOnTaskDTO?.scope!!

                                // Обновление
                                updateTask(dependentTaskDTO.id!!, dependentTaskDTO)
                            }
                        }
                        call.respond(deletTask(taskId), "Delete")

                        // У задачи поле parent равно null будет вызвано искльчение
                        if(task?.parent != null)  {
                            // Если удаляется единственный дочерний элемент то родителю в score присвоить 0
                            if(getDownTask(task?.parent!!).isEmpty()) {
                                val parent = getTask(task?.parent!!)
                                parent?.scope = 0
                                updateTask(parent?.id!!, parent)
                                // Повторный перерасчет для обноления score у родительских элементов
                                recalculationScore(projectId, task?.generation!!)
                                recalculationScoreWithDependenceForDelete()
                            } else {
                                recalculationScore(projectId, task?.generation!!)
                                recalculationScoreWithDependenceForDelete()
                            }
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }
            }
        }
    }
}



