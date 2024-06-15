package com.example.db.UserRoleProject

import com.example.database.UserRoleProject.UserRoleProjectDTO
import com.example.db.Task.TaskModel.collectAllTasks
import com.example.db.UserRoleProject.UserRoleProjectModel.clearFieldCreateProject
import com.example.db.UserRoleProject.UserRoleProjectModel.deleteFromProject
import com.example.db.UserRoleProject.UserRoleProjectModel.deleteFromTask
import com.example.db.UserRoleProject.UserRoleProjectModel.fetchUserInProj
import com.example.db.UserRoleProject.UserRoleProjectModel.getNotification
import com.example.db.UserRoleProject.UserRoleProjectModel.getUserProject
import com.example.db.UserRoleProject.UserRoleProjectModel.insert
import com.example.db.UserRoleProject.UserRoleProjectModel.linkinUserRootTask
import com.example.db.UserRoleProject.UserRoleProjectModel.scheduling
import com.example.db.UserRoleProject.UserRoleProjectModel.updateURP
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Application.UserRoleProjectController() {
    routing {
        authenticate("auth-jwt") {
            route("/user_role_project") {
                // Запрос на получение проектов, в которых добавлен пользователь
                get("/project") {
                    val principle = call.principal<JWTPrincipal>()
                    val role = principle!!.payload.getClaim("role").asString()
                    val userId = principle!!.payload.getClaim("userId").asInt()
                    val serializedList = getUserProject(userId, role)

                    call.respond(serializedList!!)
                }

                get("/calendar_plan/{projId}") {
                    val projId = call.parameters["projId"]?.toIntOrNull()
                    if (projId != null) {
                        val serializedList = scheduling(projId)
                        call.respond(serializedList)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                get("/notification") {
                    val principle = call.principal<JWTPrincipal>()
                    val userId = principle!!.payload.getClaim("userId").asInt()
                    call.respond(HttpStatusCode.OK, getNotification(userId))
                }

                get("/excel/{projId}") {
                    val projId = call.parameters["projId"]?.toIntOrNull()

                    if (projId != null) {
                        val tasks = scheduling(projId)

                        val workbook: Workbook = XSSFWorkbook()
                        val sheet: Sheet = workbook.createSheet("Tasks")

                        val headerFont: Font = workbook.createFont().apply {
                            bold = true
                        }

                        val headerCellStyle: CellStyle = workbook.createCellStyle().apply {
                            setFont(headerFont)
                            fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
                            fillPattern = FillPatternType.SOLID_FOREGROUND
                            borderBottom = BorderStyle.THIN
                            borderTop = BorderStyle.THIN
                            borderLeft = BorderStyle.THIN
                            borderRight = BorderStyle.THIN
                        }

                        val dateCellStyle: CellStyle = workbook.createCellStyle().apply {
                            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
                            fillPattern = FillPatternType.SOLID_FOREGROUND
                            borderBottom = BorderStyle.THIN
                            borderTop = BorderStyle.THIN
                            borderLeft = BorderStyle.THIN
                            borderRight = BorderStyle.THIN
                        }

                        val headerRow: Row = sheet.createRow(0)

                        // Получение всех уникальных дат
                        val dates = tasks.flatMap { it.execution_date }.map {
                            LocalDate.parse(it.substring(0, 10), DateTimeFormatter.ISO_DATE)
                        }.distinct().sorted()

                        // Создание заголовков для дат
                        headerRow.createCell(0).apply {
                            setCellValue("Task Name")
                            cellStyle = headerCellStyle
                        }

                        dates.forEachIndexed { index, date ->
                            val cell = headerRow.createCell(index + 1)
                            cell.setCellValue(date.toString())
                            cell.cellStyle = headerCellStyle
                        }

                        // Заполнение данных задач
                        tasks.forEachIndexed { rowIndex, task ->
                            val row = sheet.createRow(rowIndex + 1)
                            row.createCell(0).apply {
                                setCellValue(task.nameTask)
                                cellStyle = dateCellStyle
                            }

                            task.execution_date.forEach { execDate ->
                                val date = LocalDate.parse(execDate.substring(0, 10), DateTimeFormatter.ISO_DATE)
                                val colIndex = dates.indexOf(date) + 1
                                val cell = row.createCell(colIndex)
                                cell.cellStyle = dateCellStyle
                            }
                        }

                        // Автоматическая регулировка ширины столбцов
                        for (i in 0..dates.size) {
                            sheet.autoSizeColumn(i)
                        }

                        // Записываем файл в ByteArrayOutputStream
                        val outputStream = ByteArrayOutputStream()
                        workbook.write(outputStream)
                        workbook.close()

                        val byteArray = outputStream.toByteArray()

                        // Отправляем файл клиенту
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "calendar_plan_$projId.xlsx").toString()
                        )

                        // Отправляем файл клиенту
                        call.respondBytes(byteArray, ContentType.Application.OctetStream, HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Привязка пользователей в проекте или к задаче
                post {
                    val principle = call.principal<JWTPrincipal>()
                    val roles = principle!!.payload.getClaim("role").asString()

                    if(roles == "Админ" || roles == "Проектный менеджмент") {
                        val urp = call.receive<String>()
                        val gson = Gson()

                        val getData = gson.fromJson(urp, UserRoleProjectDTO::class.java)

                        if (getData.projectid != null || getData.current_task_id != null) {
                            // Привязка пользователя к проекту
                            if (getData.projectid != null) {
                                val usersInProj = fetchUserInProj(getData.userid, getData.projectid!!)
                                if (usersInProj.isNotEmpty()) {
                                    call.respond(HttpStatusCode.Conflict, "User already added")
                                } else {
                                    insert(getData)
                                }
                                call.respond(HttpStatusCode.Created)
                                // Очистка поля creater_project, так как не только создатель привязан к проекту
                                clearFieldCreateProject(getData.projectid!!)
                                // Привязка пользователя к задаче
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

                // Удаление пользователя из проекта
                delete("fromproject/{projectId}/{personid}") {
                    val personId = call.parameters["personid"]?.toIntOrNull()
                    val projectId = call.parameters["projectId"]?.toIntOrNull()
                    if (personId != null && projectId != null) {
                        val tasks = collectAllTasks(projectId)
                        if(tasks.isNotEmpty()) {
                            // Сначала удаляем пользователей из задачи
                            tasks.forEach { task ->
                                deleteFromTask(personId, task.id!!)
                            }
                            // Затем из проекта
                            deleteFromProject(personId, projectId)
                        } else {
                            // Удаление если у проекта не было задач
                            deleteFromProject(personId, projectId)
                        }
                        call.respond(OK, "Delete")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                // Удаление пользователя из задачи
                delete("fromtask/{taskId}/{personid}") {
                    val personId = call.parameters["personid"]?.toIntOrNull()
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                    if (personId != null && taskId != null) {
                        call.respond(deleteFromTask(personId, taskId), "Delete")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }
            }
        }
    }
}