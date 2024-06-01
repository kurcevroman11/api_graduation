package com.example.database.Dependence

import com.example.database.Dependence.DependenceModel.addDependence
import com.example.database.Dependence.DependenceModel.deleteDependence
import com.example.database.Dependence.DependenceModel.getDependenceForDelete
import com.example.database.Dependence.DependenceModel.recalculationDependence
import com.example.db.Task.TaskModel.getParentId
import com.example.db.Task.TaskModel.getTask
import com.example.db.Task.TaskModel.recalculationScore
import com.example.db.Task.TaskModel.recalculationScoreWithDependence
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
            recalculationDependence(
                dependent = dependenceDTO.dependent,
                dependenceDTO = dependenceDTO
            )
            call.respond(HttpStatusCode.Created, "Dependence created")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun delete(){
        val dependsOn = call.parameters["dependsOn"]?.toInt()

        if(dependsOn != null){
            // Получаем связку с зависимой задаче
            var dependence = getDependenceForDelete(dependsOn)

            if(dependence != null) {
                // Получаем зависимую задачу
                var dependentTaskDTO2 = getTask(dependence?.dependent!!)
                // Проверка имеет ли зависимая задача зависимость для другой задачи
                // Перерачсчт если зависимая задача имела зависимости для других задач
                var dependentId2 = getDependenceForDelete(dependentTaskDTO2?.id!!)
                while(dependentId2 != null) {
                    // Получаем другую зависимую задачу
                    var dependentTaskDTO3 = getTask(dependentId2.dependent!!)

                    dependentTaskDTO3?.scope = dependentTaskDTO3?.scope!! - dependentTaskDTO2?.scope!!

                    // Обновление
                    updateTask(dependentTaskDTO3.id!!, dependentTaskDTO3)

                    dependentId2 = getDependenceForDelete(dependentTaskDTO3.id!!)
                }
                recalculationScoreWithDependence()

                // Удаляемая привязанная задача от которой есть зависимость
                var dependentOnTaskDTO = getTask(dependence.dependsOn!!)
                dependentTaskDTO2?.scope = dependentTaskDTO2?.scope!! - dependentOnTaskDTO?.scope!!
                // Обновление
                updateTask(dependentTaskDTO2.id!!, dependentTaskDTO2)

                //<---------------------------------------------------------------------------------------------->

                // Теперь нужно сделать пересуммирование зависимых задач
                // Проверка имеет ли зависимая задача зависимость для другой задачи
                // Перерачсчт если зависимая задача имела зависимости для других задач
                dependentId2 = getDependenceForDelete(dependentTaskDTO2?.id!!)
                while(dependentId2 != null) {
                    // Получаем другую зависимую задачу
                    var dependentTaskDTO3 = getTask(dependentId2.dependent!!)

                    dependentTaskDTO3?.scope = dependentTaskDTO3?.scope!! + dependentTaskDTO2?.scope!!

                    // Обновление
                    updateTask(dependentTaskDTO3.id!!, dependentTaskDTO3)

                    dependentId2 = getDependenceForDelete(dependentTaskDTO3.id!!)
                }
                recalculationScoreWithDependence()

                val dependentTaskDTO = getTask(dependence.dependent)
                val projectId = getParentId(dependentTaskDTO?.id!!)

                deleteDependence(dependsOn)
                recalculationScore(projectId, dependentTaskDTO?.generation!!)
                call.respond(HttpStatusCode.OK, "Dependence deleted")
            } else {
                val dependentOnTaskDTO = getTask(dependence?.dependsOn!!)
                val dependentTaskDTO = getTask(dependence?.dependent!!)
                dependentTaskDTO?.scope = dependentTaskDTO?.scope!! - dependentOnTaskDTO?.scope!!
                // Обновление
                updateTask(dependentTaskDTO.id!!, dependentTaskDTO)

                val projectId = getParentId(dependentTaskDTO?.id!!)
                deleteDependence(dependsOn)
                recalculationScore(projectId, dependentTaskDTO?.generation!!)
                call.respond(HttpStatusCode.OK, "Dependence deleted")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }
}