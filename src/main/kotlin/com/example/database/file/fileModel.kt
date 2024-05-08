package com.example.database.file

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.http.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger

object FileModel : Table("file") {

    private val id = FileModel.integer("id").autoIncrement()
    private val descriptionId = FileModel.integer("descriptionid").nullable()
    private val orig_filename = FileModel.text("orig_filename").nullable()
    private val type = FileModel.text("type").nullable()

    // Функция получения описания по descriptionId
    fun getFileInTask(id: Int): List<FileDTO>? {
        return try {
            transaction {
                FileModel.select { descriptionId.eq(id) }.map {
                    FileDTO(
                    it[FileModel.id],
                    it[orig_filename],
                    it[descriptionId],
                    it[type])  }
            }
        } catch (e: Exception) {
           ArrayList<FileDTO>()
        }
    }

    fun getFile(id: Int): FileDTO? {
        return try {
            transaction {
                val fileModel = FileModel.select { FileModel.id.eq(id) }.single()
                FileDTO(
                    id = fileModel[FileModel.id],
                    orig_filename = fileModel[orig_filename],
                    descriptionId = fileModel[descriptionId],
                    type = fileModel[type])

            }
        } catch (e: Exception) {
            FileDTO(null,null,null,null)
        }
    }

    fun deleteFile(id: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = FileModel.deleteWhere { FileModel.descriptionId eq id }
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

    fun deleteSpecificFile(descrptionId: Int, fileId: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = FileModel.deleteWhere { FileModel.descriptionId eq descrptionId and (FileModel.id eq fileId)}
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
}

object FileForTask: IdTable<Long>("file") {
    override val id: Column<EntityID<Long>> = FileForTask.long("id").autoIncrement().entityId()

    private val orig_filename = FileForTask.text("orig_filename").nullable()
    private val descriptionId = FileForTask.integer("descriptionid").nullable()
    private val type = FileForTask.text("type").nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    fun insertAndGetId(fileDTO: FileDTO): Long {
        var newFileId: Long = 0
        transaction {
            addLogger(StdOutSqlLogger)

            newFileId = FileForTask.insertAndGetId {
                it[orig_filename] = fileDTO.orig_filename
                it[descriptionId] = fileDTO.descriptionId
                it[type] = fileDTO.type
            }.value
        }
        return newFileId
    }
}