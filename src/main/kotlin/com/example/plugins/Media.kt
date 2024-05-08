package com.example.plugins

import com.example.database.Description.DescriptionForTask
import com.example.db.Description.DescriptionDTO
import java.io.File

fun createMedia(name:String): Long {
    val filePath = "src/main/resources/media/${name}/"

    // Создаем папку, если она не существует
    val directory = File(filePath)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val discritionID = DescriptionForTask.insertandGetId(DescriptionDTO(null, null, filePath))

    return discritionID
}
