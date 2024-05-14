package com.example.database.man_hours

import com.example.database.man_hours.ManHoursModel.fetchByProjectId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.File

fun Application.ManHoursController() {
    routing {
        authenticate("auth-jwt") {
            route("/manhours") {
                post("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.insertManHours()
                    call.response.status(HttpStatusCode(201, "Man-hours created"))
                }

                get("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.fetchManHours()
                    call.respond(HttpStatusCode.OK)
                }

                get("specific/{manhoursid}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.fetchManHoursById()
                    call.respond(HttpStatusCode.OK)
                }

                get("report/{projId}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.getReport()
                    call.respond(HttpStatusCode.OK)
                }

                get("excelreport/{projId}") {
                    val projectId = call.parameters["projId"]?.toIntOrNull()

                    if (projectId != null) {
                        val data = fetchByProjectId(projectId)

                        // Группируем данные по taskName и createdAt
                        val taskNames = data.map { it.taskName }.distinct()
                        val createdDates = data.mapNotNull { it.createdAt }.distinct().sorted()

                        val dataMap = mutableMapOf<String, MutableMap<String, String?>>()
                        taskNames.forEach { taskName ->
                            dataMap[taskName] = mutableMapOf()
                            createdDates.forEach { date ->
                                dataMap[taskName]!![date] = data.find { it.taskName == taskName && it.createdAt == date }?.hoursSpent
                            }
                        }

                        val workbook = XSSFWorkbook()
                        val sheet = workbook.createSheet("Man Hours Report")

                        val headerCellStyle = workbook.createCellStyle().apply {
                            setFont(workbook.createFont().apply {
                                bold = true
                            })
                            alignment = HorizontalAlignment.CENTER
                        }

                        // Создаем строку заголовка
                        val headerRow = sheet.createRow(0)
                        headerRow.createCell(0).apply {
                            setCellValue("Task Name")
                            cellStyle = headerCellStyle
                        }

                        createdDates.forEachIndexed { index, date ->
                            val cell = headerRow.createCell(index + 1)
                            cell.setCellValue(date)
                            cell.cellStyle = headerCellStyle
                        }

                        // Заполняем данные
                        taskNames.forEachIndexed { rowIndex, taskName ->
                            val row = sheet.createRow(rowIndex + 1)
                            row.createCell(0).setCellValue(taskName)

                            createdDates.forEachIndexed { colIndex, date ->
                                val cell = row.createCell(colIndex + 1)
                                cell.setCellValue(dataMap[taskName]?.get(date))
                            }
                        }

                        // Записываем файл в ByteArrayOutputStream
                        val outputStream = ByteArrayOutputStream()
                        workbook.write(outputStream)
                        workbook.close()

                        val byteArray = outputStream.toByteArray()

                        // Отправляем файл клиенту
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "project_man-hours_$projectId.xlsx").toString()
                        )

                        // Отправляем файл клиенту
                        call.respondBytes(byteArray, ContentType.Application.OctetStream, HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid project ID.")
                    }
                }

                put("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.updateManHours()
                    call.respond(HttpStatusCode.OK)
                }

                delete("/{id}") {
                    val manHoursController = ManHoursController(call)
                    manHoursController.deleteManHours()
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}