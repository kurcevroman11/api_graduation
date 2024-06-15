package com.example.database.man_hours

import com.example.database.man_hours.ManHoursModel.fetchByProjectId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

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
                        // Пример списка
                        val manHoursReportDTOList = fetchByProjectId(projectId)
                        manHoursReportDTOList.forEach { item ->
                            item.createdAt = item.createdAt?.let {
                                val offsetDateTime = OffsetDateTime.parse(it)
                                val localDateTime = offsetDateTime.toLocalDateTime()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                localDateTime.format(formatter)
                            }
                        }

                        val taskNames = manHoursReportDTOList.map { it.taskName }.distinct()
                        val createdDates = manHoursReportDTOList.mapNotNull { it.createdAt }.distinct().sorted()

                        val dataMap = mutableMapOf<String, MutableMap<String, String?>>()
                        taskNames.forEach { taskName ->
                            dataMap[taskName] = mutableMapOf()
                            createdDates.forEach { date ->
                                dataMap[taskName]!![date] = manHoursReportDTOList.find { it.taskName == taskName && it.createdAt == date }?.hoursSpent
                            }
                        }

                        val workbook = XSSFWorkbook()
                        val sheet = workbook.createSheet("Man Hours Report")

                        val headerCellStyle = workbook.createCellStyle().apply {
                            setFont(workbook.createFont().apply {
                                bold = true
                            })
                            alignment = HorizontalAlignment.CENTER
                            borderBottom = BorderStyle.THIN
                            borderLeft = BorderStyle.THIN
                            borderRight = BorderStyle.THIN
                            borderTop = BorderStyle.THIN
                        }

                        val dataCellStyle = workbook.createCellStyle().apply {
                            borderBottom = BorderStyle.THIN
                            borderLeft = BorderStyle.THIN
                            borderRight = BorderStyle.THIN
                            borderTop = BorderStyle.THIN
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
                            row.createCell(0).apply {
                                setCellValue(taskName)
                                cellStyle = dataCellStyle
                            }

                            createdDates.forEachIndexed { colIndex, date ->
                                val cell = row.createCell(colIndex + 1)
                                cell.setCellValue(dataMap[taskName]?.get(date))
                                cell.cellStyle = dataCellStyle
                            }
                        }

                        // Автоматическая регулировка ширины столбцов
                        for (i in 0..createdDates.size) {
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