package com.example.db.Description

import com.example.database.file.FileDTO
import com.example.database.file.FileForTask.insertAndGetId
import com.example.database.file.FileModel
import com.example.database.file.FileModel.getFile
import com.example.database.file.FileModel.getFileInTask
import com.example.db.Description.DescriptionModel.deleteDescription
import com.example.db.Description.DescriptionModel.getDescription
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.io.File

const val MAX_FILE_SIZE = 1048576 * 20 // 20МБ

@Serializable
data class DescriptionDTOFileDTO(
    val id:Int?,
    val file_resources: List<FileDTO>?
)

private val logger = KotlinLogging.logger {}
fun Application.DescriptionContriller() {
    routing {
        route("/description") {
            get("/{id}") {
                val descriptionId = call.parameters["id"]?.toIntOrNull()

                if (descriptionId != null) {
                    val description = getDescription(descriptionId)
                    if (description != null) {
                        val list = getFileInTask(description.id!!)

                        val descriptionString = DescriptionDTOFileDTO(descriptionId, list)
                        call.respond(HttpStatusCode.OK, descriptionString)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "нет такого пользователя")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }

            var fileDescription = ""
            var fileName = ""

            get("/download/{id}/{fileId}") {
                val descriptionId = call.parameters["id"]?.toIntOrNull()
                val fileId = call.parameters["fileId"]?.toIntOrNull()

                if (descriptionId != null) {
                    val descriptionDTO = getDescription(descriptionId)
                    if (fileId != null) {
                        val fileDTO = getFile(fileId)
                        val imege = File(descriptionDTO.file_resources!! + fileId +".${fileDTO!!.type}")

                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(
                                ContentDisposition.Parameters.FileName,
                                "${fileDTO!!.orig_filename}.${fileDTO.type}"
                            )
                                .toString()
                        )

                        call.respondFile(imege)
                    }else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }

            post("/upload/{id}") {
                val descriptionId = call.parameters["id"]?.toIntOrNull()
                // Получаем размер файла
                val contentLength = call.request.header(HttpHeaders.ContentLength).toString().toInt()
                if (descriptionId != null) {
                    val descriptionDTO = getDescription(descriptionId)
                    if (contentLength!! < MAX_FILE_SIZE) {
                        val multipartData = call.receiveMultipart()

                        multipartData.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    // Узнаем имя файла
                                    fileDescription = part.value
                                }

                                is PartData.FileItem -> {
                                    fileName = part.originalFileName as String

                                    val name = fileName.substringBeforeLast(".")
                                    val extension = fileName.substringAfterLast(".")

                                    logger.info { "Фото: $fileName, Name:$name, Type: $extension" }

                                    val fileDTO = FileDTO(null, name , descriptionDTO.id, extension)

                                    // Создание записи с в таблице file
                                    val fileNameServer = insertAndGetId(fileDTO)

                                    // Создаем папку, если она не существует
                                    val directory = File(descriptionDTO.file_resources)
                                    if (!directory.exists()) {
                                        directory.mkdirs()
                                    }

                                    // Создание файла
                                    val fileBytes = part.streamProvider().readBytes()
                                    File(descriptionDTO.file_resources + fileNameServer+".${extension}").writeBytes(fileBytes)
                                }
                                else -> {}
                            }
                            part.dispose()
                        }
                        call.respondText("$fileDescription is uploaded to 'uploads/$fileName'")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Сильно большой файл!")
                    }

                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }

            delete("/{id}/{fileid}") {
                val descriptionId = call.parameters["id"]?.toIntOrNull()
                val fileId = call.parameters["fileid"]?.toIntOrNull()
                if (descriptionId != null && fileId != null) {
                    call.respond(FileModel.deleteSpecificFile(descriptionId, fileId), "Delete")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format.")
                }
            }
        }
    }
}