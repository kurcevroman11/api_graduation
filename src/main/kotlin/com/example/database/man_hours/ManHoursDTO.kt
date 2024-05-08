package com.example.database.man_hours

import kotlinx.serialization.Serializable

@Serializable
class ManHoursDTO (
    val id: Int ? = null,
    val created_at: String? = null,
    var hours_spent: String? = null,
    val comment: String? = null,
    var taskid: Int? = null,
    var projectid: Int? = null,
    val activityid: Int? = null,
)