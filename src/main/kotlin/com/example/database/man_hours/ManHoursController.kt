package com.example.database.man_hours

import com.example.database.man_hours.ManHoursModel.delete
import com.example.database.man_hours.ManHoursModel.fetchById
import com.example.database.man_hours.ManHoursModel.fetchByProjectId
import com.example.database.man_hours.ManHoursModel.fetchSpecific
import com.example.database.man_hours.ManHoursModel.insert
import com.example.database.man_hours.ManHoursModel.update
import com.example.db.Task.TaskModel.getParentId
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.time.OffsetDateTime

class ManHoursController(val call: ApplicationCall) {
    suspend fun insertManHours(){
        val taskId = call.parameters["id"]?.toInt()

        val manHours = call.receive<String>()
        val gson = Gson()

        var manHoursDTO = gson.fromJson(manHours, ManHoursDTO::class.java)
        if(taskId != null){
            manHoursDTO.taskid = taskId
            manHoursDTO.hours_spent = formatTime(manHoursDTO.hours_spent ?: "")
            manHoursDTO.projectid = getParentId(taskId)
            insert(manHoursDTO)
            call.respond(HttpStatusCode.Created, "Man-hours created")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun fetchManHours(){
        val taskId = call.parameters["id"]?.toInt()

        if(taskId != null){
            call.respond(fetchById(taskId))
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun fetchManHoursById(){
        val manHoursId = call.parameters["manhoursid"]?.toInt()

        if(manHoursId != null){
            call.respond(fetchSpecific(manHoursId))
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun updateManHours(){
        val manHoursId = call.parameters["id"]?.toInt()

        val manHours = call.receive<String>()
        val gson = Gson()

        var manHoursDTO = gson.fromJson(manHours, ManHoursDTO::class.java)
        if(manHoursId != null){
            update(manHoursDTO, manHoursId)
            call.respond(HttpStatusCode.Created, "Man-hours created")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun getReport() {
        val manHoursId = call.parameters["projId"]?.toInt()

        if(manHoursId != null){
            val manHour = fetchByProjectId(manHoursId)
            val aggregatedHours = aggregateHoursSpent(manHour)
            call.respond(aggregatedHours)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    fun parseDuration(time: String?): Duration {
        if (time == null || time.isEmpty()) return Duration.ZERO
        val parts = time.split(":")
        val hours = parts[0].toLong()
        val minutes = parts[1].toLong()
        return Duration.ofHours(hours).plusMinutes(minutes)
    }

    fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        return "%02d:%02d".format(hours, minutes)
    }

    fun aggregateHoursSpent(manHours: List<ManHoursReportDTO>): List<ManHoursReportDTO> {
        return manHours
            .filter { it.createdAt != null && it.hoursSpent != null }
            .groupBy { OffsetDateTime.parse(it.createdAt).toLocalDate().toString() }
            .map { (date, entries) ->
                val totalDuration = entries
                    .map { parseDuration(it.hoursSpent) }
                    .reduce { acc, duration -> acc.plus(duration) }
                ManHoursReportDTO(
                    id = entries.first().id, // или другой логики для id
                    createdAt = date,
                    hoursSpent = formatDuration(totalDuration),
                    taskId = entries.first().taskId,
                    taskName = entries.first().taskName
                )
            }
    }

    // Функция для создания Excel файла
    suspend fun createExcelFile(projectId: Int, outputPath: String) {
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

        FileOutputStream(File(outputPath)).use { fileOut ->
            workbook.write(fileOut)
        }
        workbook.close()
    }

    suspend fun deleteManHours() {
        val manHoursId = call.parameters["id"]?.toInt()

        if(manHoursId != null){
            delete(manHoursId)
            call.respond(HttpStatusCode.OK, "Deleted")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    fun formatTime(input: String): String {
        // Убедитесь, что входная строка имеет длину 4
        require(input.length == 4) { "Input string must be 4 characters long" }

        // Извлечь часы и минуты из строки
        val hours = input.substring(0, 2)
        val minutes = input.substring(2, 4)

        // Вернуть форматированное время в формате "HH:MM"
        return "$hours:$minutes"
    }
}