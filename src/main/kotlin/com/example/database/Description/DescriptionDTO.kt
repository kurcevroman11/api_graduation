package com.example.db.Description

import kotlinx.serialization.Serializable

@Serializable
class DescriptionDTO(
    val id:Int?,
    var content:String?,
    var file_resources: String?
)