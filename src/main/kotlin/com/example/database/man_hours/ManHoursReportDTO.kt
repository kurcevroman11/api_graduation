package com.example.database.man_hours

import kotlinx.serialization.Serializable

@Serializable
data class ManHoursReportDTO(
    val id: Int,
    val createdAt: String?,
    val hoursSpent: String?,
    val taskId: Int,
    val taskName: String
)