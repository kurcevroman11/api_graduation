package com.example.database.Dependence

import kotlinx.serialization.Serializable

@Serializable
data class Dependence (
    val dependsOn: MutableList<Int> = mutableListOf(),
    val dependent: Int = 0,
)