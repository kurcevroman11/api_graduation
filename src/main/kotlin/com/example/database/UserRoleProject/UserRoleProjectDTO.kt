package com.example.database.UserRoleProject
import kotlinx.serialization.Serializable


@Serializable
class UserRoleProjectDTO(
    val id: Int? = null,
    var userid: MutableList<Int> = mutableListOf(),
    var projectid: Int? = null,
    val type_of_activityid: Int? = null,
    val score: Int? = null,
    val current_task_id: Int? = null,
    val creater_project: Int? = null
)


