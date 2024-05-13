package com.example.database.Dependence

import com.example.database.Dependence.DependenceModel.addDependence
import com.example.database.Dependence.DependenceModel.deleteDependence
import com.example.database.Dependence.DependenceModel.getAllDependences
import com.example.database.Dependence.DependenceModel.getDependenceForDelete
import com.example.database.Dependence.DependenceModel.getDependences
import com.example.db.Task.TaskDTO
import com.example.db.Task.TaskModel.getParentId
import com.example.db.Task.TaskModel.getTask
import com.example.db.Task.TaskModel.recalculationScore
import com.example.db.Task.TaskModel.recalculationScoreWithDependence
import com.example.db.Task.TaskModel.recalculationScoreWithDependenceForDelete
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

            // Проверка нет ли у зависимой задачи ее зависимая задача если так
            // то нужно сделать вычитание у зависимой задачи чтобы получить ее начальное значение
            // Извлечение связки
            var dependentId2 = getDependenceForDelete(dependenceDTO.dependent)
            if(dependentId2 != null) {
                var dependenceOnTaskDTO2 = getTask(dependentId2.dependsOn!!)
                var dependentTaskDTO2 = getTask(dependentId2.dependent!!)

                dependentTaskDTO2?.scope = dependentTaskDTO2?.scope!! - dependenceOnTaskDTO2?.scope!!
                updateTask(dependentTaskDTO2.id!!, dependentTaskDTO2)
            }

            // Извленчение задачи от которой зависит dependentTaskDTO
            val dependsOnTaskDTO: TaskDTO? = getTask(dependenceDTO.dependsOn)

            // Суммирование время выполнения задачи
            dependentTaskDTO?.scope = dependsOnTaskDTO?.scope!! + dependentTaskDTO?.scope!!
            // Обновление
            updateTask(dependentTaskDTO.id!!, dependentTaskDTO)

            // Перерачсчт если зависимая задача имела зависимости для других задач
            if(dependentTaskDTO?.id != null) {
                var dependentId2 = getDependenceForDelete(dependenceDTO.dependent)
                while(dependentId2 != null) {
                    var dependentTaskDTO2 = getTask(dependentId2.dependent!!)
                    dependentTaskDTO2?.scope = dependentTaskDTO2?.scope!! + dependentTaskDTO?.scope!!
                    // Обновление
                    updateTask(dependentTaskDTO2.id!!, dependentTaskDTO2)

                    dependentId2 = getDependenceForDelete(dependentId2.dependent)
                }
                recalculationScoreWithDependence()
            }
            call.respond(HttpStatusCode.Created, "Dependence created")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }

    suspend fun delete(){
        val dependsOn = call.parameters["dependsOn"]?.toInt()

        if(dependsOn != null){
            val dependence = getDependenceForDelete(dependsOn)

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

            val dependentTaskDTO = dependence?.let { getTask(it.dependent) }
            val projectId = getParentId(dependentTaskDTO?.id!!)

            deleteDependence(dependsOn)
            recalculationScore(projectId, dependentTaskDTO?.generation!!)
            call.respond(HttpStatusCode.OK, "Dependence deleted")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        }
    }
}