package com.example.database.man_hours

import com.example.database.man_hours.ManHoursModel.delete
import com.example.database.man_hours.ManHoursModel.fetchById
import com.example.database.man_hours.ManHoursModel.fetchSpecific
import com.example.database.man_hours.ManHoursModel.insert
import com.example.database.man_hours.ManHoursModel.update
import com.example.db.Task.TaskModel.getParentId
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

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
            fetchById(taskId)
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