package com.example.database.activity

import com.example.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

object ActivityModel: Table("activity") {
    private val id = ActivityModel.integer("id").autoIncrement()
    private val name = ActivityModel.varchar("name", 64)

    private fun resultRowToNode(row: ResultRow) = ActivityDTO(
        id = row[ActivityModel.id],
        name = row[ActivityModel.name],
    )

    suspend fun fetchAllActivity(): List<ActivityDTO> = dbQuery {
        ActivityModel
            .selectAll()
            .map(::resultRowToNode)
    }
}