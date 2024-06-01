package com.example.database.Dependence

import com.example.dao.DatabaseFactory.dbQuery
import com.example.db.Task.TaskDTO
import com.example.db.Task.TaskModel
import com.example.db.Task.TaskModel.getTask
import com.example.db.Task.TaskModel.recalculationScoreWithDependence
import com.example.db.Task.TaskModel.updateTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object DependenceModel : Table("dependence") {
    val dependsOn = integer("depends_on").references(TaskModel.id) // Внешний ключ к таблице "task"
    val dependent = integer("dependent").references(TaskModel.id)

    private fun resultRowToNode(row: ResultRow) = Dependence(
        dependsOn = row[DependenceModel.dependsOn],
        dependent = row[DependenceModel.dependent],
    )

    suspend fun addDependence(dependence: Dependence) = dbQuery {
        DependenceModel.insert {
            it[DependenceModel.dependsOn] = dependence.dependsOn
            it[DependenceModel.dependent] = dependence.dependent
        }
    }

    suspend fun getAllDependences(): List<Dependence> = dbQuery {
        DependenceModel
            .selectAll()
            .map(::resultRowToNode)
    }


    suspend fun getDependences(dependent: Int): Dependence? = dbQuery {
        DependenceModel
            .select { DependenceModel.dependent.eq(dependent) }
            .map(::resultRowToNode).singleOrNull()
    }

    // Получение id задачи от которой зависит другая зависимая задача
    fun getDependencesInt(dependent: Int): Int? {
        return try {
            transaction {
                DependenceModel.select {
                    DependenceModel.dependent eq dependent
                }.singleOrNull()?.get(DependenceModel.dependsOn) ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }

    // Функция для получения списка зависимостей для заданного dependentOn
    suspend fun getDependencies(dependentOn: Int): List<Dependence> = dbQuery {
        DependenceModel
            .select { DependenceModel.dependsOn eq dependentOn }
            .map(::resultRowToNode)
    }

    // Рекурсивная функция для получения полной цепочки зависимостей
    suspend fun getDependenceForDeleteRecurse(dependentOn: Int): List<Dependence> = withContext(Dispatchers.IO) {
        val initialDependence = getDependencies(dependentOn)

        val allDependencies = mutableListOf<Dependence>()
        for (dependence in initialDependence) {
            allDependencies.add(dependence)
            allDependencies.addAll(getDependenceForDeleteRecurse(dependence.dependent))
        }
        return@withContext allDependencies
    }

    suspend fun getDependenceForDelete(dependentOn: Int): Dependence? = dbQuery {
        DependenceModel
            .select { DependenceModel.dependsOn.eq(dependentOn) }
            .map(::resultRowToNode).singleOrNull()
    }

    suspend fun deleteDependence(dependsOn: Int) {
        dbQuery {
            DependenceModel.deleteWhere { DependenceModel.dependsOn eq dependsOn }
        }
    }


    suspend fun recalculationDependence(dependent: Int, dependenceDTO: Dependence, updateTask: TaskDTO? = null) {
        var dependentTaskDTO = getTask(dependent!!)

        // Проверка нет ли у зависимой задачи ее зависимая задача если так
        // то нужно сделать вычитание у зависимой задачи чтобы получить ее начальное значение

        // Извленчение задачи от которой зависит dependentTaskDTO
        var dependsOnTaskDTO = getTask(dependenceDTO.dependsOn!!)
        if(dependenceDTO != null) {
            // Вычитание задачи до обновление задачи от которой зависим
            // Перерачсчт если зависимая задача имела зависимости для других задач
            if(dependentTaskDTO?.id != null) {
                if(updateTask != null) {

                    // Перерасчет если задача имеал зависимость для другой задачи
                    var dependentId = getDependenceForDelete(updateTask.id!!)

                    if(dependentId != null) {
                        val dependentTaskDTO = getTask(dependentId?.dependent!!)
                        val dependsOnTaskDTO = getTask(dependentId?.dependsOn!!)
                        if (dependentTaskDTO != null && dependsOnTaskDTO != null) {
                            var dependencies = mutableListOf<Dependence>()
                            while(dependentId != null) {
                                dependentId = getDependenceForDelete(dependentId.dependent)
                                if(dependentId != null) {
                                    dependencies.add(dependentId)
                                }
                            }

                            dependencies.forEach { dependence ->
                                val dependentTaskDTO = getTask(dependence.dependent)
                                val dependsOnTaskDTO = getTask(dependence.dependsOn)
                                if (dependentTaskDTO != null && dependsOnTaskDTO != null) {
                                    dependentTaskDTO.scope = dependentTaskDTO.scope?.minus(dependsOnTaskDTO.scope ?: 0)
                                    // Обновление
                                    updateTask(dependentTaskDTO.id!!, dependentTaskDTO)
                                }
                            }

                            // В последнию очередь обновляешь верхушку цепи
                            dependentTaskDTO.scope = dependentTaskDTO.scope?.minus(updateTask.scope ?: 0)
                            // Обновление
                            updateTask(dependentTaskDTO.id!!, dependentTaskDTO)
                        }
                    }
                } else {
                    var dependentId2 = getDependenceForDelete(dependenceDTO.dependent)

                    while(dependentId2 != null) {
                        var dependentTaskDTO2 = getTask(dependentId2.dependent!!)

                        dependentTaskDTO2?.scope = dependentTaskDTO2?.scope!! - dependentTaskDTO?.scope!!
                        // Обновление
                        updateTask(dependentTaskDTO2.id!!, dependentTaskDTO2)

                        dependentId2 = getDependenceForDelete(dependentId2.dependent)
                    }
                }
                recalculationScoreWithDependence()
            }
        }

        // Затем у зависимой задачи прибавляем время
        dependsOnTaskDTO = getTask(dependenceDTO.dependsOn!!)
        dependentTaskDTO = getTask(dependent!!)
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
    }
}