package com.example.database.Dependence

import com.example.dao.DatabaseFactory.dbQuery
import com.example.db.Task.TaskModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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

}