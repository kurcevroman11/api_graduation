package com.example.database.man_hours

import com.example.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.format.DateTimeFormat

object ManHoursModel : Table("man_hours") {
    val id = integer("id").autoIncrement()
    val createdAt = datetime("created_at").nullable()
    val hoursSpent = text("hours_spent")
    val comment = text("comment").nullable()
    val taskId = integer("taskid").nullable()
    val projectId = integer("projectid").nullable()
    val activityId = integer("activityid").nullable()

    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSSZZ")

    private fun resultRowToNode(row: ResultRow) = ManHoursDTO(
        id = row[ManHoursModel.id],
        created_at = row[ManHoursModel.createdAt].toString(),
        hours_spent = row[ManHoursModel.hoursSpent],
        comment = row[ManHoursModel.comment],
        taskid = row[ManHoursModel.taskId],
        projectid = row[ManHoursModel.projectId],
        activityid = row[ManHoursModel.activityId]
    )

    suspend fun insert(manHoursDTO: ManHoursDTO) = dbQuery {
        ManHoursModel.insert {
            manHoursDTO.created_at?.let { created_at ->
                it[ManHoursModel.createdAt] = formatter.parseDateTime(created_at)
            }
            manHoursDTO.hours_spent?.let { hoursSpent -> it[ManHoursModel.hoursSpent] = hoursSpent }
            manHoursDTO.comment?.let { comment -> it[ManHoursModel.comment] = comment }
            manHoursDTO.taskid?.let { taskid -> it[ManHoursModel.taskId] = taskid }
            manHoursDTO.projectid?.let { projectid -> it[ManHoursModel.projectId] = projectid }
            manHoursDTO.activityid?.let { activityid -> it[ManHoursModel.activityId] = activityid }
        }
    }

    suspend fun update(manHoursDTO: ManHoursDTO, taskId: Int) = dbQuery {
        ManHoursModel.update({ ManHoursModel.id eq taskId }) {
            manHoursDTO.created_at?.let { created_at ->
                it[ManHoursModel.createdAt] = formatter.parseDateTime(created_at)
            }
            manHoursDTO.hours_spent?.let { hoursSpent -> it[ManHoursModel.hoursSpent] = hoursSpent }
            manHoursDTO.comment?.let { comment -> it[ManHoursModel.comment] = comment }
            manHoursDTO.taskid?.let { taskid -> it[ManHoursModel.taskId] = taskid }
            manHoursDTO.projectid?.let { projectid -> it[ManHoursModel.projectId] = projectid }
            manHoursDTO.activityid?.let { activityid -> it[ManHoursModel.activityId] = activityid }
        }
    }

    suspend fun fetchById(taskId: Int): List<ManHoursDTO> = dbQuery {
        ManHoursModel
            .select { ManHoursModel.taskId.eq(taskId) }
            .map(::resultRowToNode)
    }

    suspend fun fetchSpecific(manHoursId: Int): ManHoursDTO = dbQuery {
        ManHoursModel
            .select { ManHoursModel.id.eq(manHoursId) }
            .map(::resultRowToNode).single()
    }

    suspend fun delete(manHoursId: Int) {
        dbQuery {
            ManHoursModel.deleteWhere { ManHoursModel.id eq manHoursId }
        }
    }

    suspend fun deleteByTask(taskId: Int) {
        dbQuery {
            ManHoursModel.deleteWhere { ManHoursModel.taskId eq taskId }
        }
    }
}


