package com.example.database.Dependence

import kotlinx.serialization.Serializable

@Serializable
data class Dependence (
    val dependsOn: Int = 0,
    val dependent: Int = 0,
)