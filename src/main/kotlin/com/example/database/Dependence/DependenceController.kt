package com.example.database.Dependence

import com.example.database.Dependence.DependenceModel.addDependence
import com.example.database.Dependence.DependenceModel.deleteDependence
import com.example.database.Dependence.DependenceModel.getDependenceForDelete
import com.example.database.Dependence.DependenceModel.getDependences
import com.example.db.Task.TaskDTO
import com.example.db.Task.TaskModel.getParentId
import com.example.db.Task.TaskModel.getTask
import com.example.db.Task.TaskModel.recalculationScore
import com.example.db.Task.TaskModel.updateTask
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class DependenceController(val call: ApplicationCall) {
    suspend fun insertDependence(){
        val dependence = call.receive<String>()
        val gson = Gson()

        var dependenceDTO = gson.fromJson(dependence, Dependence::class.java)
        if(dependenceDTO.dependent != null && dependenceDTO.dependsOn != null){
            addDependence(dependenceDTO)
            var dependentTaskDTO = getTask(dependenceDTO.dependent!!)

            // Извленчение задач от которых зависит dependentTaskDTO
            val dependsOnTaskDTO: TaskDTO? = getTask(dependenceDTO.dependsOn)

            // Суммирование время выполнения задачи
            dependentTaskDTO?.scope = dependsOnTaskDTO?.scope!! + dependentTaskDTO?.scope!!

            if(dependentTaskDTO?.id != null) {
                // Обновление
                updateTask(dependentTaskDTO.id!!, dependentTaskDTO)
                val projectId = getParentId(dependentTaskDTO?.id!!)
                // Перерасчет графа проекта
                recalculationScore(projectId, dependentTaskDTO?.generation!! - 1)

                // Обновление
                updateTask(dependentTaskDTO.id!!, dependentTaskDTO)
            }

            call.respond(HttpStatusCode.Created, "Dependence created")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun delete(){
        val dependsOn = call.parameters["dependsOn"]?.toInt()

        if(dependsOn != null){
            // Удаляемая привязанная задача
            val dependentOnTaskDTO = getTask(dependsOn)

            val dependence = getDependenceForDelete(dependsOn)
            // Зависимая задача
            val dependentTaskDTO = dependence?.let { getTask(it.dependent) }
            // Вычитания время выполнения задачи
            dependentTaskDTO?.scope = dependentTaskDTO?.scope!! - dependentOnTaskDTO?.scope!!

            if(dependentTaskDTO?.id != null) {
                // Обновление
                updateTask(dependentTaskDTO.id!!, dependentTaskDTO)
                val projectId = getParentId(dependentTaskDTO?.id!!)
                // Перерасчет графа проекта
                recalculationScore(projectId, dependentTaskDTO?.generation!!)
            }

            deleteDependence(dependsOn)
            call.respond(HttpStatusCode.OK, "Dependence deleted")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }
}