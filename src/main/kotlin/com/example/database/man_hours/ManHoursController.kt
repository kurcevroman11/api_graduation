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
            .groupBy { Pair(OffsetDateTime.parse(it.createdAt).toLocalDate().toString(), it.taskId) }
            .map { (key, entries) ->
                val (date, taskId) = key
                val totalDuration = entries
                    .map { parseDuration(it.hoursSpent) }
                    .reduce { acc, duration -> acc.plus(duration) }
                ManHoursReportDTO(
                    id = entries.first().id, // Можно изменить логику определения id
                    createdAt = date,
                    hoursSpent = formatDuration(totalDuration),
                    taskId = taskId,
                    taskName = entries.first().taskName
                )
            }
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