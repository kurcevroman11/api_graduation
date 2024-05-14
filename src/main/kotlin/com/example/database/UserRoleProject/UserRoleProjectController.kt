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
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream

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

                get("/calendar_plan/{projId}") {
                    val projId = call.parameters["projId"]?.toIntOrNull()
                    if (projId != null) {
                        val serializedList = scheduling(projId)
                        call.respond(serializedList)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                }

                get("/excel/{projId}") {
                    val projId = call.parameters["projId"]?.toIntOrNull()

                    if (projId != null) {
                        val workbook = XSSFWorkbook()
                        val sheet = workbook.createSheet("Sheet1")

                        val cellStyle = workbook.createCellStyle()
                        val color = IndexedColors.GREEN.getIndex()
                        cellStyle.setFillForegroundColor(color)
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

                        // Создание заголовка
                        val headerRow = sheet.createRow(0)

                        for (header in 1..120) {
                            val cell = headerRow.createCell(header)
                            cell.setCellValue(header.toString() + " день")
                        }

                        // Метод, который выводить словарь, где ключ - название задача, а
                        // значение кол-во дней выполнения задания
                        val calendarPlan = UserRoleProjectModel.scheduling(projId)

                        val data = calendarPlan

                        for ((rowIndex, rowData) in data.withIndex()) {
                            val row = sheet.createRow(rowIndex + 1)

                            val cell = row.createCell(0)
                            cell.setCellValue(rowData.nameTask)
                            for (i in 1..rowData.execution) {
                                val cellExecution = row.createCell(i + rowData.start)
                                cellExecution.setCellValue(" ")
                                cellExecution.cellStyle = cellStyle
                            }
                        }


                        val headerStyle = workbook.createCellStyle()
                        headerStyle.alignment = HorizontalAlignment.CENTER
                        val headerFont = workbook.createFont()
                        headerFont.bold = true
                        headerStyle.setFont(headerFont)
                        val cell = headerRow.createCell(0)
                        cell.cellStyle = headerStyle
                        cell.setCellValue("Task/Day") // Установка значения ячейки
                        sheet.setColumnWidth(0, 12 * 256) // Установка ширины столбцов
                        headerRow.getCell(0).cellStyle = headerStyle


                        sheet.setColumnWidth(0, 12 * 256) // Установка ширины столбца

                        // Записываем файл в ByteArrayOutputStream
                        val outputStream = ByteArrayOutputStream()
                        workbook.write(outputStream)
                        workbook.close()

                        val byteArray = outputStream.toByteArray()

                        // Отправляем файл клиенту
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "project_$projId.xlsx").toString()
                        )

                        // Отправляем файл клиенту
                        call.respondBytes(byteArray, ContentType.Application.OctetStream, HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
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
                                } else {
                                    insert(getData)
                                }
                                call.respond(HttpStatusCode.Created)
                                // Очистка поля creater_project, так как не только создатель привязан к проекту
                                clearFieldCreateProject(getData.projectid)
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