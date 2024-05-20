package com.example.db.Description

import com.example.database.Description.DescriptionForTask
import com.example.database.file.FileModel
import com.example.db.Task.TaskModel
import com.example.plugins.createMedia
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.*
object DescriptionModel: Table("description") {

    private val id = DescriptionModel.integer("id").autoIncrement()
    private val task_id = DescriptionModel.integer("task_id").references(TaskModel.id)
    private val file_resources = DescriptionModel.text("file_resources").nullable()


    fun insert(taskId: Int) {
        try {
            transaction {
                addLogger(StdOutSqlLogger)
                DescriptionModel.insert {
                    it[task_id] = taskId
                    it[file_resources] = createMedia(taskId.toString())
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    fun getDescription(id: Int): DescriptionDTO {
        lateinit var descriptionDTO: DescriptionDTO

        transaction {
            val descriptionModel = DescriptionModel.select { DescriptionModel.task_id.eq(id) }.single()
            descriptionDTO = DescriptionDTO(
                id = descriptionModel[DescriptionModel.id],
                file_resources = descriptionModel[file_resources],
                taskId = descriptionModel[task_id]
                )
        }
        return descriptionDTO
    }

    fun deleteDescription(id: Int?): HttpStatusCode {
        if (id != null) {
            transaction {
                val description = getDescription(id)
                deleteFolder(description.file_resources)
                val deletedRowCount = DescriptionModel.deleteWhere { DescriptionModel.task_id eq id }
                if (deletedRowCount > 0) {
                    return@transaction HttpStatusCode.NoContent
                } else {
                    return@transaction HttpStatusCode.NoContent
                }
            }
        } else {
            return HttpStatusCode.BadRequest
        }
        return HttpStatusCode.OK
    }

    fun removeLastFolderFromPath(path: String): String {
        val folderSeparator = path.lastIndexOf(File.separator)
        return if (folderSeparator != -1) {
            path.substring(0, folderSeparator)
        } else {
            // Вернуть исходный путь, если разделитель не найден
            path
        }
    }

    fun deleteFolder(folderPath: String?) {
        if (folderPath != null) {
            val folder = File(removeLastFolderFromPath(folderPath))
            if (folder.exists()) {
                val files = folder.listFiles()
                if (files != null) {
                    for (file in files) {
                        if (file.isDirectory) {
                            deleteFolder(file.absolutePath)
                        } else {
                            file.delete()
                        }
                    }
                }
                folder.delete()
            }
        }
    }
}
