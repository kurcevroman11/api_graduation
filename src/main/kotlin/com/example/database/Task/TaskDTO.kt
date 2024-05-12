package com.example.db.Task

import com.example.database.Task.TaskDependenceOn
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import java.time.LocalTime

@Serializable
class TaskDTO(
    var id: Int?,
    var name: String?,
    var status: Int?,
    val start_date: String?,
    var scope: Int?,
    var description: Int?,
    var parent: Int?,
    var userCount: Int? = null,
    var generation : Int? = null,
    var content: String? = null,
    var typeofactivityid: Int?,
    var position: Int?,
    val taskDependenceOn: TaskDependenceOn? = null
)
{
    constructor() : this(
        id = null,
        name = null,
        status = null,
        start_date = null,
        scope = null,
        description = null,
        parent = null,
        userCount = null,
        generation = null,
        content = null,
        typeofactivityid = null,
        position = null,
    )
}
