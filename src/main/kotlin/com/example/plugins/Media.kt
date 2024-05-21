package com.example.plugins

import com.example.db.Description.DescriptionDTO
import java.io.File

fun createMedia(taskId: String): String {
    val filePath = "src/main/resources/media/${taskId}/"

    // Создаем папку, если она не существует
    val directory = File(filePath)
    if (!directory.exists()) {
        directory.mkdirs()
    }

    return filePath
}
