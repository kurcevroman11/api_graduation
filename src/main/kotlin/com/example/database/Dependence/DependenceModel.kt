package com.example.database.Dependence

import com.example.dao.DatabaseFactory.dbQuery
import com.example.db.Task.TaskModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object DependenceModel : Table("dependence") {
    val dependsOn = integer("depends_on").references(TaskModel.id) // Внешний ключ к таблице "task"
    val dependent = integer("dependent").references(TaskModel.id)


    private fun resultRowToNode(row: ResultRow) = Dependence(
        dependsOn = mutableListOf(row[DependenceModel.dependsOn]),
        dependent = row[DependenceModel.dependent],
    )

    suspend fun addDependence(dependence: Dependence) = dbQuery {
        dependence.dependsOn.forEach { dependsOn ->
            DependenceModel.insert {
                it[DependenceModel.dependsOn] = dependsOn
                it[DependenceModel.dependent] = dependence.dependent
            }
        }
    }

    suspend fun getAllDependences(dependsOn: Int): List<Dependence> = dbQuery {
        DependenceModel
            .select { DependenceModel.dependsOn.eq(dependsOn) }
            .map(::resultRowToNode)
    }

    suspend fun deleteDependence(dependsOn: Int) {
        dbQuery {
            DependenceModel.deleteWhere { DependenceModel.dependsOn eq dependsOn }
        }
    }

}